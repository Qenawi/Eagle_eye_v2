package com.example.eagle_eye_v2.screens.camera

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Call
import androidx.compose.material.icons.sharp.Face
import androidx.compose.material.icons.sharp.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Color.Companion
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import com.example.eagle_eye_v2.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.graphics.Color as CColor
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import androidx.camera.core.Preview as cameraPreview

sealed class CameraUIAction {
  object OnCameraClick : CameraUIAction()
  object OnGalleryViewClick : CameraUIAction()
  object OnSwitchCameraClick : CameraUIAction()
}


@Composable
fun CameraView(onImageCaptured: (Uri, Boolean) -> Unit, onError: (ImageCaptureException) -> Unit) {

  val context = LocalContext.current
  var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_FRONT) }
  val imageCapture: ImageCapture = remember {
    ImageCapture.Builder().build()
  }
  val galleryLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    if (uri != null) onImageCaptured(uri, true)
  }

  CameraPreviewView(
    imageCapture,
    lensFacing
  ) { cameraUIAction ->
    when (cameraUIAction) {
      is CameraUIAction.OnCameraClick -> {
        imageCapture.takePicture(context, lensFacing, onImageCaptured, onError)
      }
      is CameraUIAction.OnSwitchCameraClick -> {
        lensFacing =
          if (lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT
          else
            CameraSelector.LENS_FACING_BACK
      }
      is CameraUIAction.OnGalleryViewClick -> {
        if (true == context.getOutputDirectory().listFiles()?.isNotEmpty()) {
          galleryLauncher.launch("image/*")
        }
      }
    }
  }
}

@SuppressLint("RestrictedApi")
@Composable
private fun CameraPreviewView(
  imageCapture: ImageCapture,
  lensFacing: Int = CameraSelector.LENS_FACING_FRONT,
  cameraUIAction: (CameraUIAction) -> Unit
) {

  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  val preview = cameraPreview.Builder().build()
  val cameraSelector = CameraSelector.Builder()
    .requireLensFacing(lensFacing)
    .build()

  val previewView = remember { PreviewView(context) }
  LaunchedEffect(lensFacing) {
    val cameraProvider = context.getCameraProvider()
    cameraProvider.unbindAll()
    cameraProvider.bindToLifecycle(
      lifecycleOwner,
      cameraSelector,
      preview,
      imageCapture
    )
    preview.setSurfaceProvider(previewView.surfaceProvider)
  }

  Box(modifier = Modifier.fillMaxSize()) {
    AndroidView({ previewView }, modifier = Modifier.fillMaxSize()) {

    }
    Column(
      modifier = Modifier.align(Alignment.BottomCenter),
      verticalArrangement = Arrangement.Bottom
    ) {
      CameraControls(cameraUIAction)
    }

  }
}










@Composable
fun CameraControls(cameraUIAction: (CameraUIAction) -> Unit) {

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(androidx.compose.ui.graphics.Color.Black)
      .padding(16.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {

    CameraControl(
      Icons.Sharp.Face,
      R.string.app_name,
      modifier= Modifier.size(64.dp),
      onClick = { cameraUIAction(CameraUIAction.OnSwitchCameraClick) }
    )

    CameraControl(
      Icons.Sharp.Person,
      R.string.app_name,
      modifier= Modifier
        .size(64.dp)
        .padding(1.dp).border(1.dp, CColor.White, CircleShape),
      onClick = { cameraUIAction(CameraUIAction.OnCameraClick) }
    )

    CameraControl(
      Icons.Sharp.Call,
      R.string.app_name,
      modifier= Modifier.size(64.dp),
      onClick = { cameraUIAction(CameraUIAction.OnGalleryViewClick) }
    )

  }
}


@Composable
fun CameraControl(
  imageVector: ImageVector,
  contentDescId: Int,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {


  IconButton(
    onClick = onClick,
    modifier = modifier
  ) {
    Icon(
      imageVector,
      contentDescription = stringResource(id = contentDescId),
      modifier = modifier,
      tint = CColor.White
    )
  }

}
















suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
  ProcessCameraProvider.getInstance(this).also { cameraProvider ->
    cameraProvider.addListener({
      continuation.resume(cameraProvider.get())
    }, ContextCompat.getMainExecutor(this))
  }
}



private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
private const val PHOTO_EXTENSION = ".jpg"


fun ImageCapture.takePicture(
  context: Context,
  lensFacing: Int,
  onImageCaptured: (Uri, Boolean) -> Unit,
  onError: (ImageCaptureException) -> Unit
) {
  val outputDirectory = context.getOutputDirectory()
  // Create output file to hold the image
  val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)
  val outputFileOptions = getOutputFileOptions(lensFacing, photoFile)

  this.takePicture(
    outputFileOptions,
    Executors.newSingleThreadExecutor(),
    object : ImageCapture.OnImageSavedCallback {
      override fun onImageSaved(output: ImageCapture.OutputFileResults) {
        val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
        // If the folder selected is an external media directory, this is
        // unnecessary but otherwise other apps will not be able to access our
        // images unless we scan them using [MediaScannerConnection]
        val mimeType = MimeTypeMap.getSingleton()
          .getMimeTypeFromExtension(savedUri.toFile().extension)
        MediaScannerConnection.scanFile(
          context,
          arrayOf(savedUri.toFile().absolutePath),
          arrayOf(mimeType)
        ) { _, uri ->

        }
        onImageCaptured(savedUri, false)
      }
      override fun onError(exception: ImageCaptureException) {
        onError(exception)
      }
    })
}


fun getOutputFileOptions(
  lensFacing: Int,
  photoFile: File
): ImageCapture.OutputFileOptions {

  // Setup image capture metadata
  val metadata = ImageCapture.Metadata().apply {
    // Mirror image when using the front camera
    isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
  }
  // Create output options object which contains file + metadata
  val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
    .setMetadata(metadata)
    .build()

  return outputOptions
}

fun createFile(baseFolder: File, format: String, extension: String) =
  File(
    baseFolder, SimpleDateFormat(format, Locale.US)
    .format(System.currentTimeMillis()) + extension
  )


fun Context.getOutputDirectory(): File {
  val mediaDir = this.externalMediaDirs.firstOrNull()?.let {
    File(it, this.resources.getString(R.string.app_name)).apply { mkdirs() }
  }
  return if (mediaDir != null && mediaDir.exists())
    mediaDir else this.filesDir
}