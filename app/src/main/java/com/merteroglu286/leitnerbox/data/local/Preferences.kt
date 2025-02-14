package com.merteroglu286.leitnerbox.data.local

interface Preferences {

    fun saveAuthorizationToken(authorizationToken: String)
    fun getAuthorizationToken(): String?

    fun saveUserId(id: String)
    fun getUserId(): String?

    fun saveLanguage(language:String)
    fun getLanguage(): String

}