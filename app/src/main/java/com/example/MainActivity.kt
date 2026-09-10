package com.example

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.LaunchedEffect
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.MyApplicationTheme
import com.example.utils.BiometricAuthManager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class MainActivity : FragmentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      // Defaulting to Dark Theme as requested
      var isDarkTheme by remember { mutableStateOf(true) }
      var isAuthenticated by remember { mutableStateOf(false) }
      var authError by remember { mutableStateOf<String?>(null) }

      LaunchedEffect(Unit) {
          BiometricAuthManager.authenticate(
              activity = this@MainActivity,
              onSuccess = { isAuthenticated = true },
              onError = { authError = it }
          )
      }

      MyApplicationTheme(darkTheme = isDarkTheme) {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          if (isAuthenticated) {
            AppNavigation(
              isDarkTheme = isDarkTheme,
              onThemeToggle = { isDarkTheme = !isDarkTheme }
            )
          } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (authError != null) {
                    Text("Erro de autenticação: $authError", color = MaterialTheme.colorScheme.error)
                } else {
                    Text("Autenticando...", color = MaterialTheme.colorScheme.onBackground)
                }
            }
          }
        }
      }
    }
  }
}
