package com.exolve.callbutton

import android.app.Application
import android.util.Log
import com.exolve.voicesdk.CallClient
import com.exolve.voicesdk.Communicator
import com.exolve.voicesdk.Configuration
import com.exolve.voicesdk.LogConfiguration
import com.exolve.voicesdk.LogLevel
import com.exolve.voicesdk.VersionInfo

private const val TELECOM_MANAGER = "TelecomManager"

class TelecomManager(
    private var context: Application,
    private val callButtonViewModel: CallButtonViewModel
) {

    private val configuration: Configuration = Configuration
        .builder(context)
        .logConfiguration(
            LogConfiguration.builder()
                .logLevel(LogLevel.DEBUG)
                .build()
        )
        .build()

    private var callClient: CallClient = Communicator
        .initializeForOutgoingCalls(context, configuration)
        .callClient

    init {
        Log.d(TELECOM_MANAGER, "init: callClient = $callClient")
        callClient.setCallsListener(
            CallsListener(
                context = context,
                callButtonViewModel = callButtonViewModel
            ), context.mainLooper
        )
    }

    fun getVersionDescription(): String {
        val versionInfo: VersionInfo = Communicator.getInstance().versionInfo
        return "SDK ver.${versionInfo.buildVersion}"
    }

    fun activateAccount(account: String, password: String) {
        Log.d(TELECOM_MANAGER, "activateAccount: $account")
        callClient.register(account, password)
    }

    fun deactivateAccount() {
        Log.d(TELECOM_MANAGER, "deactivateAccount")
        callClient.unregister()
    }

    fun call(number: String) {
        // In this demo app only one simultaneous call is supported.
        if (hasActiveCall()) {
            Log.w(TELECOM_MANAGER, "End the current call before placing a new one")
            return
        }
        Log.d(TELECOM_MANAGER, "call: number = $number")
        callClient.placeCall(number)
    }

    fun terminateCall() {
        Log.d(TELECOM_MANAGER, "terminateCall")
        callClient.calls.getOrNull(0)?.terminate()
    }

    fun hasActiveCall() : Boolean {
        return callClient.calls.isNotEmpty()
    }

}