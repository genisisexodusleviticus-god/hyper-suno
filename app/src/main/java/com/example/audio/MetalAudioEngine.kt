package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.data.BeatStep
import com.example.data.MetalScale
import com.example.data.TrackChannelId
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
import kotlin.math.pow
import kotlin.math.sin

class MetalAudioEngine {

    companion object {
        const val SAMPLE_RATE = 44100
        const val PATTERN_COUNT = 4
        const val STEPS_PER_PATTERN = 16
        val INSTANCE: MetalAudioEngine by lazy { MetalAudioEngine() }

        // Standard A1 reference frequency for Drop A tuning (55 Hz)
        const val BASE_A1_FREQ = 55.0f
        const val BASE_SYNTH_A2_FREQ = 110.0f
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

    var swingAmount: Float = 0.0f // 0.0 = straight, 0.5 = heavy metal groove swing
    var activeScale: MetalScale = MetalScale.PHRYGIAN_DOMINANT

    // Pattern Banks: 0 = Pattern A, 1 = Pattern B, 2 = Pattern C, 3 = Pattern D
    var currentPatternIndex: Int = 0
    val patternBanks: Array<Array<BeatStep>> = Array(PATTERN_COUNT) {
        Array(STEPS_PER_PATTERN) { BeatStep() }
    }

    // Convenience accessor for active pattern
    val stepGrid: Array<BeatStep>
        get() = patternBanks[currentPatternIndex]

    // Track Mixing Matrix
    val trackVolumes = mutableMapOf<TrackChannelId, Float>().apply {
        TrackChannelId.values().forEach { put(it, 0.85f) }
    }
    val trackMutes = mutableMapOf<TrackChannelId, Boolean>().apply {
        TrackChannelId.values().forEach { put(it, false) }
    }
    val trackSolos = mutableMapOf<TrackChannelId, Boolean>().apply {
        TrackChannelId.values().forEach { put(it, false) }
    }

    // Synth parameters
    var synthWaveformIndex: Int = 0 // 0 = Sawtooth, 1 = Square/Pulse, 2 = Acid FM, 3 = Cyber Lead
    var synthFilterCutoff: Float = 0.75f // 0.1 to 1.0
    var synthResonance: Float = 0.5f

    // Master bus
    var masterVolume: Float = 0.9f
    var masterDistortion: Float = 0.6f

    // Stem volume backward compatibility for older screens
    var stemGuitarsVolume: Float
        get() = trackVolumes[TrackChannelId.DJENT_CHUG] ?: 1f
        set(value) { trackVolumes[TrackChannelId.DJENT_CHUG] = value }

    var stemDrumsVolume: Float
        get() = trackVolumes[TrackChannelId.BLAST_KICK] ?: 1f
        set(value) {
            trackVolumes[TrackChannelId.BLAST_KICK] = value
            trackVolumes[TrackChannelId.SNARE_CRACK] = value
            trackVolumes[TrackChannelId.HI_HAT] = value
            trackVolumes[TrackChannelId.CHINA_CYMBAL] = value
        }

    var stemAngelPadsVolume: Float
        get() = trackVolumes[TrackChannelId.ANGEL_PAD] ?: 1f
        set(value) { trackVolumes[TrackChannelId.ANGEL_PAD] = value }

    var stemGlitchFxVolume: Float
        get() = trackVolumes[TrackChannelId.GLITCH_FX] ?: 1f
        set(value) { trackVolumes[TrackChannelId.GLITCH_FX] = value }

    // Callbacks
    var onStepCallback: ((Int) -> Unit)? = null
    var onVisualizerCallback: ((amplitude: Float, spectrum: FloatArray) -> Unit)? = null

    init {
        initDefaultPatterns()
        initAudioTrack()
    }

    private fun initDefaultPatterns() {
        // Pattern 0: Blastbeat & Fast Djent Syncopation
        val p0 = patternBanks[0]
        for (i in 0 until STEPS_PER_PATTERN) {
            p0[i].kick = (i % 2 == 0)
            p0[i].isKickAccented = (i % 4 == 0)
            p0[i].snare = (i == 4 || i == 12)
            p0[i].isSnareAccented = (i == 4 || i == 12)
            p0[i].hihat = (i % 2 != 0)
            p0[i].china = (i == 0 || i == 8)
            p0[i].djentChug = (i == 0 || i == 3 || i == 6 || i == 9 || i == 10 || i == 14)
            p0[i].djentPitchIndex = if (i == 10) 2 else if (i == 14) 1 else 0
            p0[i].cyberSynth = (i % 2 == 0)
            p0[i].synthPitchIndex = (i / 2) % 8
            p0[i].angelPad = (i == 0 || i == 8)
            p0[i].subBass = (i == 0)
            p0[i].glitchZap = (i == 15 || i == 7)
        }

        // Pattern 1: Drop-Z Heavy Breakdown
        val p1 = patternBanks[1]
        for (i in 0 until STEPS_PER_PATTERN) {
            p1[i].kick = (i == 0 || i == 3 || i == 6 || i == 10 || i == 12)
            p1[i].isKickAccented = (i == 0 || i == 10)
            p1[i].snare = (i == 8)
            p1[i].isSnareAccented = true
            p1[i].hihat = (i % 2 == 0)
            p1[i].china = (i == 0 || i == 6 || i == 12)
            p1[i].djentChug = (i == 0 || i == 3 || i == 6 || i == 10 || i == 12)
            p1[i].djentPitchIndex = if (i == 12) 1 else 0
            p1[i].isDjentAccented = (i == 0 || i == 10)
            p1[i].cyberSynth = (i == 2 || i == 5 || i == 8 || i == 14)
            p1[i].synthPitchIndex = if (i == 14) 4 else 2
            p1[i].subBass = (i == 0 || i == 8)
            p1[i].glitchZap = (i == 7 || i == 15)
        }

        // Pattern 2: Polyrhythmic 7/8 Djent Riff
        val p2 = patternBanks[2]
        for (i in 0 until STEPS_PER_PATTERN) {
            p2[i].kick = (i == 0 || i == 3 || i == 5 || i == 8 || i == 11 || i == 14)
            p2[i].snare = (i == 4 || i == 12)
            p2[i].hihat = (i % 2 != 0)
            p2[i].china = (i == 0 || i == 8)
            p2[i].djentChug = (i == 0 || i == 3 || i == 5 || i == 8 || i == 11 || i == 14)
            p2[i].djentPitchIndex = (i % 5)
            p2[i].cyberSynth = (i == 1 || i == 4 || i == 7 || i == 10 || i == 13)
            p2[i].synthPitchIndex = (i % 7)
            p2[i].angelPad = (i == 0)
            p2[i].glitchZap = (i == 7)
        }

        // Pattern 3: Industrial Cyber Shred
        val p3 = patternBanks[3]
        for (i in 0 until STEPS_PER_PATTERN) {
            p3[i].kick = (i % 4 == 0)
            p3[i].snare = (i % 4 == 2)
            p3[i].hihat = true
            p3[i].china = (i == 14)
            p3[i].djentChug = (i % 2 == 0)
            p3[i].djentPitchIndex = if (i > 8) 3 else 0
            p3[i].cyberSynth = true
            p3[i].synthPitchIndex = (i * 2) % 8
            p3[i].isSynthAccented = (i % 4 == 0)
            p3[i].angelPad = (i == 0 || i == 8)
            p3[i].subBass = (i == 0)
            p3[i].glitchZap = (i == 15)
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

    fun copyPattern(fromIndex: Int, toIndex: Int) {
        if (fromIndex in 0 until PATTERN_COUNT && toIndex in 0 until PATTERN_COUNT) {
            for (i in 0 until STEPS_PER_PATTERN) {
                val src = patternBanks[fromIndex][i]
                patternBanks[toIndex][i] = src.copy()
            }
        }
    }

    fun clearPattern(patternIdx: Int) {
        if (patternIdx in 0 until PATTERN_COUNT) {
            for (i in 0 until STEPS_PER_PATTERN) {
                patternBanks[patternIdx][i] = BeatStep()
            }
        }
    }

    fun isTrackAudible(channelId: TrackChannelId): Boolean {
        val hasSolo = trackSolos.values.any { it }
        if (hasSolo) {
            return trackSolos[channelId] == true
        }
        return trackMutes[channelId] != true
    }

    fun getTrackVolume(channelId: TrackChannelId): Float {
        if (!isTrackAudible(channelId)) return 0.0f
        return (trackVolumes[channelId] ?: 0.85f).coerceIn(0f, 1f)
    }

    fun getScalePitchFrequency(pitchIndex: Int, baseFreq: Float, octaveOffset: Int = 0): Float {
        val semitoneList = activeScale.semitones
        val wrappedIdx = (pitchIndex % semitoneList.size).coerceAtLeast(0)
        val octaveExtra = pitchIndex / semitoneList.size + octaveOffset
        val semitones = semitoneList[wrappedIdx] + octaveExtra * 12
        return baseFreq * 2.0.pow(semitones / 12.0).toFloat()
    }

    fun startSequencer() {
        if (isPlaying) return
        isPlaying = true
        playbackJob = scope.launch {
            while (isActive && isPlaying) {
                val baseStepMs = (60_000.0 / bpm) / 4.0 // 16th note in ms
                // Swing calculation: delay odd steps slightly
                val stepDurationMs = if (currentStep % 2 == 1 && swingAmount > 0.01f) {
                    (baseStepMs * (1.0 + swingAmount * 0.45)).toLong()
                } else if (currentStep % 2 == 0 && swingAmount > 0.01f) {
                    (baseStepMs * (1.0 - swingAmount * 0.45)).toLong().coerceAtLeast(10L)
                } else {
                    baseStepMs.toLong()
                }

                val step = stepGrid[currentStep]
                val samplesCount = (SAMPLE_RATE * (stepDurationMs / 1000.0)).toInt().coerceAtLeast(256)
                val buffer = FloatArray(samplesCount)

                // 1. Double Blast Kick
                val kickVol = getTrackVolume(TrackChannelId.BLAST_KICK)
                if (step.kick && kickVol > 0.01f) {
                    val amp = if (step.isKickAccented) 1.25f else 0.85f
                    mixKick(buffer, amp * kickVol)
                }

                // 2. Snare Crack
                val snareVol = getTrackVolume(TrackChannelId.SNARE_CRACK)
                if (step.snare && snareVol > 0.01f) {
                    val amp = if (step.isSnareAccented) 1.15f else 0.80f
                    mixSnare(buffer, amp * snareVol)
                }

                // 3. Hi-Hat
                val hihatVol = getTrackVolume(TrackChannelId.HI_HAT)
                if (step.hihat && hihatVol > 0.01f) {
                    mixHiHat(buffer, 0.45f * hihatVol)
                }

                // 4. China Cymbal / Crash Blast
                val chinaVol = getTrackVolume(TrackChannelId.CHINA_CYMBAL)
                if (step.china && chinaVol > 0.01f) {
                    mixChinaCymbal(buffer, 0.70f * chinaVol)
                }

                // 5. 8-String Djent Chug Riff
                val djentVol = getTrackVolume(TrackChannelId.DJENT_CHUG)
                if (step.djentChug && djentVol > 0.01f) {
                    val pitchFreq = getScalePitchFrequency(step.djentPitchIndex, BASE_A1_FREQ)
                    val amp = if (step.isDjentAccented) 1.2f else 0.85f
                    mixDjentChug(buffer, pitchFreq, amp * djentVol * (1f + masterDistortion * 0.4f))
                }

                // 6. Cyber Acid Synth / Lead
                val synthVol = getTrackVolume(TrackChannelId.CYBER_SYNTH)
                if (step.cyberSynth && synthVol > 0.01f) {
                    val pitchFreq = getScalePitchFrequency(step.synthPitchIndex, BASE_SYNTH_A2_FREQ, step.synthOctave)
                    val amp = if (step.isSynthAccented) 1.1f else 0.75f
                    mixCyberSynth(buffer, pitchFreq, amp * synthVol)
                }

                // 7. Angelic Choir / Celestial Pad
                val angelVol = getTrackVolume(TrackChannelId.ANGEL_PAD)
                if (step.angelPad && angelVol > 0.01f) {
                    val padPitch = if (currentStep < 8) 440.0f else 523.25f
                    mixAngelicPad(buffer, padPitch, 0.50f * angelVol)
                }

                // 8. 808 Sub-Bass Slam
                val subVol = getTrackVolume(TrackChannelId.SUB_BASS)
                if (step.subBass && subVol > 0.01f) {
                    mixSubBass(buffer, 42.0f, 0.90f * subVol)
                }

                // 9. Glitch / Blegh FX
                val glitchVol = getTrackVolume(TrackChannelId.GLITCH_FX)
                if (step.glitchZap && glitchVol > 0.01f) {
                    mixGlitchZap(buffer, 0.65f * glitchVol)
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
                    val factor = when {
                        (step.djentChug || step.subBass) && b < 3 -> 0.95f
                        step.kick && b < 2 -> 0.90f
                        step.snare && (b == 3 || b == 4) -> 0.85f
                        step.cyberSynth && (b == 4 || b == 5) -> 0.90f
                        step.angelPad && b >= 5 -> 0.75f
                        step.china || step.hihat -> 0.70f
                        step.glitchZap -> 0.85f
                        else -> 0.15f
                    }
                    fakeSpectrum[b] = (maxAmp * factor).coerceIn(0.05f, 1.0f)
                }

                audioQueue.offer(pcmBuffer)
                onStepCallback?.invoke(currentStep)
                onVisualizerCallback?.invoke(maxAmp, fakeSpectrum)

                currentStep = (currentStep + 1) % STEPS_PER_PATTERN
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

    // --- One-Shot Live Sound Trigger & Audition Functions ---

    fun triggerChannelSound(channelId: TrackChannelId, pitchIndex: Int = 0) {
        scope.launch {
            val durationMs = 380
            val samplesCount = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = FloatArray(samplesCount)

            when (channelId) {
                TrackChannelId.BLAST_KICK -> mixKick(buffer, 1.0f)
                TrackChannelId.SNARE_CRACK -> mixSnare(buffer, 0.9f)
                TrackChannelId.HI_HAT -> mixHiHat(buffer, 0.6f)
                TrackChannelId.CHINA_CYMBAL -> mixChinaCymbal(buffer, 0.8f)
                TrackChannelId.DJENT_CHUG -> {
                    val freq = getScalePitchFrequency(pitchIndex, BASE_A1_FREQ)
                    mixDjentChug(buffer, freq, 0.95f, longDecay = true)
                }
                TrackChannelId.CYBER_SYNTH -> {
                    val freq = getScalePitchFrequency(pitchIndex, BASE_SYNTH_A2_FREQ)
                    mixCyberSynth(buffer, freq, 0.9f)
                }
                TrackChannelId.ANGEL_PAD -> mixAngelicPad(buffer, 440f, 0.7f)
                TrackChannelId.SUB_BASS -> mixSubBass(buffer, 45f, 1.0f)
                TrackChannelId.GLITCH_FX -> mixGlitchZap(buffer, 0.8f)
            }

            submitOneShot(buffer)
        }
    }

    fun triggerDjentChug(lowNote: Boolean = true) {
        scope.launch {
            val durationMs = 350
            val samplesCount = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = FloatArray(samplesCount)
            val pitch = if (lowNote) 48.99f else 65.41f
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
                val mod = sin(2.0 * PI * 18.0 * t).toFloat()
                val carrier = sin(2.0 * PI * (65.0 + mod * 30.0) * t).toFloat()
                val noise = ((Math.random() * 2.0 - 1.0).toFloat()) * 0.4f
                var s = (carrier + noise) * env * 0.9f
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
            val freqs = floatArrayOf(440f, 554.37f, 659.25f, 880f)
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

    // --- Internal Multi-Instrument DSP Mixers ---

    private fun mixDjentChug(buffer: FloatArray, baseFreq: Float, amp: Float, longDecay: Boolean = false) {
        val decayRate = if (longDecay) 5.0f else 11.0f
        for (i in buffer.indices) {
            val t = i.toFloat() / SAMPLE_RATE
            val env = exp(-t * decayRate).toFloat()
            val s1 = sin(2.0 * PI * baseFreq * t).toFloat()
            val s2 = sin(2.0 * PI * (baseFreq * 0.5) * t).toFloat() * 0.7f
            val s3 = sin(2.0 * PI * (baseFreq * 2.0) * t).toFloat() * 0.5f
            val s4 = sin(2.0 * PI * (baseFreq * 3.0) * t).toFloat() * 0.3f
            var wave = (s1 + s2 + s3 + s4)
            wave = sin(wave * (2.0f + masterDistortion * 3.2f)).toFloat()
            buffer[i] += wave * env * amp
        }
    }

    private fun mixCyberSynth(buffer: FloatArray, freq: Float, amp: Float) {
        val cutoff = synthFilterCutoff.coerceIn(0.1f, 1.0f)
        for (i in buffer.indices) {
            val t = i.toFloat() / SAMPLE_RATE
            val phase = (freq * t) % 1.0f
            val env = exp(-t * (8.0f / cutoff)).toFloat()

            var rawWave = when (synthWaveformIndex) {
                0 -> (2.0f * phase - 1.0f) // Sawtooth
                1 -> if (phase < 0.5f) 0.8f else -0.8f // Square/Pulse
                2 -> {
                    // Acid FM
                    val mod = sin(2.0 * PI * (freq * 2.0) * t).toFloat() * synthResonance * 2.0f
                    sin(2.0 * PI * freq * t + mod).toFloat()
                }
                else -> {
                    // Cyber Lead (Saw + Sub-octave Sine)
                    val saw = 2.0f * phase - 1.0f
                    val sub = sin(PI * freq * t).toFloat() * 0.5f
                    saw * 0.7f + sub
                }
            }

            // Resonant Filter Simulation
            rawWave = (rawWave * (1.0f + synthResonance * 1.5f)).coerceIn(-1.2f, 1.2f)
            buffer[i] += rawWave * env * amp * 0.65f
        }
    }

    private fun mixKick(buffer: FloatArray, amp: Float) {
        for (i in buffer.indices) {
            val t = i.toFloat() / SAMPLE_RATE
            val env = exp(-t * 22.0f).toFloat()
            val freq = 160.0f * exp(-t * 32.0f) + 42.0f
            val s = sin(2.0 * PI * freq * t).toFloat()
            val click = if (t < 0.008f) ((Math.random() * 2.0 - 1.0).toFloat()) * 0.4f else 0.0f
            val driven = ((s + click) * 1.6f).coerceIn(-1.0f, 1.0f)
            buffer[i] += driven * env * amp
        }
    }

    private fun mixSnare(buffer: FloatArray, amp: Float) {
        for (i in buffer.indices) {
            val t = i.toFloat() / SAMPLE_RATE
            val bodyEnv = exp(-t * 26.0f).toFloat()
            val noiseEnv = exp(-t * 18.0f).toFloat()
            val body = sin(2.0 * PI * 190.0 * t).toFloat() * bodyEnv * 0.5f
            val noise = ((Math.random() * 2.0 - 1.0).toFloat()) * noiseEnv * 0.7f
            buffer[i] += (body + noise) * amp
        }
    }

    private fun mixHiHat(buffer: FloatArray, amp: Float) {
        for (i in buffer.indices) {
            val t = i.toFloat() / SAMPLE_RATE
            val env = exp(-t * 50.0f).toFloat()
            val noise = ((Math.random() * 2.0 - 1.0).toFloat()) * env
            buffer[i] += noise * amp
        }
    }

    private fun mixChinaCymbal(buffer: FloatArray, amp: Float) {
        for (i in buffer.indices) {
            val t = i.toFloat() / SAMPLE_RATE
            val env = exp(-t * 8.0f).toFloat() // Slower trashy cymbal decay
            val metallicTone = sin(2.0 * PI * 4200.0 * t).toFloat() * 0.3f + sin(2.0 * PI * 6800.0 * t).toFloat() * 0.2f
            val whiteNoise = ((Math.random() * 2.0 - 1.0).toFloat()) * 0.7f
            val trashWave = (metallicTone + whiteNoise) * env
            buffer[i] += trashWave * amp
        }
    }

    private fun mixAngelicPad(buffer: FloatArray, baseFreq: Float, amp: Float) {
        for (i in buffer.indices) {
            val t = i.toFloat() / SAMPLE_RATE
            val env = (1.0f - (i.toFloat() / buffer.size)) * 0.8f
            val vib = sin(2.0 * PI * 6.0 * t).toFloat() * 4.0f
            val s1 = sin(2.0 * PI * (baseFreq + vib) * t).toFloat() * 0.5f
            val s2 = sin(2.0 * PI * (baseFreq * 1.5f + vib) * t).toFloat() * 0.3f
            val s3 = sin(2.0 * PI * (baseFreq * 2.0f) * t).toFloat() * 0.2f
            buffer[i] += (s1 + s2 + s3) * env * amp
        }
    }

    private fun mixSubBass(buffer: FloatArray, freq: Float, amp: Float) {
        for (i in buffer.indices) {
            val t = i.toFloat() / SAMPLE_RATE
            val env = exp(-t * 5.0f).toFloat()
            val subFreq = freq * exp(-t * 2.0f) + 32.0f
            val sine = sin(2.0 * PI * subFreq * t).toFloat()
            val saturated = (sine * 1.4f).coerceIn(-1.0f, 1.0f)
            buffer[i] += saturated * env * amp
        }
    }

    private fun mixGlitchZap(buffer: FloatArray, amp: Float) {
        for (i in buffer.indices) {
            val t = i.toFloat() / SAMPLE_RATE
            val env = exp(-t * 25.0f).toFloat()
            val freq = 3000.0f * (1.0f - t * 5.0f).coerceAtLeast(0.1f)
            val s = sin(2.0 * PI * freq * t).toFloat()
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
