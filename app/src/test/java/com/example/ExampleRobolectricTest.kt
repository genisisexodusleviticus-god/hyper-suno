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
    }
}
