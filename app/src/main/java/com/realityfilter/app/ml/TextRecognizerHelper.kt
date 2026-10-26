package com.realityfilter.app.ml

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * On-device OCR via ML Kit Text Recognition.
 * Fully local after model availability.
 */
class TextRecognizerHelper(context: Context) {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun recognizeFromBitmap(bitmap: Bitmap): String {
        val image = InputImage.fromBitmap(bitmap, 0)
        return recognize(image)
    }

    suspend fun recognizeFromUri(context: Context, uri: Uri): String {
        val image = InputImage.fromFilePath(context, uri)
        return recognize(image)
    }

    private suspend fun recognize(image: InputImage): String =
        suspendCancellableCoroutine { cont ->
            recognizer.process(image)
                .addOnSuccessListener { result ->
                    cont.resume(result.text ?: "")
                }
                .addOnFailureListener { e ->
                    cont.resumeWithException(e)
                }
        }

    fun close() {
        recognizer.close()
    }
}
