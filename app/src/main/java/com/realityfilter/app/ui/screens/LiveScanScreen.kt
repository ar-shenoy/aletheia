package com.realityfilter.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.realityfilter.app.domain.model.Observation
import com.realityfilter.app.domain.model.Pattern
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong

@Composable
fun LiveScanScreen(
    patterns: List<Pattern>,
    onFrameAnalyzed: (String) -> List<Observation>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var overlayText by remember { mutableStateOf("") }
    var observations by remember { mutableStateOf<List<Observation>>(emptyList()) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (hasPermission) {
            val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
            val lastRunMs = remember { AtomicLong(0L) }
            val recognizer = remember {
                TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            }

            DisposableEffect(Unit) {
                onDispose {
                    cameraExecutor.shutdown()
                    recognizer.close()
                }
            }

            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }
                        val analysis = ImageAnalysis.Builder()
                            .setTargetResolution(Size(1280, 720))
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()

                        analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                            val now = System.currentTimeMillis()
                            if (now - lastRunMs.get() < 1200L) {
                                imageProxy.close()
                                return@setAnalyzer
                            }
                            lastRunMs.set(now)
                            val mediaImage = imageProxy.image
                            if (mediaImage != null) {
                                val image = InputImage.fromMediaImage(
                                    mediaImage,
                                    imageProxy.imageInfo.rotationDegrees
                                )
                                recognizer.process(image)
                                    .addOnSuccessListener { visionText ->
                                        val text = visionText.text.orEmpty()
                                        if (text.isNotBlank()) {
                                            overlayText = text.take(400)
                                            observations = onFrameAnalyzed(text)
                                        }
                                    }
                                    .addOnCompleteListener { imageProxy.close() }
                            } else {
                                imageProxy.close()
                            }
                        }

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                analysis
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.72f))
                    .padding(12.dp)
            ) {
                Text(text = "Live overlay", style = MaterialTheme.typography.labelLarge, color = Color.White.copy(alpha = 0.7f))
                if (overlayText.isNotBlank()) {
                    Text(
                        text = overlayText.take(160) + if (overlayText.length > 160) "\u2026" else "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 3
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (observations.isEmpty()) {
                    Text(
                        text = "Point at text \u2014 patterns appear here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                } else {
                    observations.take(4).forEach { obs ->
                        val name = patterns.find { it.id == obs.patternId }?.name ?: obs.patternId
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f))
                        ) {
                            Text(
                                text = "$name \u00b7 ${(obs.confidence * 100).toInt()}%",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Camera permission required for live overlay", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
