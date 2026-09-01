package app.lawnchair.pulse.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Non-secret configuration for one AI provider. The actual API key is NEVER
 * stored here — it lives only in [app.lawnchair.pulse.assistant.SecureKeyStore],
 * which is backed by Android Keystore-encrypted SharedPreferences. This row
 * only remembers which model/base URL to use and whether a key has been set,
 * so the Room database (which is not encrypted at rest) never sees the secret.
 */
@Entity(tableName = "pulse_ai_providers")
data class AiProviderSetting(
    @PrimaryKey val providerId: String,
    val model: String,
    val baseUrlOverride: String? = null,
    val hasApiKey: Boolean = false,
)
