package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.audio.MetalAudioEngine
import com.example.data.MetalAiGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun readStringFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Suno Hyper", appName)
    }

    @Test
    fun generateMetalTrackValidation() {
        val track = MetalAiGenerator.generateTrack("Demon and Angel War in Drop-F")
        assertNotNull(track)
        assertTrue(track.lyrics.isNotEmpty())
        assertTrue(track.scenes.isNotEmpty())
        assertTrue(track.bpm >= 180)
    }

    @Test
    fun audioEngineInitialization() {
        val engine = MetalAudioEngine.INSTANCE
        assertNotNull(engine)
        assertEquals(16, engine.stepGrid.size)
        assertEquals(4, engine.patternBanks.size)
    }

    @Test
    fun multiTrackSequencerPatternSwitchingAndCopy() {
        val engine = MetalAudioEngine.INSTANCE
        engine.currentPatternIndex = 0
        engine.stepGrid[0].kick = true
        engine.stepGrid[0].synthPitchIndex = 3

        // Copy Pattern 0 to Pattern 1
        engine.copyPattern(0, 1)
        assertEquals(true, engine.patternBanks[1][0].kick)
        assertEquals(3, engine.patternBanks[1][0].synthPitchIndex)

        // Clear Pattern 1
        engine.clearPattern(1)
        assertEquals(false, engine.patternBanks[1][0].kick)
    }

    @Test
    fun scalePitchFrequencyCalculation() {
        val engine = MetalAudioEngine.INSTANCE
        engine.activeScale = com.example.data.MetalScale.PHRYGIAN_DOMINANT
        val baseFreq = engine.getScalePitchFrequency(0, MetalAudioEngine.BASE_A1_FREQ)
        assertEquals(55.0f, baseFreq, 0.01f)

        val octaveFreq = engine.getScalePitchFrequency(engine.activeScale.semitones.size, MetalAudioEngine.BASE_A1_FREQ)
        assertEquals(110.0f, octaveFreq, 0.1f)
    }
}
