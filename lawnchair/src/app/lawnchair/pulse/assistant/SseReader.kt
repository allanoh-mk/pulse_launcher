package app.lawnchair.pulse.assistant

import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.BufferedSource

object SseReader {
    private val client = OkHttpClient()

    fun stream(request: Request): Flow<String> = flow {
        val call = client.newCall(request)
        try {
            val response = call.execute()
            if (!response.isSuccessful) {
                emit("ERROR: HTTP ${response.code} ${response.message}")
                return@flow
            }
            val source: BufferedSource = response.body?.source() ?: return@flow
            while (!source.exhausted()) {
                val line = source.readUtf8Line()
                if (line != null && line.startsWith("data:")) {
                    val data = line.removePrefix("data:").trim()
                    if (data == "[DONE]") {
                        break
                    }
                    if (data.isNotBlank()) {
                        emit(data)
                    }
                }
            }
        } catch (e: IOException) {
            emit("ERROR: ${e.message}")
        } finally {
            call.cancel()
        }
    }.flowOn(Dispatchers.IO)
}
