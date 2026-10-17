package com.example.engine

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.model.EnglishVoicePreset
import com.example.model.LipSyncViseme
import com.example.model.TimedVisemeEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * English Speech & Phoneme Viseme Lip-Sync Engine.
 * Ensures generated speech is strictly in English (`Locale.US` / `Locale.UK`) and
 * drives real-time mouth visemes (`REST`, `AA`, `EE`, `OH`, `FV`, `MBP`) synchronized
 * to the spoken word boundaries and syllable vowels.
 */
class EnglishLipSyncEngine(context: Context) {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private val _currentViseme = MutableStateFlow(LipSyncViseme.REST)
    val currentViseme: StateFlow<LipSyncViseme> = _currentViseme.asStateFlow()

    private val _activeSpokenWord = MutableStateFlow("")
    val activeSpokenWord: StateFlow<String> = _activeSpokenWord.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val res = tts?.setLanguage(Locale.US)
                isTtsReady = res != TextToSpeech.LANG_MISSING_DATA && res != TextToSpeech.LANG_NOT_SUPPORTED
                setupProgressListener()
            }
        }
    }

    private fun setupProgressListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isSpeaking.value = true
            }

            override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                // Real-time word boundary callback on Android API 26+
                val fullText = lastSpokenScript
                if (start in 0..fullText.length && end in start..fullText.length) {
                    val word = fullText.substring(start, end).trim()
                    _activeSpokenWord.value = word
                    _currentViseme.value = analyzeWordViseme(word)
                }
            }

            override fun onDone(utteranceId: String?) {
                _isSpeaking.value = false
                _currentViseme.value = LipSyncViseme.REST
                _activeSpokenWord.value = ""
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _isSpeaking.value = false
                _currentViseme.value = LipSyncViseme.REST
            }
        })
    }

    private var lastSpokenScript: String = ""

    fun speakEnglishWithLipSync(
        englishScript: String,
        voicePreset: EnglishVoicePreset,
        muted: Boolean = false
    ) {
        lastSpokenScript = englishScript
        if (!isTtsReady || muted) return

        val targetLocale = when {
            voicePreset.accentLabel.contains("UK") -> Locale.UK
            else -> Locale.US
        }
        tts?.language = targetLocale
        tts?.setPitch(voicePreset.pitchMultiplier)
        tts?.setSpeechRate(voicePreset.speechRate)

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "cinemotion_lipsync_${System.currentTimeMillis()}")
        }
        tts?.speak(englishScript, TextToSpeech.QUEUE_FLUSH, params, "cinemotion_utt")
    }

    fun stopSpeaking() {
        tts?.stop()
        _isSpeaking.value = false
        _currentViseme.value = LipSyncViseme.REST
        _activeSpokenWord.value = ""
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
    }

    companion object {
        /**
         * Maps any English word or syllable to its dominant phonetic mouth viseme
         * so the lip-sync presenter avatar matches the English speech articulation.
         */
        fun analyzeWordViseme(word: String): LipSyncViseme {
            val clean = word.lowercase(Locale.US).filter { it.isLetter() }
            if (clean.isEmpty()) return LipSyncViseme.REST

            return when {
                clean.startsWith("m") || clean.startsWith("b") || clean.startsWith("p") -> LipSyncViseme.MBP
                clean.startsWith("f") || clean.startsWith("v") || clean.startsWith("th") -> LipSyncViseme.FV_TH
                clean.contains("oo") || clean.contains("ow") || clean.contains("ou") || clean.contains("o") -> LipSyncViseme.OH_OO
                clean.contains("ee") || clean.contains("ea") || clean.contains("ie") || clean.endsWith("y") -> LipSyncViseme.EE_IH
                clean.contains("a") || clean.contains("ai") || clean.contains("ar") -> LipSyncViseme.AA_AH
                clean.contains("e") || clean.contains("i") -> LipSyncViseme.EE_IH
                clean.contains("u") -> LipSyncViseme.OH_OO
                else -> LipSyncViseme.AA_AH
            }
        }

        /**
         * Builds a complete millisecond-accurate timeline of English viseme keyframes
         * across a given video duration (e.g., 6000ms or 15000ms) for deterministic canvas & MP4 rendering.
         */
        fun buildTimedVisemeTimeline(
            englishScript: String,
            totalDurationMs: Long
        ): List<TimedVisemeEvent> {
            val words = englishScript.trim()
                .split(Regex("\\s+"))
                .filter { it.isNotBlank() }
            if (words.isEmpty()) return emptyList()

            val events = mutableListOf<TimedVisemeEvent>()
            val activeSpeechDuration = (totalDurationMs * 0.90).toLong().coerceAtLeast(1000L)
            val msPerWord = (activeSpeechDuration / words.size).coerceAtLeast(140L)

            var cursorMs = 150L
            for (word in words) {
                val wordStart = cursorMs
                val wordEnd = (cursorMs + msPerWord).coerceAtMost(totalDurationMs)
                // Split multi-syllable words into two viseme phases (consonant onset + vowel nucleus)
                val midPoint = (wordStart + wordEnd) / 2
                val onsetViseme = when {
                    word.lowercase().startsWith("m") || word.lowercase().startsWith("b") || word.lowercase().startsWith("p") -> LipSyncViseme.MBP
                    word.lowercase().startsWith("f") || word.lowercase().startsWith("v") -> LipSyncViseme.FV_TH
                    else -> analyzeWordViseme(word)
                }
                val nucleusViseme = analyzeWordViseme(word.drop(1).ifEmpty { word })

                events.add(
                    TimedVisemeEvent(
                        word = word,
                        visemeName = onsetViseme.name,
                        startMs = wordStart,
                        endMs = midPoint
                    )
                )
                events.add(
                    TimedVisemeEvent(
                        word = word,
                        visemeName = nucleusViseme.name,
                        startMs = midPoint,
                        endMs = wordEnd - 30L
                    )
                )
                // Subtle micro-closure between words for natural human articulation
                events.add(
                    TimedVisemeEvent(
                        word = word,
                        visemeName = LipSyncViseme.REST.name,
                        startMs = wordEnd - 30L,
                        endMs = wordEnd
                    )
                )
                cursorMs = wordEnd
            }
            return events
        }

        /**
         * Resolves the active [LipSyncViseme] and spoken word at any given playback timestamp `timeMs`.
         */
        fun sampleAtTime(
            timeline: List<TimedVisemeEvent>,
            timeMs: Long
        ): Pair<LipSyncViseme, String> {
            val event = timeline.firstOrNull { timeMs in it.startMs..it.endMs }
                ?: return Pair(LipSyncViseme.REST, "")
            val viseme = runCatching { LipSyncViseme.valueOf(event.visemeName) }
                .getOrDefault(LipSyncViseme.REST)
            return Pair(viseme, event.word)
        }
    }
}
