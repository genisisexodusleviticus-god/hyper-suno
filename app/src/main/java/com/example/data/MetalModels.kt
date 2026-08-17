package com.example.data

enum class VocalStyle(val label: String, val icon: String, val colorHex: Long) {
    DEMONIC_GROWL("Demonic Guttural", "👹", 0xFFFF0055),
    FALSE_CHORD_SCREAM("False Chord Screams", "🔥", 0xFFFF1744),
    PIG_SQUEAL("Eldritch Pig Squeal", "⚡", 0xFF9D00FF),
    ANGELIC_CLEAN("Celestial Angelic Highs", "🪽", 0xFF00F5D4),
    ETHEREAL_CHOIR("Gregorian Angel Choir", "✨", 0xFF00E5FF),
    DUAL_HARMONY("Demon-Angel Duality", "☯️", 0xFFB026FF),
    BLEGH_BREAKDOWN("Drop-Z Breakdown Chug", "💀", 0xFF39FF14)
}

enum class SectionType(val displayName: String) {
    INTRO("⚡ [INTRO: Eldritch Void Awakening]"),
    VERSE_DEMON("👹 [VERSE 1: Demonic Sub-Guttural Screams]"),
    PRE_CHORUS_ANGEL("🪽 [PRE-CHORUS: Angelic Falsetto Ascent]"),
    CHORUS_DUAL("☯️ [CHORUS: Demon-Angel Duality Riff]"),
    BREAKDOWN("💀 [BREAKDOWN: Drop-Z Chug Obliteration]"),
    GUITAR_SOLO("⚡ [SOLO: Hyper-Glitch Shred & Blast Beat]"),
    OUTRO("✨ [OUTRO: Celestial Apocalypse Fade]")
}

data class LyricSection(
    val sectionType: SectionType,
    val vocalStyle: VocalStyle,
    val screamAnnotation: String,
    val lyrics: String,
    val timestampStartSec: Int,
    val durationSec: Int
)

data class VideoScene(
    val id: String,
    val title: String,
    val sceneType: SceneType,
    val promptDescription: String,
    val durationSec: Float,
    val glitchIntensity: Float = 0.6f,
    val cameraShake: Float = 0.5f,
    val particleDensity: Float = 0.8f
)

enum class SceneType(val title: String, val themeColor: Long) {
    HELLFIRE_VOID("Hellfire Obsidian Void", 0xFFFF0055),
    ANGELIC_CATHEDRAL("Cathedral of Seraphs", 0xFF00F5D4),
    CYBER_BLOODPIT("Cybernetic Moshpit", 0xFF39FF14),
    COSMIC_EVENT_HORIZON("Eldritch Event Horizon", 0xFF9D00FF),
    GLITCH_CRYPT("Glitch Necropolis", 0xFFB026FF)
}

data class MetalTrack(
    val id: String,
    val title: String,
    val subgenre: String,
    val bpm: Int,
    val tuning: String,
    val demonAngelBalance: Float, // 0.0 = pure demon, 1.0 = pure angel, 0.5 = dual
    val lyrics: List<LyricSection>,
    val scenes: List<VideoScene>,
    val durationSeconds: Int = 180,
    val coverResId: Int? = null,
    val generatedAt: Long = System.currentTimeMillis()
)

data class BeatStep(
    var kick: Boolean = false,
    var snare: Boolean = false,
    var hihat: Boolean = false,
    var djentChug: Boolean = false,
    var glitchZap: Boolean = false,
    var angelPad: Boolean = false
)

data class DspSettings(
    var distortionGain: Float = 0.75f,
    var bitcrushDepth: Float = 0.40f,
    var pitchShiftSemitones: Int = -7,
    var reverbWet: Float = 0.65f,
    var glitchStutterRate: Float = 0.35f,
    var lowPassCutoffHz: Float = 3800f
)
