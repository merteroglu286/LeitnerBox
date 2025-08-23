package com.merteroglu286.leitnerbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.merteroglu286.datastore.settings.AppSettings
import com.merteroglu286.datastore.settings.AppSettingsSerializer
import com.merteroglu286.leitnerbox.ui.theme.LeitnerBoxTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import androidx.compose.runtime.getValue
import com.merteroglu286.datastore.settings.Language
import com.merteroglu286.datastore.settings.Location
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    lateinit var appSettingsDataStore : DataStore<AppSettings>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appSettingsDataStore = DataStoreFactory.create(
            serializer = AppSettingsSerializer(),
            produceFile = {dataStoreFile("app_settings.json")},
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )

        enableEdgeToEdge()
        setContent {
            LeitnerBoxTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SettingScreen(
                        appSettingsDataStore = appSettingsDataStore,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SettingScreen(
    appSettingsDataStore: DataStore<AppSettings>,
    modifier: Modifier
){
    val scope = rememberCoroutineScope()
    val appSettings by appSettingsDataStore.data.collectAsState(initial = AppSettings())

    Column(modifier = Modifier.padding(50.dp)) {
        Text(text = "Language" + appSettings.language)

        Text(text = "Last known locations:")
        appSettings.lastKnownLocations.forEach{ location ->
            Spacer(modifier = Modifier.padding(16.dp))
            Text(text = "Lat: ${location.lat} Long: ${location.long}")
        }
        Spacer(modifier = Modifier.padding(16.dp))

        val newLocation = Location(37.123, 122.908)

        Language.values().forEach { language ->
            DropdownMenuItem(
                text = {Text(text = language.name)},
                onClick = {
                    scope.launch {
                        appSettingsDataStore.updateData { currentSettings ->
                            currentSettings.copy(language = language,
                                lastKnownLocations = currentSettings.lastKnownLocations.add(
                                    newLocation
                                ))
                        }
                    }
                }
            )
        }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LeitnerBoxTheme {
        Greeting("Android")
    }
}