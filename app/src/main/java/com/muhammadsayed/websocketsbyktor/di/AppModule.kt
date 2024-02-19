package com.muhammadsayed.websocketsbyktor.di

import com.muhammadsayed.websocketsbyktor.data.KtorRealtimeMessagingClient
import com.muhammadsayed.websocketsbyktor.data.RealtimeMessagingClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(Logging)
            install(WebSockets)
        }
    }

    @Singleton
    @Provides
    fun provideRealtimeMessagingClient(httpClient: HttpClient): RealtimeMessagingClient {
        return KtorRealtimeMessagingClient(httpClient)
    }
}