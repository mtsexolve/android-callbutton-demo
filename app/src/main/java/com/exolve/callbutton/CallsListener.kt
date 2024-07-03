package com.exolve.callbutton

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.exolve.voicesdk.Call
import com.exolve.voicesdk.CallError
import com.exolve.voicesdk.CallPendingEvent
import com.exolve.voicesdk.CallUserAction
import com.exolve.voicesdk.ICallsListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


private const val CALLS_LISTENER = "CallsListener"

class CallsListener(
    private val context: Context,
    private val callButtonViewModel: CallButtonViewModel
) : ICallsListener {

    override fun callNew(p0: Call?) {
        callButtonViewModel.updateState()
    }

    override fun callConnected(p0: Call?) {
        callButtonViewModel.updateState()
    }

    override fun callHold(p0: Call?) {
    }

    override fun callResumed(p0: Call?) {
    }

    override fun callDisconnected(p0: Call?) {
        Log.d(CALLS_LISTENER, "callDisconnected(). Call: ${p0?.id},")
        callButtonViewModel.updateState()
    }

    override fun callError(p0: Call?, p1: CallError?, p2: String?) {
        Log.d(CALLS_LISTENER, "callError(). Call: ${p0?.id}, error: ${p1?.toString()}, errorDescription: $p2")
        callButtonViewModel.updateState()
        CoroutineScope(Dispatchers.IO).launch {
            if (p0 != null && p1 != null && p2 != null) {
                if(p2.isNotEmpty()){
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(
                            context,
                            "Call error: $p2",
                            Toast.LENGTH_SHORT)
                            .show();
                    }
                }
            }
        }
    }

    override fun callUserActionRequired(
        call: Call?,
        pendingEvent: CallPendingEvent?,
        action: CallUserAction?
    ) {
    }

    override fun callInConference(p0: Call?, p1: Boolean) {
    }

    override fun callMuted(p0: Call) {
    }

    override fun callConnectionLost(p0: Call) {
    }
}

