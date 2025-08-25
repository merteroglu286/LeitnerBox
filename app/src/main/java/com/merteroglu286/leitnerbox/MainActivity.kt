package com.merteroglu286.leitnerbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.merteroglu286.leitnerbox.ui.theme.LeitnerBoxTheme
import com.merteroglu286.protodatastore.manager.preferences.PreferencesDataStoreInterface
import com.merteroglu286.protodatastore.manager.session.SessionDataStoreInterface
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionDataStoreInterface

    @Inject
    lateinit var preferencesManager: PreferencesDataStoreInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            LeitnerBoxTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ProtoDataStoreTestScreen(
                        sessionManager = sessionManager,
                        preferencesManager = preferencesManager,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtoDataStoreTestScreen(
    sessionManager: SessionDataStoreInterface,
    preferencesManager: PreferencesDataStoreInterface,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    // Session Data States
    val accessToken by sessionManager.getAccessTokenFlow().collectAsState(initial = "")
    val refreshToken by sessionManager.getRefreshTokenFlow().collectAsState(initial = "")
    val userId by sessionManager.getUserIdFlow().collectAsState(initial = "")

    // Preferences Data States
    val language by preferencesManager.getLanguageFlow().collectAsState(initial = "")
    val isAppLockEnabled by preferencesManager.getIsAppLockEnableFlow().collectAsState(initial = false)
    val notificationCount by preferencesManager.getNotificationCountFlow().collectAsState(initial = 0)
    val moneyBalance by preferencesManager.getMoneyBalanceFlow().collectAsState(initial = 0L)

    // Input States
    var accessTokenInput by remember { mutableStateOf("") }
    var refreshTokenInput by remember { mutableStateOf("") }
    var userIdInput by remember { mutableStateOf("") }
    var languageInput by remember { mutableStateOf("") }
    var notificationCountInput by remember { mutableStateOf("") }
    var moneyBalanceInput by remember { mutableStateOf("") }

    // Language Dropdown
    var languageExpanded by remember { mutableStateOf(false) }
    val languages = listOf("tr", "en", "de", "fr", "es")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Proto DataStore Test",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Session Data Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "📱 Session Data",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text("Access Token: $accessToken")
                Text("Refresh Token: $refreshToken")
                Text("User ID: $userId")

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = accessTokenInput,
                    onValueChange = { accessTokenInput = it },
                    label = { Text("Access Token") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = refreshTokenInput,
                    onValueChange = { refreshTokenInput = it },
                    label = { Text("Refresh Token") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = userIdInput,
                    onValueChange = { userIdInput = it },
                    label = { Text("User ID") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                sessionManager.setSession(
                                    accessToken = accessTokenInput,
                                    refreshToken = refreshTokenInput,
                                    userID = userIdInput
                                )
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save Session")
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                sessionManager.setAccessToken("")
                                sessionManager.setRefreshToken("")
                                sessionManager.setUserId("")
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Clear Session")
                    }
                }
            }
        }

        // Preferences Data Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "⚙️ Preferences Data",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text("Language: $language")
                Text("App Lock: ${if (isAppLockEnabled) "Enabled" else "Disabled"}")
                Text("Notification Count: $notificationCount")
                Text("Money Balance: $moneyBalance TL")

                Spacer(modifier = Modifier.height(8.dp))

                // Language Dropdown
                ExposedDropdownMenuBox(
                    expanded = languageExpanded,
                    onExpandedChange = { languageExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = languageInput.ifEmpty { "Select Language" },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = languageExpanded,
                        onDismissRequest = { languageExpanded = false }
                    ) {
                        languages.forEach { lang ->
                            DropdownMenuItem(
                                text = { Text(lang.uppercase()) },
                                onClick = {
                                    languageInput = lang
                                    languageExpanded = false
                                    scope.launch {
                                        preferencesManager.setLanguage(lang)
                                    }
                                }
                            )
                        }
                    }
                }

                // App Lock Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("App Lock")
                    Switch(
                        checked = isAppLockEnabled,
                        onCheckedChange = { enabled ->
                            scope.launch {
                                preferencesManager.setIsAppLockEnable(enabled)
                            }
                        }
                    )
                }

                OutlinedTextField(
                    value = notificationCountInput,
                    onValueChange = { notificationCountInput = it },
                    label = { Text("Notification Count") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = moneyBalanceInput,
                    onValueChange = { moneyBalanceInput = it },
                    label = { Text("Money Balance") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                notificationCountInput.toIntOrNull()?.let { count ->
                                    preferencesManager.setNotificationCount(count)
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save Notification")
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                moneyBalanceInput.toLongOrNull()?.let { balance ->
                                    preferencesManager.setMoneyBalance(balance)
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save Balance")
                    }
                }

                Button(
                    onClick = {
                        scope.launch {
                            preferencesManager.setLanguage("tr")
                            preferencesManager.setIsAppLockEnable(false)
                            preferencesManager.setNotificationCount(0)
                            preferencesManager.setMoneyBalance(0L)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reset All Preferences")
                }
            }
        }

        // Quick Test Actions
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🚀 Quick Tests",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                sessionManager.setSession(
                                    accessToken = "sample_access_token_123",
                                    refreshToken = "sample_refresh_token_456",
                                    userID = "user_789"
                                )
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Sample Session")
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                preferencesManager.setLanguage("en")
                                preferencesManager.setIsAppLockEnable(true)
                                preferencesManager.setNotificationCount(5)
                                preferencesManager.setMoneyBalance(1250L)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Sample Prefs")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProtoDataStoreTestScreenPreview() {
    LeitnerBoxTheme {
        // Preview için mock objeler kullanılabilir
    }
}