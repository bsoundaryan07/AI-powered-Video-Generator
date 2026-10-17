package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.VideoProjectEntity
import com.example.model.AspectRatioPreset
import com.example.model.CameraMotionPreset
import com.example.model.VisualAestheticPreset
import com.example.ui.components.VideoStagePlayerCard
import com.example.viewmodel.StudioDestination
import com.example.viewmodel.StudioUiState
import com.example.viewmodel.StudioViewModel

@Composable
fun StudioWorkspaceScreen(
    uiState: StudioUiState,
    savedProjects: List<VideoProjectEntity>,
    viewModel: StudioViewModel,
    onRequestVoiceInput: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 840.dp

        if (isWideScreen) {
            // Canonical 2-Column Studio Layout for Tablets, Foldables & Desktop
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PromptToVideoHeroBar(
                    uiState = uiState,
                    viewModel = viewModel,
                    onRequestVoiceInput = onRequestVoiceInput
                )
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StudioControlsColumn(
                            uiState = uiState,
                            viewModel = viewModel,
                            onRequestVoiceInput = onRequestVoiceInput
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1.15f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        VideoStagePlayerCard(
                            uiState = uiState,
                            onTogglePlayPause = viewModel::toggleVideoPlayback,
                            onToggleMute = viewModel::toggleMuteAudio,
                            onSeekProgress = viewModel::seekVideoProgress,
                            onTestEnglishVoice = viewModel::triggerEnglishVoicePreview,
                            onExportMp4 = viewModel::exportAndShareMp4Video,
                            onRandomizeRemix = {
                                viewModel.randomizeSeed()
                                viewModel.generateShortVideo15sPipeline()
                            }
                        )
                        GeneratedVideosShowcaseSection(
                            savedProjects = savedProjects,
                            activeSeed = uiState.seed,
                            onPlayProject = { project ->
                                viewModel.loadProjectFromHistory(project)
                                viewModel.triggerEnglishVoicePreview()
                            },
                            onExportMp4 = viewModel::exportAndShareMp4Video,
                            onDeleteProject = viewModel::deleteHistoryProject
                        )
                        EmpatheticScriptPreviewCard(uiState = uiState, viewModel = viewModel)
                    }
                }
            }
        } else {
            // Compact Vertical Handheld Layout — Prompt-to-Video Generator & Generated Videos Section right at the top
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PromptToVideoHeroBar(
                    uiState = uiState,
                    viewModel = viewModel,
                    onRequestVoiceInput = onRequestVoiceInput
                )
                VideoStagePlayerCard(
                    uiState = uiState,
                    onTogglePlayPause = viewModel::toggleVideoPlayback,
                    onToggleMute = viewModel::toggleMuteAudio,
                    onSeekProgress = viewModel::seekVideoProgress,
                    onTestEnglishVoice = viewModel::triggerEnglishVoicePreview,
                    onExportMp4 = viewModel::exportAndShareMp4Video,
                    onRandomizeRemix = {
                        viewModel.randomizeSeed()
                        viewModel.generateShortVideo15sPipeline()
                    }
                )
                GeneratedVideosShowcaseSection(
                    savedProjects = savedProjects,
                    activeSeed = uiState.seed,
                    onPlayProject = { project ->
                        viewModel.loadProjectFromHistory(project)
                        viewModel.triggerEnglishVoicePreview()
                    },
                    onExportMp4 = viewModel::exportAndShareMp4Video,
                    onDeleteProject = viewModel::deleteHistoryProject
                )
                EmpatheticScriptPreviewCard(uiState = uiState, viewModel = viewModel)
                StudioControlsColumn(
                    uiState = uiState,
                    viewModel = viewModel,
                    onRequestVoiceInput = onRequestVoiceInput
                )
            }
        }
    }
}

@Composable
private fun GeneratedVideosShowcaseSection(
    savedProjects: List<VideoProjectEntity>,
    activeSeed: Int,
    onPlayProject: (VideoProjectEntity) -> Unit,
    onExportMp4: () -> Unit,
    onDeleteProject: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("generated_videos_showcase_section"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Videocam,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = "Generated Videos (${savedProjects.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tap any generated video below to watch with English lip-sync on the live stage",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (savedProjects.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Your generated videos will appear here immediately after tapping 'Prompt to Video (1.2s)'.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    savedProjects.forEach { project ->
                        val isCurrentlyLoaded = project.seed == activeSeed
                        val stylePreset = VisualAestheticPreset.entries.firstOrNull { it.id == project.visualAestheticId }
                            ?: VisualAestheticPreset.HYPER_REALISTIC

                        Surface(
                            modifier = Modifier
                                .width(270.dp)
                                .clickable { onPlayProject(project) }
                                .testTag("generated_video_card_${project.id}"),
                            shape = RoundedCornerShape(14.dp),
                            color = if (isCurrentlyLoaded) {
                                stylePreset.secondaryColor
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            border = BorderStroke(
                                width = if (isCurrentlyLoaded) 2.dp else 1.dp,
                                color = if (isCurrentlyLoaded) stylePreset.primaryColor else MaterialTheme.colorScheme.outline
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = stylePreset.primaryColor,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = stylePreset.displayName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = androidx.compose.ui.graphics.Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Text(
                                        text = "${project.durationSeconds}s • ${project.aspectRatioId}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Text(
                                    text = project.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = "\"${project.empatheticEnglishScript}\"",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { onPlayProject(project) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp)
                                            .testTag("btn_watch_generated_${project.id}"),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(if (isCurrentlyLoaded) "Playing Now" else "Watch Video")
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            onPlayProject(project)
                                            onExportMp4()
                                        },
                                        modifier = Modifier.height(40.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("MP4")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PromptToVideoHeroBar(
    uiState: StudioUiState,
    viewModel: StudioViewModel,
    onRequestVoiceInput: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("prompt_to_video_hero_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.MovieCreation,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "AI Prompt-to-Video Generator",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "1.2s Veo 3.1 Fast",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            OutlinedTextField(
                value = uiState.promptInput,
                onValueChange = viewModel::updatePromptInput,
                label = { Text("Enter Prompt to Generate Video") },
                placeholder = { Text("e.g., Sunset over a calm ocean lighthouse with gentle waves and warm narration...") },
                minLines = 2,
                maxLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_prompt_to_video_input")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = viewModel::generateShortVideo15sPipeline,
                    enabled = !uiState.isGenerating,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_quick_prompt_to_video"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (uiState.isGenerating) {
                            "Rendering (${String.format("%.1fs", uiState.generationElapsedSeconds)})..."
                        } else {
                            "Prompt to Video (1.2s)"
                        },
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onRequestVoiceInput,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("btn_quick_voice_prompt")
                ) {
                    Icon(Icons.Filled.Mic, contentDescription = "Voice Prompt", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Speak")
                }
            }
        }
    }
}

@Composable
private fun EmpatheticScriptPreviewCard(
    uiState: StudioUiState,
    viewModel: StudioViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("empathetic_script_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Spoken English Narration (Lip-Sync Script)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = uiState.selectedVoice.accentLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            OutlinedTextField(
                value = uiState.empatheticEnglishScript,
                onValueChange = viewModel::updateEmpatheticScript,
                label = { Text("Human-Crafted English Voiceover") },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("empathetic_script_input")
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StudioControlsColumn(
    uiState: StudioUiState,
    viewModel: StudioViewModel,
    onRequestVoiceInput: () -> Unit
) {
    // 1. Prompt Composer Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "1. Scene Concept & Prompt Composer",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${uiState.promptInput.length}/500",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedTextField(
                value = uiState.promptInput,
                onValueChange = viewModel::updatePromptInput,
                placeholder = {
                    Text("Describe your scene naturally in English, or tap Voice-to-Video...")
                },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("prompt_textarea_input")
            )

            // Quick Actions Row
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onRequestVoiceInput,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("btn_voice_to_video")
                ) {
                    Icon(Icons.Filled.Mic, contentDescription = "Voice Input", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Voice-to-Video (EN)")
                }

                OutlinedButton(
                    onClick = viewModel::enhancePromptWithNanoBanana,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("btn_nano_banana_enhance")
                ) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = "Nano Banana Enhance", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Nano Banana Humanize")
                }

                OutlinedButton(
                    onClick = viewModel::loadSurpriseHumanPrompt,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("btn_surprise_prompt")
                ) {
                    Icon(Icons.Filled.Casino, contentDescription = "Surprise Me", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Surprise Me")
                }

                OutlinedButton(
                    onClick = { viewModel.updatePromptInput("") },
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("btn_clear_prompt")
                ) {
                    Icon(Icons.Filled.Clear, contentDescription = "Clear Prompt", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear")
                }
            }

            // 1-Tap Instant AI Video Generator Presets (Veo 3.1 Fast + Nano Banana 2.1)
            Text(
                text = "Instant 1-Tap AI Video Generator Presets (Veo 3.1 Fast)",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val quickPresets = listOf(
                    Triple("Photoreal presenter", VisualAestheticPreset.HYPER_REALISTIC, "A warm sunlit studio where a friendly storyteller speaks directly to the camera with natural English lip-sync."),
                    Triple("Pixar 3D Story", VisualAestheticPreset.THREE_D_ANIMATION, "An expressive 3D animated character in a cozy workshop sharing an inspiring story in clear English."),
                    Triple("Ghibli Meadow", VisualAestheticPreset.ANIME_GHIBLI, "A peaceful hand-painted hillside under a bright blue sky where a gentle breeze moves across golden grass."),
                    Triple("Sci-Fi Horizon", VisualAestheticPreset.CYBERPUNK_SCIFI, "A clean futuristic solar city plaza in bright daylight with holographic displays and smooth orbital motion.")
                )
                quickPresets.forEachIndexed { idx, (label, style, presetPrompt) ->
                    FilterChip(
                        selected = uiState.selectedAesthetic == style && uiState.promptInput == presetPrompt,
                        onClick = {
                            viewModel.selectVisualAesthetic(style)
                            viewModel.updatePromptInput(presetPrompt)
                            viewModel.generateShortVideo15sPipeline()
                        },
                        label = { Text("⚡ $label") },
                        modifier = Modifier.testTag("instant_ai_preset_$idx")
                    )
                }
            }

            // Primary Instant AI Video Generator CTA
            Button(
                onClick = viewModel::generateShortVideo15sPipeline,
                enabled = !uiState.isGenerating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_generate_video_primary"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.MovieCreation, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (uiState.isGenerating) {
                        "AI Generating (${String.format("%.1fs", uiState.generationElapsedSeconds)} / 1.2s Turbo)..."
                    } else {
                        "Generate AI Video Now (1.2s Veo Fast)"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }

    // 2. Visual Aesthetic Presets Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "2. Visual Aesthetic Preset",
                    style = MaterialTheme.typography.titleMedium
                )
                Surface(
                    color = uiState.selectedAesthetic.secondaryColor,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = uiState.selectedAesthetic.displayName,
                        style = MaterialTheme.typography.labelLarge,
                        color = uiState.selectedAesthetic.primaryColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Category Filter Chips
            val categories = listOf("All", "Realism", "3D & CGI", "Sci-Fi", "Cinematic", "Artistic")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = uiState.selectedCategoryFilter == cat,
                        onClick = { viewModel.selectCategoryFilter(cat) },
                        label = { Text(cat) }
                    )
                }
            }

            val filteredStyles = VisualAestheticPreset.entries.filter {
                uiState.selectedCategoryFilter == "All" || it.category == uiState.selectedCategoryFilter
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                filteredStyles.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pair.forEach { preset ->
                            val isSelected = uiState.selectedAesthetic == preset
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.selectVisualAesthetic(preset) }
                                    .testTag("style_card_${preset.id}"),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) preset.secondaryColor else MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) preset.primaryColor else MaterialTheme.colorScheme.outline
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = preset.displayName,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (isSelected) preset.primaryColor else MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Text(
                                        text = preset.badgeLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = preset.primaryColor
                                    )
                                    Text(
                                        text = preset.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

    // 3. Camera Motion & Render Configuration Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Filled.Videocam, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = "3. Camera Motion & Studio Parameters",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CameraMotionPreset.entries.forEach { motion ->
                    FilterChip(
                        selected = uiState.selectedCameraMotion == motion,
                        onClick = { viewModel.selectCameraMotion(motion) },
                        label = { Text(motion.shortLabel) },
                        modifier = Modifier.testTag("camera_chip_${motion.id}")
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Motion Dynamics Intensity", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "${uiState.motionIntensity}x",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = uiState.motionIntensity.toFloat(),
                onValueChange = { viewModel.updateMotionIntensity(it.toInt()) },
                valueRange = 1f..10f,
                steps = 8,
                modifier = Modifier.fillMaxWidth()
            )

            // Aspect Ratio & Duration Selector
            Text("Aspect Ratio & Clip Duration", style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AspectRatioPreset.entries.forEach { aspect ->
                    FilterChip(
                        selected = uiState.selectedAspectRatio == aspect,
                        onClick = { viewModel.selectAspectRatio(aspect) },
                        label = { Text(aspect.label) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(4, 6, 8, 15).forEach { sec ->
                    FilterChip(
                        selected = uiState.durationSeconds == sec,
                        onClick = { viewModel.selectDuration(sec) },
                        label = { Text("${sec}s Clip") }
                    )
                }
            }

            // Toggles for Nano Banana Pro, Lip-Sync Avatar, Subtitles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("English Lip-Sync Presenter Avatar", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    Text("Animates phoneme mouth shapes synced to English voice", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = uiState.enableLipSyncAvatar,
                    onCheckedChange = viewModel::toggleLipSyncAvatar,
                    modifier = Modifier.testTag("switch_lipsync_avatar")
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Burn Synchronized Subtitles", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    Text("High-contrast lower-third English captions", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = uiState.enableSubtitles,
                    onCheckedChange = viewModel::toggleSubtitles,
                    modifier = Modifier.testTag("switch_subtitles")
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Gemini Nano Banana 2.1 High-Res", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    Text("Uses gemini-nano-banana-2.1 for 1K visual keyframes", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = uiState.useProNanoBanana,
                    onCheckedChange = viewModel::toggleProNanoBanana,
                    modifier = Modifier.testTag("switch_nano_banana_pro")
                )
            }
        }
    }
}
