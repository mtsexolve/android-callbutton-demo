package com.exolve.callbutton

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CallButtonViewModel(application: Application) : AndroidViewModel(application) {
    private val telecomManager = TelecomManager(application, this)

    companion object {
        private const val PREFS_NAME = "call_button_prefs"
    }

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var prefsAccount: String
        get() = sharedPreferences.getString("account", "") ?: ""
        set(value) {
            sharedPreferences.edit().putString("account", value).apply()
        }

    var prefsPassword: String
        get() = sharedPreferences.getString("password", "") ?: ""
        set(value) {
            sharedPreferences.edit().putString("password", value).apply()
        }

    var prefsNumber: String
        get() = sharedPreferences.getString("number", "") ?: ""
        set(value) {
            sharedPreferences.edit().putString("number", value).apply()
        }

    private val _uiState = MutableStateFlow(
        CallButtonState(
            prefsAccount,
            prefsPassword,
            prefsNumber,
            false,
            telecomManager.getVersionDescription()
        )
    )

    val uiState: StateFlow<CallButtonState> = _uiState.asStateFlow()

    fun updateAccount(account: String) {
        prefsAccount = account
        _uiState.update { currentState ->
            currentState.copy(
                account = account
            )
        }
    }

    fun updatePassword(password: String) {
        prefsPassword = password
        _uiState.update { currentState ->
            currentState.copy(
                password = password
            )
        }
    }

    fun updateNumber(number: String) {
        prefsNumber = number
        _uiState.update { currentState ->
            currentState.copy(
                number = number
            )
        }
    }

    fun toggleCall() {
        if (!telecomManager.hasActiveCall()) {
            /* To keep things simple we are passing credentials before every call and removing them when call ends.
                This allows us not to bother with checking if credentials were changed on the settings page.
                In your app consider activating only once.
             */
            telecomManager.activateAccount(_uiState.value.account, _uiState.value.password)
            telecomManager.call(_uiState.value.number)
        } else {
            telecomManager.terminateCall()
        }
    }

    fun updateState() {
        val hasActiveCall = telecomManager.hasActiveCall()
        _uiState.update { currentState ->
            currentState.copy(
                isCalling = hasActiveCall
            )
        }
        if (!hasActiveCall) {
            telecomManager.deactivateAccount()
        }
    }
}