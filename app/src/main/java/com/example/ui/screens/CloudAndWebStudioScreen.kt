package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.cloud.CloudDeploymentCatalog
import com.example.ui.theme.JetBrainsMono
import com.example.viewmodel.StudioUiState
import com.example.viewmodel.StudioViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CloudAndWebStudioScreen(
    uiState: StudioUiState,
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    var selectedCodeTab by remember { mutableIntStateOf(0) }

    if (uiState.isWebStudioFullScreen) {
        BackHandler {
            viewModel.setWebStudioFullScreen(false)
        }
        Column(modifier = modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CineMotion Browser Web Studio (Light Theme)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedButton(
                        onClick = { viewModel.setWebStudioFullScreen(false) },
                        modifier = Modifier.testTag("btn_exit_fullscreen_web")
                    ) {
                        Icon(Icons.Filled.FullscreenExit, contentDescription = "Exit Fullscreen", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Exit Fullscreen")
                    }
                }
            }
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.mediaPlaybackRequiresUserGesture = false
                        webChromeClient = WebChromeClient()
                        webViewClient = WebViewClient()
                        loadUrl("file:///android_asset/web_studio.html")
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("fullscreen_webview")
            )
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Interactive Browser Web Studio Card
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Column {
                            Text(
                                text = "Interactive Browser Web Studio",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Light-theme responsive HTML5/JS Web Studio synced with Cloud API",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.setWebStudioFullScreen(true) },
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("btn_open_fullscreen_web")
                    ) {
                        Icon(Icons.Filled.Fullscreen, contentDescription = "Fullscreen Web", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Fullscreen")
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                settings.mediaPlaybackRequiresUserGesture = false
                                webChromeClient = WebChromeClient()
                                webViewClient = WebViewClient()
                                loadUrl("file:///android_asset/web_studio.html")
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("embedded_web_studio_webview")
                    )
                }
            }
        }

        // 2. Google Drive Static Hosting & Cloud Endpoint Configuration Card
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
                    Icon(Icons.Filled.CloudDone, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    Column {
                        Text(
                            text = "Google Drive Static Hosting & Cloud API Architecture",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Host static HTML/CSS/JS files on Google Drive & connect to the Python/Streamlit/MoviePy/FFmpeg + OpenAI/Nano Banana cloud pipeline",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                OutlinedTextField(
                    value = uiState.googleDriveFolderId,
                    onValueChange = { viewModel.updateGoogleDriveConfig(it, uiState.cloudEndpointUrl) },
                    label = { Text("Google Drive Static Hosting Folder ID") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_drive_folder_id")
                )

                OutlinedTextField(
                    value = uiState.cloudEndpointUrl,
                    onValueChange = { viewModel.updateGoogleDriveConfig(uiState.googleDriveFolderId, it) },
                    label = { Text("Cloud API Endpoint Bridge (Apps Script / Streamlit Cloud)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_cloud_endpoint_url")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Enable OpenAI API Script & Voice Bridge", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                        Text("Uses OPENAI_API_KEY alongside Gemini Nano Banana 2.1", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = uiState.useOpenAiBridge,
                        onCheckedChange = viewModel::toggleOpenAiBridge,
                        modifier = Modifier.testTag("switch_openai_bridge")
                    )
                }

                Button(
                    onClick = viewModel::exportGoogleDriveDeploymentZip,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_export_cloud_zip_bundle"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.FolderZip, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export Complete Web + Python/Streamlit + Drive ZIP Bundle")
                }
            }
        }

        // 3. Unified Codebase Inspector (Consistent Terminology Across All Files)
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
                    Icon(Icons.Filled.Code, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        text = "Integrated Cloud & Drive Source Files (Standardized Terms)",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                val codeTabs = listOf(
                    "app.py (Streamlit)",
                    "video_engine.py (MoviePy+LipSync)",
                    "nano_banana_client.py",
                    "drive_manifest.json"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    codeTabs.forEachIndexed { idx, label ->
                        FilterChip(
                            selected = selectedCodeTab == idx,
                            onClick = { selectedCodeTab = idx },
                            label = { Text(label) }
                        )
                    }
                }

                val displayedCode = when (selectedCodeTab) {
                    0 -> CloudDeploymentCatalog.generateStreamlitCloudAppPy(
                        uiState.selectedAesthetic,
                        uiState.selectedCameraMotion,
                        uiState.selectedVoice
                    )
                    1 -> CloudDeploymentCatalog.generateMoviePyLipSyncEnginePy()
                    2 -> CloudDeploymentCatalog.generateNanoBananaPythonModule()
                    else -> CloudDeploymentCatalog.generateGoogleDriveHostingManifestJson(
                        uiState.googleDriveFolderId,
                        uiState.cloudEndpointUrl
                    )
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(
                        text = displayedCode,
                        fontFamily = JetBrainsMono,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        }
    }
}
