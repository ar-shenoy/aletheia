package com.realityfilter.app.ml

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.Locale

class SpeechHelper(
    private val context: Context,
    private val onPartial: (String) -> Unit,
    private val onFinal: (String) -> Unit,
    private val onError: (String) -> Unit,
    private val continuous: Boolean = false
) {
    private var recognizer: SpeechRecognizer? = null
    private var isRunning = false
    private val mainHandler = Handler(Looper.getMainLooper())

    fun isAvailable(): Boolean = SpeechRecognizer.isRecognitionAvailable(context)

    fun start() {
        if (isRunning) return
        if (!isAvailable()) {
            onError("Speech recognition not available on this device")
            return
        }
        beginListening()
    }

    private fun beginListening() {
        recognizer?.destroy()
        recognizer = SpeechRecognizer.createSpeechRecognizer(context).also { sr ->
            sr.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}

                override fun onError(error: Int) {
                    if (continuous && isRunning && error != SpeechRecognizer.ERROR_CLIENT) {
                        mainHandler.postDelayed({
                            if (isRunning) beginListening()
                        }, 400)
                    } else if (!continuous) {
                        isRunning = false
                        onError("Speech error code $error")
                    }
                }

                override fun onResults(results: Bundle?) {
                    val texts = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val best = texts?.firstOrNull().orEmpty()
                    if (best.isNotBlank()) onFinal(best)

                    if (continuous && isRunning) {
                        mainHandler.postDelayed({
                            if (isRunning) beginListening()
                        }, 300)
                    } else {
                        isRunning = false
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val texts = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val partial = texts?.firstOrNull().orEmpty()
                    if (partial.isNotBlank()) onPartial(partial)
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }

        isRunning = true
        recognizer?.startListening(intent)
    }

    fun stop() {
        isRunning = false
        mainHandler.removeCallbacksAndMessages(null)
        recognizer?.stopListening()
        recognizer?.cancel()
        recognizer?.destroy()
        recognizer = null
    }
}
