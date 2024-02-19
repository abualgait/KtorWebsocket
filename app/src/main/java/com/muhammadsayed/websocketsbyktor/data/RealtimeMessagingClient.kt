package com.muhammadsayed.websocketsbyktor.data

import kotlinx.coroutines.flow.Flow

interface RealtimeMessagingClient {
    fun getStateStream(): Flow<String>
    suspend fun sendAction(action: MessageEvent)
    suspend fun close()
}