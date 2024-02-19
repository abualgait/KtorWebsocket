package com.muhammadsayed.websocketsbyktor.data

import kotlinx.serialization.Serializable

@Serializable
data class MessageEvent(val message: String)
