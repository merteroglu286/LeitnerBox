package com.merteroglu286.auth.data.di

import com.google.gson.Gson
import com.merteroglu286.auth.data.service.LoginService
import com.merteroglu286.auth.data.source.LoginRemote
import com.merteroglu286.auth.data.source.LoginRemoteImpl
import com.merteroglu286.auth.domain.mapper.LoginMapper
import com.merteroglu286.auth.domain.mapper.LoginMapperImpl
import com.merteroglu286.data.connectivity.NetworkMonitorInterface
import com.merteroglu286.data.constants.DISPATCHER_DEFAULT_TAG
import com.merteroglu286.data.constants.USER_ID_TAG
import com.merteroglu286.data.factory.ServiceFactory
import com.merteroglu286.data.source.NetworkDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideLoginServiceFactory(serviceFactory: ServiceFactory): LoginService {
        return serviceFactory.create(LoginService::class.java)
    }

    @Provides
    @Singleton
    fun provideNetworkDataSource(
        loginService: LoginService,
        gson: Gson,
        networkMonitorInterface: NetworkMonitorInterface,
        @Named(USER_ID_TAG) userIdProvider: () -> String
    ): NetworkDataSource<LoginService> {
        return NetworkDataSource(loginService, gson, networkMonitorInterface, userIdProvider)
    }

    @Provides
    @Singleton
    fun provideLoginMapper(
        @Named(DISPATCHER_DEFAULT_TAG) coroutineDispatcher: CoroutineDispatcher
    ): LoginMapper {
        return LoginMapperImpl(coroutineDispatcher)
    }

    @Provides
    @Singleton
    fun provideLoginRemoteImpl(
        networkDataSource: NetworkDataSource<LoginService>,
        loginMapper: LoginMapper
    ): LoginRemote {
        return LoginRemoteImpl(networkDataSource, loginMapper)
    }

}