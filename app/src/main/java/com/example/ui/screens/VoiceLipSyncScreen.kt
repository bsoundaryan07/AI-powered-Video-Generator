package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.EnglishVoicePreset
import com.example.model.LipSyncViseme
import com.example.ui.components.VideoStagePlayerCard
import com.example.viewmodel.StudioUiState
import com.example.viewmodel.StudioViewModel
import kotlin.math.sin

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VoiceLipSyncScreen(
    uiState: StudioUiState,
    viewModel: StudioViewModel,
    onStartLiveMicRecognition: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Live Video Stage showing the Lip-Sync Presenter in action
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

        // 1. English Voice Dictation & Speech-to-Video Studio Card
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
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = "English Voice-to-Video & Lip-Sync Studio",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Speak in English or pick a sample voiceover. Every syllable maps to phoneme mouth visemes.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Live Audio Waveform & Viseme Articulation Bar
                val primaryColor = MaterialTheme.colorScheme.primary
                val secondaryColor = MaterialTheme.colorScheme.secondary
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(76.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        val barCount = 28
                        val spacing = 6.dp.toPx()
                        val totalSpacing = spacing * (barCount - 1)
                        val barWidth = ((size.width - totalSpacing) / barCount).coerceAtLeast(4f)
                        val openFactor = uiState.activeViseme.mouthOpenRatio.coerceAtLeast(0.15f)
                        val timePhase = (uiState.currentPlaybackMs / 120.0)

                        for (i in 0 until barCount) {
                            val wave = (0.35f + 0.65f * kotlin.math.abs(sin(timePhase + i * 0.45))).toFloat()
                            val barH = (size.height * wave * (0.35f + 0.65f * openFactor)).coerceIn(8f, size.height)
                            val topY = (size.height - barH) / 2f
                            drawRoundRect(
                                color = if (i % 2 == 0) primaryColor else secondaryColor,
                                topLeft = Offset(i * (barWidth + spacing), topY),
                                size = Size(barWidth, barH),
                                cornerRadius = CornerRadius(4f, 4f)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onStartLiveMicRecognition,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_record_english_voice")
                    ) {
                        Icon(Icons.Filled.Mic, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Speak English Mic")
                    }

                    OutlinedButton(
                        onClick = {
                            val sampleSpeech = "Hello there! Notice how my lip movements match every English vowel and consonant naturally, creating a warm human connection in our 3D Animation studio."
                            viewModel.applyTranscribedEnglishSpeech(sampleSpeech)
                            viewModel.triggerEnglishVoicePreview()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_sample_english_voice")
                    ) {
                        Icon(Icons.Filled.PlayCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Demo English Voice")
                    }
                }

                OutlinedTextField(
                    value = uiState.empatheticEnglishScript,
                    onValueChange = viewModel::updateEmpatheticScript,
                    label = { Text("English Lip-Sync Transcript (Editable)") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("voice_transcript_editor")
                )
            }
        }

        // 2. Phoneme Viseme Inspector Grid
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.GraphicEq, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    Text(
                        text = "Real-Time English Phoneme Viseme Matrix",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Text(
                    text = "Active Spoken Word: \"${uiState.activeSpokenWord.ifBlank { "Ready" }}\" → Mouth Viseme: ${uiState.activeViseme.code}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LipSyncViseme.entries.forEach { viseme ->
                        val isActive = uiState.activeViseme == viseme
                        Surface(
                            color = if (isActive) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(
                                width = if (isActive) 2.dp else 1.dp,
                                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "[${viseme.code}] Open ${(viseme.mouthOpenRatio * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = viseme.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. English Neural Narrator Voice Selector
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.RecordVoiceOver, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        text = "English Narrator Voice & Presenter Persona",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                EnglishVoicePreset.entries.forEach { voice ->
                    val isSelected = uiState.selectedVoice == voice
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.selectEnglishVoice(voice)
                                viewModel.triggerEnglishVoicePreview()
                            }
                            .testTag("voice_preset_${voice.id}"),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        viewModel.selectEnglishVoice(voice)
                                        viewModel.triggerEnglishVoicePreview()
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = voice.displayName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${voice.accentLabel} • Edge-TTS: ${voice.edgeTtsVoiceId}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
