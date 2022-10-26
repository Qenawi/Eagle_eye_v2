package com.example.eagle_eye_v2

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.eagle_eye_v2.screens.camera.CameraView
import com.example.eagle_eye_v2.ui.theme.Eagle_Eye_v2Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      Eagle_Eye_v2Theme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
          ScreenMain()
        }
      }
    }
  }
}

sealed class NavRoutes(val route: String) {
  object Home : NavRoutes("home")
  object DataCollection : NavRoutes("dc")
}

@Preview
@Composable
fun ScreenMain() {
  val  scope = CoroutineScope(Dispatchers.Default)
  CameraView(onImageCaptured = { uri, fromGallery ->
    Log.d(TAG, "Image Uri Captured from Camera View")

  }, onError = { imageCaptureException ->
     scope.launch {
       Log.d(TAG, "Image Uri Captured from Camera View")
     }
  })
}