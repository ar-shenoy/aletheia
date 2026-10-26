package com.realityfilter.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.realityfilter.app.data.local.RealityFilterDatabase
import com.realityfilter.app.data.repository.ObservationRepository
import com.realityfilter.app.data.repository.PatternRepository
import com.realityfilter.app.domain.model.BiasProfile
import com.realityfilter.app.domain.model.Observation
import com.realityfilter.app.domain.model.Pattern
import com.realityfilter.app.domain.model.SourceType
import com.realityfilter.app.ml.PatternMatcher
import com.realityfilter.app.ml.SpeechHelper
import com.realityfilter.app.ml.TextRecognizerHelper
import com.realityfilter.app.ui.screens.BiasMirrorScreen
import com.realityfilter.app.ui.screens.ConversationScreen
import com.realityfilter.app.ui.screens.HomeScreen
import com.realityfilter.app.ui.screens.PatternDetailScreen
import com.realityfilter.app.ui.screens.PatternsScreen
import com.realityfilter.app.ui.theme.AletheiaTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private var sharedText by mutableStateOf<String?>(null)
    private var speechHelper: SpeechHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIncomingIntent(intent)

        val database = RealityFilterDatabase.getInstance(this)
        val patternRepository = PatternRepository(database.patternDao(), this)
        val observationRepository = ObservationRepository(
            database.observationDao(),
            database.analysisSessionDao()
        )
        val matcher = PatternMatcher()
        val textRecognizer = TextRecognizerHelper(this)

        lifecycleScope.launch {
            patternRepository.seedPatternsIfEmpty()
        }

        setContent {
            AletheiaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val patterns by patternRepository.getAllPatterns()
                        .collectAsState(initial = emptyList())
                    val bookmarks by observationRepository.getBookmarkedObservations()
                        .collectAsState(initial = emptyList())

                    var selectedTab by remember { mutableIntStateOf(0) }
                    var biasProfile by remember { mutableStateOf(BiasProfile()) }
                    var ocrText by remember { mutableStateOf<String?>(null) }
                    var isOcrRunning by remember { mutableStateOf(false) }
                    var selectedPattern by remember { mutableStateOf<Pattern?>(null) }
                    var isListening by remember { mutableStateOf(false) }
                    var transcript by remember { mutableStateOf("") }
                    var conversationObs by remember { mutableStateOf<List<Observation>>(emptyList()) }
                    val scope = rememberCoroutineScope()

                    LaunchedEffect(selectedTab, patterns) {
                        if (selectedTab == 2) {
                            biasProfile = withContext(Dispatchers.IO) {
                                observationRepository.buildBiasProfile(patterns)
                            }
                        }
                    }

                    val imagePicker = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) { uri: Uri? ->
                        if (uri != null) {
                            isOcrRunning = true
                            scope.launch {
                                try {
                                    val text = withContext(Dispatchers.IO) {
                                        textRecognizer.recognizeFromUri(this@MainActivity, uri)
                                    }
                                    ocrText = text
                                    selectedTab = 0
                                } catch (_: Exception) {
                                    ocrText = null
                                } finally {
                                    isOcrRunning = false
                                }
                            }
                        }
                    }

                    val micPermission = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { granted ->
                        if (granted) {
                            isListening = true
                            startSpeech { partial, finalText, _ ->
                                if (partial != null) transcript = partial
                                if (finalText != null) {
                                    transcript = if (transcript.isBlank()) finalText else "$transcript $finalText"
                                    isListening = false
                                }
                            }
                        }
                    }

                    if (selectedPattern != null) {
                        PatternDetailScreen(
                            pattern = selectedPattern!!,
                            onBack = { selectedPattern = null }
                        )
                    } else {
                        Scaffold(
                            bottomBar = {
                                NavigationBar {
                                    NavigationBarItem(
                                        selected = selectedTab == 0,
                                        onClick = { selectedTab = 0 },
                                        icon = { Icon(Icons.Default.Home, contentDescription = "Analyze") },
                                        label = { Text("Analyze") }
                                    )
                                    NavigationBarItem(
                                        selected = selectedTab == 1,
                                        onClick = { selectedTab = 1 },
                                        icon = { Icon(Icons.Default.List, contentDescription = "Patterns") },
                                        label = { Text("Patterns") }
                                    )
                                    NavigationBarItem(
                                        selected = selectedTab == 2,
                                        onClick = { selectedTab = 2 },
                                        icon = { Icon(Icons.Default.Psychology, contentDescription = "Mirror") },
                                        label = { Text("Mirror") }
                                    )
                                    NavigationBarItem(
                                        selected = selectedTab == 3,
                                        onClick = { selectedTab = 3 },
                                        icon = { Icon(Icons.Default.Mic, contentDescription = "Talk") },
                                        label = { Text("Talk") }
                                    )
                                }
                            }
                        ) { padding ->
                            when (selectedTab) {
                                0 -> HomeScreen(
                                    patterns = patterns,
                                    initialText = sharedText ?: ocrText,
                                    isOcrRunning = isOcrRunning,
                                    onAnalyze = { text ->
                                        val results = matcher.analyze(text, patterns)
                                        scope.launch(Dispatchers.IO) {
                                            observationRepository.saveAnalysis(
                                                observations = results,
                                                sourceType = when {
                                                    ocrText != null -> SourceType.CAMERA_OCR
                                                    sharedText != null -> SourceType.SHARE_SHEET
                                                    else -> SourceType.MANUAL_TEXT
                                                },
                                                sourceText = text
                                            )
                                        }
                                        results
                                    },
                                    onToggleBookmark = { obs ->
                                        scope.launch(Dispatchers.IO) {
                                            observationRepository.toggleBookmark(obs)
                                        }
                                    },
                                    onPickImage = { imagePicker.launch("image/*") },
                                    modifier = Modifier.padding(padding)
                                )
                                1 -> PatternsScreen(
                                    patterns = patterns,
                                    onPatternClick = { selectedPattern = it },
                                    modifier = Modifier.padding(padding)
                                )
                                2 -> BiasMirrorScreen(
                                    profile = biasProfile,
                                    bookmarkedObservations = bookmarks,
                                    patterns = patterns,
                                    modifier = Modifier.padding(padding)
                                )
                                3 -> ConversationScreen(
                                    isListening = isListening,
                                    transcript = transcript,
                                    observations = conversationObs,
                                    patterns = patterns,
                                    onStartListening = {
                                        val granted = ContextCompat.checkSelfPermission(
                                            this@MainActivity, Manifest.permission.RECORD_AUDIO
                                        ) == PackageManager.PERMISSION_GRANTED
                                        if (granted) {
                                            isListening = true
                                            startSpeech { partial, finalText, _ ->
                                                if (partial != null) transcript = partial
                                                if (finalText != null) {
                                                    transcript = if (transcript.isBlank()) finalText else "$transcript $finalText"
                                                    isListening = false
                                                }
                                            }
                                        } else {
                                            micPermission.launch(Manifest.permission.RECORD_AUDIO)
                                        }
                                    },
                                    onStopListening = {
                                        isListening = false
                                        speechHelper?.stop()
                                    },
                                    onAnalyzeTranscript = {
                                        val results = matcher.analyze(transcript, patterns)
                                        conversationObs = results
                                        scope.launch(Dispatchers.IO) {
                                            observationRepository.saveAnalysis(
                                                observations = results,
                                                sourceType = SourceType.CONVERSATION,
                                                sourceText = transcript
                                            )
                                        }
                                    },
                                    modifier = Modifier.padding(padding)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startSpeech(callback: (String?, String?, String?) -> Unit) {
        speechHelper?.stop()
        speechHelper = SpeechHelper(
            context = this,
            onPartial = { callback(it, null, null) },
            onFinal = { callback(null, it, null) },
            onError = { callback(null, null, it) }
        )
        speechHelper?.start()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    override fun onDestroy() {
        speechHelper?.stop()
        super.onDestroy()
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        }
    }
}
