package com.muhammadsayed.websocketsbyktor.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammadsayed.websocketsbyktor.data.MessageEvent
import com.muhammadsayed.websocketsbyktor.data.MessagesState
import com.muhammadsayed.websocketsbyktor.data.RealtimeMessagingClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.ConnectException
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val client: RealtimeMessagingClient
) : ViewModel() {

    var messages = mutableListOf<MessagesState>()

    val state = client
        .getStateStream()
        .onStart { _isConnecting.value = true }
        .onEach { _isConnecting.value = false }
        .catch { t ->
            _isConnecting.value = false
            _showConnectionError.value = t is ConnectException
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), String())

    private val _isConnecting = MutableStateFlow(false)
    val isConnecting = _isConnecting.asStateFlow()

    private val _showConnectionError = MutableStateFlow(false)
    val showConnectionError = _showConnectionError.asStateFlow()


    fun sendMessage(messageEvent: MessageEvent) {
        viewModelScope.launch {
            client.sendAction(messageEvent)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            client.close()
        }
    }
}