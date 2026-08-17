package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin

class MetalAudioEngine {

    companion object {
        const val SAMPLE_RATE = 44100
        val INSTANCE: MetalAudioEngine by lazy { MetalAudioEngine() }
    }

    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    // Real-time audio stream buffer
    private val audioQueue = ConcurrentLinkedQueue<ShortArray>()

    // Sequencer state
    var isPlaying = false
        private set

    var bpm: Int = 220
    var currentStep: Int = 0
        private set

    // Stem volume/mute controls (1.0 = full, 0.0 = muted)
    var stemGuitarsVolume: Float = 1.0f
    var stemDrumsVolume: Float = 1.0f
    var stemAngelPadsVolume: Float = 1.0f
    var stemGlitchFxVolume: Float = 1.0f
    var masterVolume: Float = 0.9f
    var masterDistortion: Float = 0.6f

    // Callbacks
    var onStepCallback: ((Int) -> Unit)? = null
    var onVisualizerCallback: ((amplitude: Float, spectrum: FloatArray) -> Unit)? = null

    // 16-step grid
    val stepGrid = Array(16) {
        com.example.data.BeatStep()
    }

    init {
        initDefaultPattern()
        initAudioTrack()
    }

    private fun initDefaultPattern() {
        // High speed hyper-metal blastbeat & djent chug pattern
        for (i in 0 until 16) {
            // Kick on 0, 2, 4, 6, 8, 10, 12, 14 (Double bass blast)
            stepGrid[i].kick = (i % 2 == 0)
            // Snare on 4, 12 (Heavy 2 and 4 metal blast)
            stepGrid[i].snare = (i == 4 || i == 12)
            // Hihat on every odd step
            stepGrid[i].hihat = (i % 2 != 0)
            // Djent Chug syncopation (0, 3, 6, 9, 10, 14)
            stepGrid[i].djentChug = (i == 0 || i == 3 || i == 6 || i == 9 || i == 10 || i == 14)
            // Angel Pad on step 0 and 8
            stepGrid[i].angelPad = (i == 0 || i == 8)
            // Glitch Zap on step 15 fill
            stepGrid[i].glitchZap = (i == 15 || i == 7)
        }
    }

    private fun initAudioTrack() {
        val bufferSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ) * 2

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()

        // Background playback thread
        scope.launch {
            val emptyBuffer = ShortArray(512)
            while (isActive) {
                val chunk = audioQueue.poll()
                if (chunk != null) {
                    audioTrack?.write(chunk, 0, chunk.size)
                } else {
                    audioTrack?.write(emptyBuffer, 0, emptyBuffer.size)
                    delay(5)
                }
            }
        }
    }

    fun startSequencer() {
        if (isPlaying) return
        isPlaying = true
        playbackJob = scope.launch {
            while (isActive && isPlaying) {
                val stepDurationMs = (60_000L / bpm) / 4L // 16th notes
                val step = stepGrid[currentStep]

                // Synthesize active step audio
                val samplesCount = (SAMPLE_RATE * (stepDurationMs / 1000.0)).toInt().coerceAtLeast(256)
                val buffer = FloatArray(samplesCount)

                // 1. Djent Chug
                if (step.djentChug && stemGuitarsVolume > 0.05f) {
                    val pitch = if (currentStep % 4 == 0) 55.0f else 58.27f // Drop A / F tuning low notes
                    mixDjentChug(buffer, pitch, 0.7f * stemGuitarsVolume * (1f + masterDistortion * 0.5f))
                }

                // 2. Blast Kick
                if (step.kick && stemDrumsVolume > 0.05f) {
                    mixKick(buffer, 0.85f * stemDrumsVolume)
                }

                // 3. Snare
                if (step.snare && stemDrumsVolume > 0.05f) {
                    mixSnare(buffer, 0.8f * stemDrumsVolume)
                }

                // 4. HiHat
                if (step.hihat && stemDrumsVolume > 0.05f) {
                    mixHiHat(buffer, 0.4f * stemDrumsVolume)
                }

                // 5. Angelic Pad
                if (step.angelPad && stemAngelPadsVolume > 0.05f) {
                    val padPitch = if (currentStep < 8) 440.0f else 523.25f // A4 / C5 celestial chords
                    mixAngelicPad(buffer, padPitch, 0.45f * stemAngelPadsVolume)
                }

                // 6. Glitch Zap
                if (step.glitchZap && stemGlitchFxVolume > 0.05f) {
                    mixGlitchZap(buffer, 0.6f * stemGlitchFxVolume)
                }

                // Convert float to PCM 16-bit with soft-clipping master distortion
                val pcmBuffer = ShortArray(samplesCount)
                var maxAmp = 0.0f
                val fakeSpectrum = FloatArray(8)

                for (i in 0 until samplesCount) {
                    var sample = buffer[i] * masterVolume
                    // Soft clipping metal saturation
                    sample = (1.5f * sample - 0.5f * sample * sample * sample).coerceIn(-1.0f, 1.0f)
                    val pcmVal = (sample * 32767.0f).toInt().coerceIn(-32768, 32767).toShort()
                    pcmBuffer[i] = pcmVal

                    val absSample = Math.abs(sample)
                    if (absSample > maxAmp) maxAmp = absSample
                }

                // Visualizer spectrum approximation
                for (b in 0 until 8) {
                    val factor = if (step.djentChug && b < 3) 0.9f
                    else if (step.snare && (b == 4 || b == 5)) 0.8f
                    else if (step.angelPad && b >= 4) 0.75f
                    else if (step.glitchZap) 0.85f
                    else 0.2f
                    fakeSpectrum[b] = (maxAmp * factor).coerceIn(0.05f, 1.0f)
                }

                audioQueue.offer(pcmBuffer)
                onStepCallback?.invoke(currentStep)
                onVisualizerCallback?.invoke(maxAmp, fakeSpectrum)

                currentStep = (currentStep + 1) % 16
                delay(stepDurationMs)
            }
        }
    }

    fun stopSequencer() {
        isPlaying = false
        playbackJob?.cancel()
        playbackJob = null
        currentStep = 0
        onStepCallback?.invoke(0)
    }

    // --- One-Shot Live Sound Trigger Functions ---

    fun triggerDjentChug(lowNote: Boolean = true) {
        scope.launch {
            val durationMs = 350
            val samplesCount = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = FloatArray(samplesCount)
            val pitch = if (lowNote) 48.99f else 65.41f // Drop G / Drop C chug
            mixDjentChug(buffer, pitch, 0.95f, true)
            submitOneShot(buffer)
        }
    }

    fun triggerDemonicRoar() {
        scope.launch {
            val durationMs = 600
            val samplesCount = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = FloatArray(samplesCount)
            for (i in 0 until samplesCount) {
                val t = i.toFloat() / SAMPLE_RATE
                val env = (1.0f - (i.toFloat() / samplesCount)) * exp(-t * 2.0f)
                // Sub-harmonic throat modulation
                val mod = sin(2.0 * PI * 18.0 * t).toFloat()
                val carrier = sin(2.0 * PI * (65.0 + mod * 30.0) * t).toFloat()
                val noise = ((Math.random() * 2.0 - 1.0).toFloat()) * 0.4f
                var s = (carrier + noise) * env * 0.9f
                // Distortion overdrive
                s = Math.sin(s * 2.5).toFloat()
                buffer[i] = s
            }
            submitOneShot(buffer)
        }
    }

    fun triggerAngelicChoir() {
        scope.launch {
            val durationMs = 800
            val samplesCount = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = FloatArray(samplesCount)
            val freqs = floatArrayOf(440f, 554.37f, 659.25f, 880f) // A major ethereal celestial triad
            for (i in 0 until samplesCount) {
                val t = i.toFloat() / SAMPLE_RATE
                val env = sin(PI * (i.toFloat() / samplesCount)).toFloat()
                var s = 0.0f
                for (f in freqs) {
                    val vibrato = sin(2.0 * PI * 5.5 * t).toFloat() * 3.0f
                    s += sin(2.0 * PI * (f + vibrato) * t).toFloat() * 0.25f
                }
                buffer[i] = s * env * 0.8f
            }
            submitOneShot(buffer)
        }
    }

    fun triggerBleghDrop() {
        scope.launch {
            val durationMs = 700
            val samplesCount = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = FloatArray(samplesCount)
            // Vocal "Blegh" vocal choke followed by sub-bass slam
            for (i in 0 until samplesCount) {
                val t = i.toFloat() / SAMPLE_RATE
                val env = exp(-t * 4.0f).toFloat()
                val subFreq = 120.0f * exp(-t * 6.0f) + 35.0f
                val sub = sin(2.0 * PI * subFreq * t).toFloat()
                val grit = ((Math.random() * 2.0 - 1.0).toFloat()) * (if (t < 0.12f) 0.8f else 0.1f)
                var s = (sub * 0.8f + grit * 0.4f) * env
                s = (s * 1.8f).coerceIn(-1.0f, 1.0f)
                buffer[i] = s
            }
            submitOneShot(buffer)
        }
    }

    fun triggerGlitchLaser() {
        scope.launch {
            val durationMs = 300
            val samplesCount = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = FloatArray(samplesCount)
            for (i in 0 until samplesCount) {
                val t = i.toFloat() / SAMPLE_RATE
                val env = (1.0f - (i.toFloat() / samplesCount))
                val freq = 2400.0f * exp(-t * 12.0f) + 100.0f
                var s = sin(2.0 * PI * freq * t).toFloat()
                // Stepped bitcrush quantization
                s = (s * 4.0f).toInt() / 4.0f
                buffer[i] = s * env * 0.7f
            }
            submitOneShot(buffer)
        }
    }

    fun triggerSirenSqueal() {
        scope.launch {
            val durationMs = 600
            val samplesCount = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = FloatArray(samplesCount)
            for (i in 0 until samplesCount) {
                val t = i.toFloat() / SAMPLE_RATE
                val env = exp(-t * 2.5f).toFloat()
                val squealPitch = 1200.0f + 400.0f * sin(2.0 * PI * 14.0 * t).toFloat()
                val s = sin(2.0 * PI * squealPitch * t).toFloat()
                // Harmonic squeal distortion
                val distorted = sin(s * 3.0f).toFloat()
                buffer[i] = distorted * env * 0.6f
            }
            submitOneShot(buffer)
        }
    }

    private fun submitOneShot(buffer: FloatArray) {
        val pcm = ShortArray(buffer.size)
        var maxAmp = 0.0f
        for (i in buffer.indices) {
            val sample = (buffer[i] * masterVolume).coerceIn(-1.0f, 1.0f)
            pcm[i] = (sample * 32767.0f).toInt().toShort()
            val a = Math.abs(sample)
            if (a > maxAmp) maxAmp = a
        }
        audioQueue.offer(pcm)
        val spec = FloatArray(8) { (maxAmp * (0.6f + it * 0.05f)).coerceIn(0.1f, 1.0f) }
        onVisualizerCallback?.invoke(maxAmp, spec)
    }

    // --- Internal DSP Mixers ---

    private fun mixDjentChug(buffer: FloatArray, baseFreq: Float, amp: Float, longDecay: Boolean = false) {
        val decayRate = if (longDecay) 5.0f else 12.0f
        for (i in buffer.indices) {
            val t = i.toFloat() / SAMPLE_RATE
            val env = exp(-t * decayRate).toFloat()
            // Fundamental + 2nd sub-harmonic + 3rd harmonic
            val s1 = sin(2.0 * PI * baseFreq * t).toFloat()
            val s2 = sin(2.0 * PI * (baseFreq * 0.5) * t).toFloat() * 0.7f // Sub-octave chug
            val s3 = sin(2.0 * PI * (baseFreq * 2.0) * t).toFloat() * 0.5f
            val s4 = sin(2.0 * PI * (baseFreq * 3.0) * t).toFloat() * 0.3f
            var wave = (s1 + s2 + s3 + s4)
            // Heavy overdrive wavefolder
            wave = sin(wave * (2.0f + masterDistortion * 3.0f)).toFloat()
            buffer[i] += wave * env * amp
        }
    }

    private fun mixKick(buffer: FloatArray, amp: Float) {
        for (i in buffer.indices) {
            val t = i.toFloat() / SAMPLE_RATE
            val env = exp(-t * 22.0f).toFloat()
            val freq = 160.0f * exp(-t * 30.0f) + 42.0f
            val s = sin(2.0 * PI * freq * t).toFloat()
            // Drive saturation
            val driven = (s * 1.5f).coerceIn(-1.0f, 1.0f)
            buffer[i] += driven * env * amp
        }
    }

    private fun mixSnare(buffer: FloatArray, amp: Float) {
        for (i in buffer.indices) {
            val t = i.toFloat() / SAMPLE_RATE
            val bodyEnv = exp(-t * 26.0f).toFloat()
            val noiseEnv = exp(-t * 18.0f).toFloat()
            val body = sin(2.0 * PI * 185.0 * t).toFloat() * bodyEnv * 0.5f
            val noise = ((Math.random() * 2.0 - 1.0).toFloat()) * noiseEnv * 0.7f
            buffer[i] += (body + noise) * amp
        }
    }

    private fun mixHiHat(buffer: FloatArray, amp: Float) {
        for (i in buffer.indices) {
            val t = i.toFloat() / SAMPLE_RATE
            val env = exp(-t * 50.0f).toFloat()
            // High metallic noise
            val noise = ((Math.random() * 2.0 - 1.0).toFloat()) * env
            buffer[i] += noise * amp
        }
    }

    private fun mixAngelicPad(buffer: FloatArray, baseFreq: Float, amp: Float) {
        for (i in buffer.indices) {
            val t = i.toFloat() / SAMPLE_RATE
            val env = (1.0f - (i.toFloat() / buffer.size)) * 0.8f
            val vib = sin(2.0 * PI * 6.0 * t).toFloat() * 4.0f
            val s1 = sin(2.0 * PI * (baseFreq + vib) * t).toFloat() * 0.5f
            val s2 = sin(2.0 * PI * (baseFreq * 1.5f + vib) * t).toFloat() * 0.3f // 5th harmonic
            val s3 = sin(2.0 * PI * (baseFreq * 2.0f) * t).toFloat() * 0.2f
            buffer[i] += (s1 + s2 + s3) * env * amp
        }
    }

    private fun mixGlitchZap(buffer: FloatArray, amp: Float) {
        for (i in buffer.indices) {
            val t = i.toFloat() / SAMPLE_RATE
            val env = exp(-t * 25.0f).toFloat()
            val freq = 3000.0f * (1.0f - t * 5.0f).coerceAtLeast(0.1f)
            val s = sin(2.0 * PI * freq * t).toFloat()
            // Quantized glitch
            val bitcrushed = (s * 3.0f).toInt() / 3.0f
            buffer[i] += bitcrushed * env * amp
        }
    }

    fun release() {
        stopSequencer()
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
    }
}
