package com.merteroglu286.data.di

import com.merteroglu286.data.BuildConfig
import com.merteroglu286.data.constants.HEADER_INTERCEPTOR_TAG
import com.merteroglu286.data.constants.LOGGING_INTERCEPTOR_TAG
import com.merteroglu286.data.interceptors.AUTHORIZATION_HEADER
import com.merteroglu286.data.interceptors.CLIENT_ID_HEADER
import com.merteroglu286.data.interceptors.HeaderInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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