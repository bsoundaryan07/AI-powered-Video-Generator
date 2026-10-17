package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.engine.EnglishLipSyncEngine
import com.example.model.LipSyncViseme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("CineMotion Studio", appName)
    }

    @Test
    fun `english phoneme viseme timeline maps words accurately`() {
        val script = "Moments of bright vision feel alive in motion"
        val timeline = EnglishLipSyncEngine.buildTimedVisemeTimeline(script, 6000L)
        assertTrue(timeline.isNotEmpty())
        assertEquals(LipSyncViseme.MBP, EnglishLipSyncEngine.analyzeWordViseme("Moments"))
        assertEquals(LipSyncViseme.FV_TH, EnglishLipSyncEngine.analyzeWordViseme("feel"))
        assertEquals(LipSyncViseme.OH_OO, EnglishLipSyncEngine.analyzeWordViseme("glow"))
    }
}
