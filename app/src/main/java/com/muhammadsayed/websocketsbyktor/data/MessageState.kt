package com.muhammadsayed.websocketsbyktor.data

import kotlinx.serialization.Serializable

@Serializable
data class MessagesState(
    val message: String? = ""
)
