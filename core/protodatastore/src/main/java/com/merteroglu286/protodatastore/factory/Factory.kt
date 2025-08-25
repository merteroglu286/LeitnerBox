package com.merteroglu286.protodatastore.factory

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.merteroglu286.proto.Preferences
import com.merteroglu286.proto.Session
import com.merteroglu286.protodatastore.serializer.PreferencesSerializer
import com.merteroglu286.protodatastore.serializer.SessionSerializer

val Context.sessionDataStore: DataStore<Session> by dataStore(
    fileName = "session.pb",
    serializer = SessionSerializer
)

val Context.preferencesDataStore : DataStore<Preferences> by dataStore(
    fileName = "preferences.pb",
    serializer = PreferencesSerializer
)