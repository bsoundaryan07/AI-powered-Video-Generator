package com.example

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesomeMotion
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.AutoAwesomeMotion
import androidx.compose.material.icons.outlined.CloudQueue
import androidx.compose.material.icons.outlined.MovieCreation
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.CloudAndWebStudioScreen
import com.example.ui.screens.StoryboardAndHistoryScreen
import com.example.ui.screens.StudioWorkspaceScreen
import com.example.ui.screens.VoiceLipSyncScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.StudioDestination
import com.example.viewmodel.StudioViewModel
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = false, dynamicColor = false) {
                CineMotionStudioApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CineMotionStudioApp(
    viewModel: StudioViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val savedProjects by viewModel.savedProjects.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Return to STUDIO tab when Back is pressed from secondary tabs
    if (uiState.currentTab != StudioDestination.STUDIO) {
        BackHandler {
            viewModel.selectTab(StudioDestination.STUDIO)
        }
    }

    // English Speech Recognizer Intent Launcher
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = matches?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                viewModel.applyTranscribedEnglishSpeech(spokenText)
                viewModel.triggerEnglishVoicePreview()
            }
        }
    }

    fun launchEnglishVoiceDictation() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toLanguageTag())
                putExtra(
                    RecognizerIntent.EXTRA_PROMPT,
                    "Speak your scene description in English..."
                )
            }
            speechRecognizerLauncher.launch(intent)
        } catch (_: Exception) {
            // Fallback for emulators or environments without Google Voice Typing installed
            val demoEnglishSpeech =
                "A warm sunlit wooden studio where a friendly storyteller speaks in English with natural lip-sync animation."
            viewModel.applyTranscribedEnglishSpeech(demoEnglishSpeech)
            viewModel.triggerEnglishVoicePreview()
        }
    }

    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchEnglishVoiceDictation()
        } else {
            // Graceful fallback sample so user is never blocked
            val fallbackEnglishSpeech =
                "Every creative idea starts with a warm human voice. Watch how our English speech aligns with the presenter's lip-sync animation."
            viewModel.applyTranscribedEnglishSpeech(fallbackEnglishSpeech)
            viewModel.triggerEnglishVoicePreview()
        }
    }

    val onRequestEnglishVoiceInput = {
        val hasPerm = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (hasPerm) {
            launchEnglishVoiceDictation()
        } else {
            recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavigationRail = maxWidth >= 840.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "CineMotion Studio",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = "Nano Banana 2.1",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    },
                    actions = {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(999.dp),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.VerifiedUser,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "15s SLA • EN Lip-Sync",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                )
            },
            bottomBar = {
                if (!useNavigationRail) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.testTag("bottom_navigation_bar")
                    ) {
                        StudioDestination.entries.forEach { dest ->
                            val selected = uiState.currentTab == dest
                            NavigationBarItem(
                                selected = selected,
                                onClick = { viewModel.selectTab(dest) },
                                icon = {
                                    Icon(
                                        imageVector = destinationIcon(dest, selected),
                                        contentDescription = dest.label
                                    )
                                },
                                label = { Text(dest.label) },
                                modifier = Modifier.testTag("nav_tab_${dest.route}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useNavigationRail) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxHeight()
                            .testTag("side_navigation_rail")
                    ) {
                        StudioDestination.entries.forEach { dest ->
                            val selected = uiState.currentTab == dest
                            NavigationRailItem(
                                selected = selected,
                                onClick = { viewModel.selectTab(dest) },
                                icon = {
                                    Icon(
                                        imageVector = destinationIcon(dest, selected),
                                        contentDescription = dest.label
                                    )
                                },
                                label = { Text(dest.label) },
                                modifier = Modifier.testTag("nav_rail_${dest.route}")
                            )
                        }
                    }
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    // Subtle Status & Security Notice Bar
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.statusBannerMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("status_banner_text")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Prototype BuildConfig Key Notice",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Active Destination Screen
                    when (uiState.currentTab) {
                        StudioDestination.STUDIO -> {
                            StudioWorkspaceScreen(
                                uiState = uiState,
                                savedProjects = savedProjects,
                                viewModel = viewModel,
                                onRequestVoiceInput = onRequestEnglishVoiceInput,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        StudioDestination.VOICE_LIPSYNC -> {
                            VoiceLipSyncScreen(
                                uiState = uiState,
                                viewModel = viewModel,
                                onStartLiveMicRecognition = onRequestEnglishVoiceInput,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        StudioDestination.STORYBOARD -> {
                            StoryboardAndHistoryScreen(
                                uiState = uiState,
                                savedProjects = savedProjects,
                                viewModel = viewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        StudioDestination.CLOUD_WEB -> {
                            CloudAndWebStudioScreen(
                                uiState = uiState,
                                viewModel = viewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun destinationIcon(dest: StudioDestination, selected: Boolean) = when (dest) {
    StudioDestination.STUDIO ->
        if (selected) Icons.Filled.MovieCreation else Icons.Outlined.MovieCreation
    StudioDestination.VOICE_LIPSYNC ->
        if (selected) Icons.Filled.RecordVoiceOver else Icons.Outlined.RecordVoiceOver
    StudioDestination.STORYBOARD ->
        if (selected) Icons.Filled.AutoAwesomeMotion else Icons.Outlined.AutoAwesomeMotion
    StudioDestination.CLOUD_WEB ->
        if (selected) Icons.Filled.CloudQueue else Icons.Outlined.CloudQueue
}
