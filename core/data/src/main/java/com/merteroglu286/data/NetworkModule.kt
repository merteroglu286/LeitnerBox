package com.merteroglu286.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    @Named("Language")
    fun provideLanguage(): () -> Locale {
        return { Locale.ENGLISH}
    }

    @Provides
    @Singleton
    @Named("AccessToken")
    fun provideAccessToken(): () -> String? {
        return {""}
    }

    @Provides
    @Singleton
    @Named("ClientId")
    fun provideClientId():String {
        return ""
    }


    @Provides
    @Singleton
    @Named("HeaderInterceptor")
    fun provideHeaderInterceptor(
        @Named("ClientId") clientId: String,
        @Named("AccessToken") accessTokenProvider: () -> String?,
        @Named("Language") languageProvider: () -> Locale
    ): Interceptor {
        return HeaderInterceptor(
            clientId = clientId,
            accessTokenProvider = accessTokenProvider,
            languageProvider = languageProvider
        )
    }

    @Provides
    @Singleton
    @Named("OkHttpLoggingInterceptor")
    fun provideOkHttpLoggingInterceptor(): Interceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }

        if (!BuildConfig.DEBUG){
            interceptor.redactHeader(CLIENT_ID_HEADER)
            interceptor.redactHeader(AUTHORIZATION_HEADER)
        }

        return interceptor
    }

    @Provides
    @Singleton
    fun provideOkHttpCallFactory(interceptor: Interceptor): Call.Factory {
        return OkHttpClient.Builder().addInterceptor(interceptor)
            .retryOnConnectionFailure(true)
            .followRedirects(false)
            .followSslRedirects(false)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }
}