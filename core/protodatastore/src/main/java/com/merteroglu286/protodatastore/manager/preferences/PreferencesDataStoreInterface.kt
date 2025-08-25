package com.merteroglu286.protodatastore.manager.preferences

import kotlinx.coroutines.flow.Flow

interface PreferencesDataStoreInterface {

    // setter functions

    suspend fun setLanguage(language: String)
    suspend fun setIsAppLockEnable(isAppLockEnabled: Boolean)
    suspend fun setNotificationCount(notificationCount: Int)
    suspend fun setMoneyBalance(moneyBalance: Long)


    // getters

    suspend fun getLanguage(): String
    fun getLanguageFlow(): Flow<String>

    suspend fun getIsAppLockEnable(): Boolean
    fun getIsAppLockEnableFlow(): Flow<Boolean>

    suspend fun getNotificationCount(): Int
    fun getNotificationCountFlow(): Flow<Int>

    suspend fun getMoneyBalance(): Long
    fun getMoneyBalanceFlow(): Flow<Long>

}