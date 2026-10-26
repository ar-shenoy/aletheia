package com.realityfilter.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.realityfilter.app.data.local.RealityFilterDatabase
import com.realityfilter.app.data.repository.PatternRepository
import com.realityfilter.app.ml.PatternMatcher
import com.realityfilter.app.ui.screens.HomeScreen
import com.realityfilter.app.ui.screens.PatternsScreen
import com.realityfilter.app.ui.theme.AletheiaTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var sharedText by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIncomingIntent(intent)

        val database = RealityFilterDatabase.getInstance(this)
        val patternRepository = PatternRepository(database.patternDao(), this)
        val matcher = PatternMatcher()

        lifecycleScope.launch {
            patternRepository.seedPatternsIfEmpty()
        }

        setContent {
            AletheiaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val patterns by patternRepository.getAllPatterns()
                        .collectAsState(initial = emptyList())

                    var selectedTab by remember { mutableIntStateOf(0) }

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
                            }
                        }
                    ) { padding ->
                        when (selectedTab) {
                            0 -> HomeScreen(
                                patterns = patterns,
                                initialText = sharedText,
                                onAnalyze = { text -> matcher.analyze(text, patterns) },
                                modifier = Modifier.padding(padding)
                            )
                            1 -> PatternsScreen(
                                patterns = patterns,
                                modifier = Modifier.padding(padding)
                            )
                            2 -> BiasMirrorPlaceholder(
                                modifier = Modifier.padding(padding)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        }
    }
}

@Composable
private fun BiasMirrorPlaceholder(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Personal Bias Mirror",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Coming next.\n\nThis screen will show the patterns that appear most often in the content you choose to analyze — built entirely on-device from your own history. Nothing ever leaves the device.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}
