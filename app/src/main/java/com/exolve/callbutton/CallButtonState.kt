package com.exolve.callbutton

data class CallButtonState(
    val account: String,
    val password: String,
    val number: String,
    val isCalling: Boolean,
    val versionInfo: String
)