package app.lawnchair.pulse.privatespace

import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object PrivateSpaceManager {
    private val _isUnlocked = MutableStateFlow(false)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    fun isAppHidden(packageName: String, hiddenSet: Set<String>): Boolean {
        if (_isUnlocked.value) return false
        return hiddenSet.contains(packageName)
    }
    
    fun isComponentHidden(componentKey: String, hiddenSet: Set<String>): Boolean {
        if (_isUnlocked.value) return false
        return hiddenSet.contains(componentKey)
    }

    fun lock() {
        _isUnlocked.value = false
    }

    fun requestUnlock(activity: FragmentActivity, onSuccess: () -> Unit = {}) {
        if (_isUnlocked.value) {
            onSuccess()
            return
        }

        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(activity, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    _isUnlocked.value = true
                    onSuccess()
                    Toast.makeText(activity, "Private Space Unlocked", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(activity, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Private Space")
            .setSubtitle("Authenticate to view hidden apps")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
