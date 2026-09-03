package app.lawnchair.pulse.data.repositories

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import app.lawnchair.pulse.core.PulsePreferences
import com.patrykmichalik.opto.core.firstBlocking
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Repository for weather data from Open-Meteo (free tier, no API key required).
 * Resolves location using IP-based coarse location by default, with a precise GPS fallback.
 * Caches responses in DataStore for 30 minutes.
 */
class WeatherRepository(private val context: Context) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    private val Context.weatherDataStore by preferencesDataStore(name = "weather_cache")

    private val lastUpdatedKey = longPreferencesKey("last_updated")
    private val weatherDataKey = stringPreferencesKey("weather_data")

    @Serializable
    data class WeatherData(
        val temperature: Double,
        val weatherCode: Int,
        val isDay: Boolean,
        val city: String = "Unknown"
    )

    suspend fun getWeather(): WeatherData = withContext(Dispatchers.IO) {
        val cached = getCachedWeather()
        if (cached != null && System.currentTimeMillis() - cached.first < 30 * 60 * 1000) {
            return@withContext cached.second
        }

        val location = resolveLocation()
        return@withContext fetchAndCacheWeather(location.first, location.second, location.third)
    }

    private suspend fun resolveLocation(): Triple<Double, Double, String> {
        val usePrecise = PulsePreferences.getInstance(context).weatherUsePreciseLocation.firstBlocking()
        
        if (usePrecise) {
            val gpsLoc = getGpsLocation()
            if (gpsLoc != null) {
                return gpsLoc
            }
        }
        
        return getIpLocation()
    }

    private fun getGpsLocation(): Triple<Double, Double, String>? {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null
        
        var location: Location? = null
        val providers = locationManager.getProviders(true)
        for (provider in providers) {
            val l = locationManager.getLastKnownLocation(provider) ?: continue
            if (location == null || l.accuracy < location.accuracy) {
                location = l
            }
        }

        if (location != null) {
            return Triple(location.latitude, location.longitude, "Local")
        }
        return null
    }

    private fun getIpLocation(): Triple<Double, Double, String> {
        return try {
            val request = Request.Builder().url("http://ip-api.com/json/").build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string() ?: throw Exception("Empty response")
                val lat = body.substringAfter("\"lat\":").substringBefore(",").toDouble()
                val lon = body.substringAfter("\"lon\":").substringBefore(",").toDouble()
                val city = body.substringAfter("\"city\":\"").substringBefore("\"")
                Triple(lat, lon, city)
            } else {
                Triple(40.7128, -74.0060, "New York") // Default fallback
            }
        } catch (e: Exception) {
            Triple(40.7128, -74.0060, "New York") // Default fallback
        }
    }

    private suspend fun getCachedWeather(): Pair<Long, WeatherData>? {
        val prefs = context.weatherDataStore.data.first()
        val lastUpdated = prefs[lastUpdatedKey] ?: return null
        val weatherDataJson = prefs[weatherDataKey] ?: return null

        return try {
            Pair(lastUpdated, Json.decodeFromString(weatherDataJson))
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun fetchAndCacheWeather(latitude: Double, longitude: Double, city: String): WeatherData {
        val url = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current_weather=true"
        val request = Request.Builder().url(url).build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw Exception("Failed to fetch weather: ".plus(response.message))
        }

        val body = response.body?.string() ?: throw Exception("Empty response")
        val weatherData = parseWeatherResponse(body, city)

        context.weatherDataStore.edit {
            it[lastUpdatedKey] = System.currentTimeMillis()
            it[weatherDataKey] = Json.encodeToString(weatherData)
        }

        return weatherData
    }

    private fun parseWeatherResponse(response: String, city: String): WeatherData {
        val temperature = response.substringAfter("\"temperature\":").substringBefore(",").toDouble()
        val weatherCode = response.substringAfter("\"weathercode\":").substringBefore(",").toInt()
        val isDay = response.substringAfter("\"is_day\":").substringBefore(",").toInt() == 1

        return WeatherData(temperature, weatherCode, isDay, city)
    }
}
