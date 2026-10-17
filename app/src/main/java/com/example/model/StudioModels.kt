package com.example.model

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

/**
 * Standardized Visual Aesthetic Presets used consistently across the Android Studio,
 * Web Browser Studio, and Python/Streamlit/MoviePy Cloud Pipeline.
 */
enum class VisualAestheticPreset(
    val id: String,
    val displayName: String,
    val badgeLabel: String,
    val category: String,
    val description: String,
    val promptSuffix: String,
    val negativePrompt: String,
    val primaryColorHex: Long,
    val secondaryColorHex: Long
) {
    HYPER_REALISTIC(
        id = "hyper-realistic",
        displayName = "Hyper-Realistic",
        badgeLabel = "Photoreal 8K",
        category = "Realism",
        description = "Ultra-realistic 8K cinema optics, natural daylight, authentic human skin subsurface scattering, and crisp depth of field.",
        promptSuffix = "masterpiece, ultra-detailed, 8k uhd, photorealistic, natural volumetric lighting, sharp focus, Hasselblad 50mm lens",
        negativePrompt = "cartoon, illustration, 3d render, anime, blurry, deformed, low quality",
        primaryColorHex = 0xFF1E3A8A,
        secondaryColorHex = 0xFFDBEAFE
    ),
    THREE_D_ANIMATION(
        id = "3d-animation",
        displayName = "3D Animation",
        badgeLabel = "Pixar & CGI 3D",
        category = "3D & CGI",
        description = "Expressive stylized 3D character and world animation with warm ambient occlusion, smooth geometry, and tactile charm.",
        promptSuffix = "Pixar style, expressive 3D animation render, warm studio lighting, subsurface scattering, vibrant clean colors",
        negativePrompt = "grainy photo, flat 2d sketch, low resolution, distorted proportions",
        primaryColorHex = 0xFFB45309,
        secondaryColorHex = 0xFFFEF3C7
    ),
    ANIME_GHIBLI(
        id = "anime-ghibli",
        displayName = "Anime & Studio Ghibli",
        badgeLabel = "Hand-Painted Sky",
        category = "Artistic",
        description = "Lush hand-painted backgrounds, expressive character acting, gentle wind-swept meadows, and warm nostalgic daylight.",
        promptSuffix = "Studio Ghibli aesthetic, Makoto Shinkai sky, hand-painted background, gentle breeze, warm natural illustration",
        negativePrompt = "photorealistic, 3d cgi, harsh digital noise, western comic",
        primaryColorHex = 0xFF0F766E,
        secondaryColorHex = 0xFFCCFBF1
    ),
    CYBERPUNK_SCIFI(
        id = "cyberpunk-scifi",
        displayName = "Cyberpunk & Sci-Fi",
        badgeLabel = "Futuristic Horizon",
        category = "Sci-Fi",
        description = "High-tech architectural vistas, holographic interfaces, wet pavement reflections, and clean sci-fi industrial design.",
        promptSuffix = "futuristic sci-fi architecture, holographic HUD, reflective surfaces, clean atmospheric perspective, 8k",
        negativePrompt = "medieval, rustic, low poly, washed out, blurry",
        primaryColorHex = 0xFF4338CA,
        secondaryColorHex = 0xFFE0E7FF
    ),
    CINEMATIC_MASTERPIECE(
        id = "cinematic-movie",
        displayName = "Cinematic Masterpiece",
        badgeLabel = "Hollywood 35mm",
        category = "Cinematic",
        description = "Anamorphic widescreen framing, Kodak 35mm film stock texture, balanced chiaroscuro lighting, and human emotional depth.",
        promptSuffix = "cinematic 35mm film still, anamorphic lens, balanced natural lighting, shallow depth of field, IMAX framing",
        negativePrompt = "amateur video, overexposed, flat lighting, low resolution",
        primaryColorHex = 0xFF9A3412,
        secondaryColorHex = 0xFFFFEDD5
    ),
    CLAYMATION_STOP_MOTION(
        id = "claymation",
        displayName = "Claymation Stop-Motion",
        badgeLabel = "Handcrafted Tactile",
        category = "3D & CGI",
        description = "Tactile handcrafted plasticine sculpts, miniature studio diorama lighting, and charming stop-motion character expression.",
        promptSuffix = "claymation stop-motion animation, handcrafted miniature set, soft studio softbox lighting, tactile clay texture",
        negativePrompt = "smooth digital CGI, photorealistic human, flat vector",
        primaryColorHex = 0xFF15803D,
        secondaryColorHex = 0xFFDCFCE7
    ),
    FANTASY_MYTHOLOGICAL(
        id = "fantasy-myth",
        displayName = "Fantasy & Mythological",
        badgeLabel = "Enchanted Realm",
        category = "Artistic",
        description = "Serene enchanted sanctuaries, ancient stone architecture, floating botanical lanterns, and timeless storytelling.",
        promptSuffix = "high fantasy concept art, serene enchanted sanctuary, intricate stone architecture, natural sunlight",
        negativePrompt = "modern cars, concrete highway, low resolution, artifacts",
        primaryColorHex = 0xFF6D28D9,
        secondaryColorHex = 0xFFEDE9FE
    ),
    RETRO_SYNTHWAVE(
        id = "retro-synthwave",
        displayName = "Retro 80s Horizon",
        badgeLabel = "Analog Horizon",
        category = "Sci-Fi",
        description = "Clean geometric perspective grids, warm analog sunset horizon, classic vector lines, and nostalgic studio warmth.",
        promptSuffix = "1980s retro-futurism, clean perspective grid, warm analog sunset horizon, crisp geometric vector lines",
        negativePrompt = "muddy colors, blurry, unstructured noise",
        primaryColorHex = 0xFFBE185D,
        secondaryColorHex = 0xFFFCE7F3
    );

    val primaryColor: Color get() = Color(primaryColorHex)
    val secondaryColor: Color get() = Color(secondaryColorHex)
}

/**
 * Standardized Camera Motion Presets (Ken Burns & 3D Parallax).
 */
enum class CameraMotionPreset(
    val id: String,
    val displayName: String,
    val shortLabel: String,
    val description: String
) {
    DYNAMIC_ZOOM_IN(
        id = "zoom-in",
        displayName = "Dynamic Zoom In (Epic Focus)",
        shortLabel = "Push In",
        description = "Smoothly pushes into the focal subject to build emotional connection."
    ),
    SLOW_PULL_OUT(
        id = "pull-out",
        displayName = "Slow Pull Out (Scene Reveal)",
        shortLabel = "Pull Out",
        description = "Gently widens the frame to reveal the surrounding environment."
    ),
    ORBIT_360(
        id = "orbit",
        displayName = "360° Orbital Pan",
        shortLabel = "360° Orbit",
        description = "Graceful circular camera movement around the central character."
    ),
    PAN_RIGHT(
        id = "pan-right",
        displayName = "Cinematic Pan Right",
        shortLabel = "Pan Right",
        description = "Steady horizontal tracking shot from left to right."
    ),
    PARALLAX_3D(
        id = "parallax",
        displayName = "Subtle 3D Parallax & Float",
        shortLabel = "3D Parallax",
        description = "Multi-plane depth shift separating foreground presenter and background."
    ),
    STATIC_TRIPOD(
        id = "static",
        displayName = "Locked Studio Tripod",
        shortLabel = "Tripod",
        description = "Rock-steady framing ideal for close-up English lip-sync narration."
    )
}

/**
 * Standardized Video Aspect Ratios.
 */
enum class AspectRatioPreset(
    val id: String,
    val label: String,
    val widthPx: Int,
    val heightPx: Int,
    val ratioFloat: Float
) {
    LANDSCAPE_16_9("16:9", "16:9 Landscape", 640, 360, 16f / 9f),
    VERTICAL_9_16("9:16", "9:16 Vertical", 360, 640, 9f / 16f),
    SQUARE_1_1("1:1", "1:1 Square", 480, 480, 1f)
}

/**
 * Standardized English Voice Narrator Presets with human warmth & cadence metadata.
 */
enum class EnglishVoicePreset(
    val id: String,
    val displayName: String,
    val accentLabel: String,
    val edgeTtsVoiceId: String,
    val pitchMultiplier: Float,
    val speechRate: Float,
    val avatarName: String
) {
    CLARA_US(
        id = "clara-us",
        displayName = "Clara (Warm & Empathetic Storyteller)",
        accentLabel = "English (US)",
        edgeTtsVoiceId = "en-US-JennyNeural",
        pitchMultiplier = 1.03f,
        speechRate = 0.96f,
        avatarName = "Clara"
    ),
    ARTHUR_UK(
        id = "arthur-uk",
        displayName = "Arthur (Calm Editorial Narrator)",
        accentLabel = "English (UK)",
        edgeTtsVoiceId = "en-GB-BrianNeural",
        pitchMultiplier = 0.95f,
        speechRate = 0.95f,
        avatarName = "Arthur"
    ),
    MAYA_US(
        id = "maya-us",
        displayName = "Maya (Expressive Creative Director)",
        accentLabel = "English (US)",
        edgeTtsVoiceId = "en-US-AriaNeural",
        pitchMultiplier = 1.06f,
        speechRate = 0.98f,
        avatarName = "Maya"
    ),
    LIAM_AU(
        id = "liam-au",
        displayName = "Liam (Friendly Conversational Guide)",
        accentLabel = "English (AU)",
        edgeTtsVoiceId = "en-AU-WilliamNeural",
        pitchMultiplier = 0.98f,
        speechRate = 0.97f,
        avatarName = "Liam"
    )
}

/**
 * Phoneme Viseme states for accurate English Speech-to-Lip-Sync animation.
 */
enum class LipSyncViseme(
    val code: String,
    val mouthOpenRatio: Float,
    val mouthWidthRatio: Float,
    val lipRoundness: Float,
    val description: String
) {
    REST("REST", 0.05f, 0.55f, 0.1f, "Neutral closed lips"),
    AA_AH("AA", 0.85f, 0.72f, 0.2f, "Wide open vowel (father, start, bright)"),
    EE_IH("EE", 0.38f, 0.88f, 0.05f, "Spread smile vowel (see, feel, dream)"),
    OH_OO("OH", 0.68f, 0.42f, 0.90f, "Rounded vowel (glow, world, motion)"),
    FV_TH("FV", 0.22f, 0.62f, 0.15f, "Labiodental consonant (voice, film, life)"),
    MBP("MBP", 0.02f, 0.58f, 0.05f, "Bilabial pressed consonant (moment, build, peace)")
}

@Serializable
data class TimedVisemeEvent(
    val word: String,
    val visemeName: String,
    val startMs: Long,
    val endMs: Long
)

@Serializable
data class StoryboardSceneItem(
    val sceneIndex: Int,
    val sceneTitle: String,
    val spokenEnglishScript: String,
    val enhancedVisualPrompt: String,
    val cameraMotionId: String,
    val durationSeconds: Int = 5,
    val keyframeBase64: String? = null
)

/**
 * 5-Stage 15-Second Rapid Generation Pipeline Steps
 */
enum class PipelineStage(
    val stepNumber: Int,
    val title: String,
    val subtitle: String,
    val startSec: Int,
    val endSec: Int
) {
    STAGE_1_NLP(
        1,
        "Nano Banana Empathetic NLP",
        "Refining script into warm, human-crafted English narrative...",
        0,
        3
    ),
    STAGE_2_VISUAL(
        2,
        "Nano Banana Keyframe Synthesis",
        "Generating high-clarity visual composition & scene lighting...",
        3,
        6
    ),
    STAGE_3_VOICE_LIPSYNC(
        3,
        "English Voice & Viseme Lip-Sync",
        "Aligning English speech cadence with phoneme mouth shapes...",
        6,
        9
    ),
    STAGE_4_CAMERA_MOTION(
        4,
        "MoviePy & Ken-Burns Camera Motion",
        "Interpolating 3D parallax, avatar lip-sync & subtitle overlays...",
        9,
        12
    ),
    STAGE_5_FFMPEG_MASTER(
        5,
        "FFmpeg H.264 Master & Cloud Sync",
        "Encoding MP4 video stream for instant playback & Drive export...",
        12,
        15
    )
}
