package com.muhammadsayed.websocketsbyktor.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.muhammadsayed.websocketsbyktor.data.MessageEvent
import com.muhammadsayed.websocketsbyktor.data.MessagesState
import com.muhammadsayed.websocketsbyktor.ui.theme.WebSocketsByKtorTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebSocketsByKtorTheme {
                val viewModel = hiltViewModel<ChatViewModel>()
                val state by viewModel.state.collectAsState()
                val isConnecting by viewModel.isConnecting.collectAsState()
                val showConnectionError by viewModel.showConnectionError.collectAsState()
                var textState by remember { mutableStateOf(TextFieldValue("")) }
                val keyboardController = LocalSoftwareKeyboardController.current


                runCatching {
                    Json.decodeFromString<MessagesState>(state)
                }.onSuccess {
                    viewModel.messages += MessagesState(it.message)
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Column {
                        if (isConnecting) {
                            Text(
                                text = "Connecting...", modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(), textAlign = TextAlign.Center
                            )
                        }

                        if (!isConnecting) {
                            Text(
                                text = "Connected", modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(), textAlign = TextAlign.Center
                            )
                        }

                        LazyColumn(Modifier.weight(1f)) {
                            itemsIndexed(viewModel.messages) { _, item ->
                                Text(
                                    text = item.message ?: "",
                                    modifier = Modifier.padding(16.dp),
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth()
                        ) {

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        shape = RoundedCornerShape(200.dp),
                                        color = Color.Gray.copy(alpha = 0.3f)
                                    )
                                    .padding(16.dp)
                            ) {
                                BasicTextField(
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                    keyboardActions = KeyboardActions(
                                        onSend = { keyboardController?.hide() }),
                                    modifier = Modifier.fillMaxWidth(),
                                    value = textState,
                                    onValueChange = {
                                        textState = it
                                    })
                                if (textState.text.isEmpty())
                                    Text(text = "Enter Message ...")

                            }
                            Spacer(modifier = Modifier.size(10.dp))
                            if (isConnecting) {
                                CircularProgressIndicator()
                            } else {
                                Button(onClick = {
                                    viewModel.sendMessage(
                                        MessageEvent(
                                            textState.text
                                        )
                                    )
                                    textState = TextFieldValue("")
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = null
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.size(10.dp))

                            if (showConnectionError) {
                                Text(
                                    text = "Connection Error",
                                    modifier = Modifier.padding(16.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }


                }
            }
        }
    }
}

