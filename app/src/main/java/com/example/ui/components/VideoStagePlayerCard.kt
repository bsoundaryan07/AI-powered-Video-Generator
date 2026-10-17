package com.example.ui.components

import android.graphics.Canvas as AndroidCanvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.engine.VideoSceneRenderer
import com.example.model.PipelineStage
import com.example.viewmodel.StudioUiState
import java.util.Locale

@Composable
fun VideoStagePlayerCard(
    uiState: StudioUiState,
    onTogglePlayPause: () -> Unit,
    onToggleMute: () -> Unit,
    onSeekProgress: (Float) -> Unit,
    onTestEnglishVoice: () -> Unit,
    onExportMp4: () -> Unit,
    onRandomizeRemix: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("video_stage_player_card"),
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
            // Top Stage Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "LIVE STAGE • ${uiState.selectedAspectRatio.id}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Viseme: ${uiState.activeViseme.code}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = String.format(
                        Locale.US,
                        "%02d.%ds / %02d.0s",
                        (uiState.currentPlaybackMs / 1000).toInt(),
                        ((uiState.currentPlaybackMs % 1000) / 100).toInt(),
                        uiState.durationSeconds
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Video Viewport Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(
                        uiState.selectedAspectRatio.ratioFloat.coerceIn(0.75f, 1.78f)
                    )
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .testTag("video_render_canvas"),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val nativeCanvas: AndroidCanvas = drawContext.canvas.nativeCanvas
                    VideoSceneRenderer.drawSceneFrame(
                        canvas = nativeCanvas,
                        width = size.width.toInt().coerceAtLeast(1),
                        height = size.height.toInt().coerceAtLeast(1),
                        timeMs = uiState.currentPlaybackMs,
                        totalDurationMs = uiState.durationSeconds * 1000L,
                        style = uiState.selectedAesthetic,
                        cameraMotion = uiState.selectedCameraMotion,
                        motionIntensity = uiState.motionIntensity,
                        englishScript = uiState.empatheticEnglishScript,
                        viseme = uiState.activeViseme,
                        activeWord = uiState.activeSpokenWord,
                        voicePreset = uiState.selectedVoice,
                        enableSubtitles = uiState.enableSubtitles,
                        enableLipSyncAvatar = uiState.enableLipSyncAvatar,
                        aiKeyframeBitmap = uiState.aiKeyframeBitmap
                    )
                }

                // 15-Second Rapid Pipeline Progress Overlay when actively generating
                if (uiState.isGenerating) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "15-Second Rapid Cloud & Local Pipeline",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = uiState.activePipelineStage.title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = uiState.activePipelineStage.subtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            LinearProgressIndicator(
                                progress = { uiState.generationProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = String.format(
                                        Locale.US,
                                        "Elapsed: %.1fs / 15.0s",
                                        uiState.generationElapsedSeconds
                                    ),
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    text = "${(uiState.generationProgress * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            // 5 Pipeline Stage Pills
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                PipelineStage.entries.forEach { stage ->
                                    val isDoneOrActive = stage.stepNumber <= uiState.activePipelineStage.stepNumber
                                    Surface(
                                        modifier = Modifier.weight(1f),
                                        color = if (isDoneOrActive) {
                                            MaterialTheme.colorScheme.primaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant
                                        },
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "0${stage.stepNumber}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isDoneOrActive) {
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            },
                                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Scrubber Slider
            val totalMs = (uiState.durationSeconds * 1000L).coerceAtLeast(1L)
            val sliderFraction = (uiState.currentPlaybackMs.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)
            Slider(
                value = sliderFraction,
                onValueChange = onSeekProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("timeline_scrubber_slider")
            )

            // Playback & Export Toolbar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalIconButton(
                        onClick = onTogglePlayPause,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("btn_play_pause")
                    ) {
                        Icon(
                            imageVector = if (uiState.isPlayingVideo) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (uiState.isPlayingVideo) "Pause Video" else "Play Video"
                        )
                    }

                    FilledTonalIconButton(
                        onClick = onToggleMute,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("btn_toggle_mute")
                    ) {
                        Icon(
                            imageVector = if (uiState.isAudioMuted) {
                                Icons.AutoMirrored.Filled.VolumeOff
                            } else {
                                Icons.AutoMirrored.Filled.VolumeUp
                            },
                            contentDescription = "Toggle English Voice Audio"
                        )
                    }

                    OutlinedButton(
                        onClick = onTestEnglishVoice,
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("btn_speak_english_now")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.RecordVoiceOver,
                            contentDescription = "Speak English Script",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Speak EN")
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onRandomizeRemix,
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("btn_remix_video")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Remix Seed",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Remix")
                    }

                    Button(
                        onClick = onExportMp4,
                        enabled = !uiState.isExportingMp4,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("btn_download_mp4")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Download,
                            contentDescription = "Export MP4",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (uiState.isExportingMp4) "Encoding..." else "Export MP4")
                    }
                }
            }
        }
    }
}
