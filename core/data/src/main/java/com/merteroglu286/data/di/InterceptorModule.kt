package com.merteroglu286.data.di

import com.merteroglu286.data.BuildConfig
import com.merteroglu286.data.connectivity.NetworkMonitorInterface
import com.merteroglu286.data.constants.AUTHENTICATION_INTERCEPTOR_TAG
import com.merteroglu286.data.constants.CLIENT_ID_TAG
import com.merteroglu286.data.constants.CONNECTIVITY_INTERCEPTOR_TAG
import com.merteroglu286.data.constants.DISPATCHER_IO_TAG
import com.merteroglu286.data.constants.HEADER_INTERCEPTOR_TAG
import com.merteroglu286.data.constants.LANGUAGE_TAG
import com.merteroglu286.data.constants.LOGGING_INTERCEPTOR_TAG
import com.merteroglu286.data.interceptors.AUTHORIZATION_HEADER
import com.merteroglu286.data.interceptors.AuthenticationInterceptor
import com.merteroglu286.data.interceptors.CLIENT_ID_HEADER
import com.merteroglu286.data.interceptors.ConnectivityInterceptor
import com.merteroglu286.data.interceptors.HeaderInterceptor
import com.merteroglu286.protodatastore.manager.session.SessionDataStoreInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import java.util.Locale
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class InterceptorModule {

    @Provides
    @Singleton
    @Named(HEADER_INTERCEPTOR_TAG)
    fun provideHeaderInterceptor(
        @Named(CLIENT_ID_TAG) clientId: String,
        @Named(LANGUAGE_TAG) languageProvider: () -> Locale
    ): Interceptor {
        return HeaderInterceptor(
            clientId = clientId,
            languageProvider = languageProvider
        )
    }

    @Provides
    @Singleton
    @Named(AUTHENTICATION_INTERCEPTOR_TAG)
    fun provideAuthenticationInterceptor(
        sessionDataStoreInterface: SessionDataStoreInterface,
        @Named(DISPATCHER_IO_TAG) coroutineDispatcher: CoroutineDispatcher
    ): Interceptor {
        return AuthenticationInterceptor(
            sessionDataStoreInterface = sessionDataStoreInterface,
            coroutineDispatcher = coroutineDispatcher
        )
    }

    @Provides
    @Singleton
    @Named(CONNECTIVITY_INTERCEPTOR_TAG)
    fun provideConnectivityInterceptor(
        networkMonitorInterface: NetworkMonitorInterface
    ): Interceptor {
        return ConnectivityInterceptor(
            networkMonitorInterface = networkMonitorInterface
        )
    }

    @Provides
    @Singleton
    @Named(LOGGING_INTERCEPTOR_TAG)
    fun provideOkHttpLoggingInterceptor(): Interceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }

        if (!BuildConfig.DEBUG) {
            interceptor.redactHeader(CLIENT_ID_HEADER)
            interceptor.redactHeader(AUTHORIZATION_HEADER)
        }

        return interceptor
    }

}