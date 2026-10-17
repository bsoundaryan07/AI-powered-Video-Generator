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
    viewModel: StudioViewModel,
    onRequestVoiceInput: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 840.dp

        if (isWideScreen) {
            // Canonical 2-Column Studio Layout for Tablets, Foldables & Desktop
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
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
                    EmpatheticScriptPreviewCard(uiState = uiState, viewModel = viewModel)
                }
            }
        } else {
            // Compact Vertical Handheld Layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
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

            // Primary 15-Second Rapid Generation CTA
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
                        "Generating (${String.format("%.1fs", uiState.generationElapsedSeconds)} / 15.0s)..."
                    } else {
                        "Generate 15s Lip-Sync Video"
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
