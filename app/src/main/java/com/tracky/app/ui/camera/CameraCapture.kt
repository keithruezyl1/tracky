package com.tracky.app.ui.camera

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import java.io.ByteArrayOutputStream
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FlipCameraAndroid
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.tracky.app.ui.components.TrackyBodyText
import com.tracky.app.ui.components.TrackyButtonPrimary
import com.tracky.app.ui.components.TrackyCard
import com.tracky.app.ui.theme.TrackyTokens
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Camera capture screen for food photos
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraCaptureScreen(
    onImageCaptured: (String) -> Unit, // Base64 encoded image
    onGallerySelected: (Uri) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    
    // Gallery picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onGallerySelected(it) }
    }

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TrackyTokens.Colors.Neutral900)
    ) {
        if (cameraPermissionState.status.isGranted) {
            // Camera preview
            CameraPreview(
                lensFacing = lensFacing,
                onImageCaptureReady = { imageCapture = it },
                modifier = Modifier.fillMaxSize()
            )

            // Top bar with close button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(TrackyTokens.Spacing.M),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(TrackyTokens.Colors.Neutral900.copy(alpha = 0.5f))
                ) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = TrackyTokens.Colors.Neutral0
                    )
                }
            }

            // Bottom controls
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(TrackyTokens.Spacing.L),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Gallery button
                    IconButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(TrackyTokens.Colors.Neutral900.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            Icons.Outlined.PhotoLibrary,
                            contentDescription = "Gallery",
                            tint = TrackyTokens.Colors.Neutral0
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Capture button
                    FloatingActionButton(
                        onClick = {
                            imageCapture?.let { capture ->
                                captureImage(
                                    context = context,
                                    imageCapture = capture,
                                    executor = ContextCompat.getMainExecutor(context),
                                    onImageCaptured = onImageCaptured,
                                    onError = { /* Handle error */ }
                                )
                            }
                        },
                        containerColor = TrackyTokens.Colors.Neutral0,
                        contentColor = TrackyTokens.Colors.BrandPrimary,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Icon(
                            Icons.Outlined.CameraAlt,
                            contentDescription = "Capture",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Flip camera button
                    IconButton(
                        onClick = {
                            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                CameraSelector.LENS_FACING_FRONT
                            } else {
                                CameraSelector.LENS_FACING_BACK
                            }
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(TrackyTokens.Colors.Neutral900.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            Icons.Outlined.FlipCameraAndroid,
                            contentDescription = "Flip Camera",
                            tint = TrackyTokens.Colors.Neutral0
                        )
                    }
                }
            }
        } else {
            // Permission not granted
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(TrackyTokens.Spacing.L),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                TrackyCard {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(TrackyTokens.Spacing.M)
                    ) {
                        TrackyBodyText(
                            text = if (cameraPermissionState.status.shouldShowRationale) {
                                "Camera permission is needed to take photos of your meals"
                            } else {
                                "Camera permission denied. Please enable it in settings."
                            },
                            color = TrackyTokens.Colors.TextPrimary
                        )
                        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
                        TrackyButtonPrimary(
                            text = "Grant Permission",
                            onClick = { cameraPermissionState.launchPermissionRequest() }
                        )
                        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))
                        TrackyButtonPrimary(
                            text = "Choose from Gallery",
                            onClick = { galleryLauncher.launch("image/*") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CameraPreview(
    lensFacing: Int,
    onImageCaptureReady: (ImageCapture) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember { PreviewView(context) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    DisposableEffect(lensFacing) {
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
            onImageCaptureReady(imageCapture)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        onDispose {
            cameraProvider.unbindAll()
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )
}

private fun captureImage(
    context: Context,
    imageCapture: ImageCapture,
    executor: Executor,
    onImageCaptured: (String) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val photoFile = File(
        context.cacheDir,
        SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis()) + ".jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                // Convert to base64 with compression
                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                val base64 = processBitmap(bitmap)
                onImageCaptured(base64)
                
                // Clean up temp file
                photoFile.delete()
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        }
    )
}

/**
 * Convert URI to base64 string
 */
fun uriToBase64(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        bitmap?.let { processBitmap(it) }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun processBitmap(bitmap: Bitmap): String {
    val maxDimension = 1024
    val scale = if (bitmap.width > maxDimension || bitmap.height > maxDimension) {
        maxDimension.toFloat() / maxOf(bitmap.width, bitmap.height)
    } else {
        1f
    }
    
    val scaledBitmap = if (scale < 1f) {
        val newWidth = (bitmap.width * scale).toInt()
        val newHeight = (bitmap.height * scale).toInt()
        Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    } else {
        bitmap
    }
    
    val outputStream = ByteArrayOutputStream()
    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
    return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
}
