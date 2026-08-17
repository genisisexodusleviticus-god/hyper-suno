package com.example.data

import com.example.R
import java.util.UUID

object MetalAiGenerator {

    private val PRESET_TITLES = listOf(
        "SERAPHIM OBLIVION // 8-STRING HYPER-DJENT",
        "CYBER-BEHEMOTH IN DROP-F VOID",
        "ARCHANGEL PROTOCOL 666",
        "LUCIFERIAN FREQUENCY & CELESTIAL WRATH",
        "ELDRITCH CHUG MANIFESTO",
        "BAPHOMET'S HALO: SICK & TWISTED DUALITY",
        "VOID HYPER-DEATHCORE [SUNO 5.5 REMIX]"
    )

    private val SUBGENRES = listOf(
        "Cyber-Demonic Deathcore",
        "Angelic Blackened Symphonic Metal",
        "Hyper-Djent Mathcore",
        "Avant-Garde Void Doom",
        "Industrial Glitch Grindcore",
        "Celestial Post-Death Metal"
    )

    private val TUNINGS = listOf("Drop F (8-String)", "Drop A (7-String)", "Drop E Double Low", "Drop D# Void", "Drop G# Technical")

    val DEFAULT_TRACKS: List<MetalTrack> by lazy {
        listOf(
            createTrack(
                title = "SERAPHIM OBLIVION // HYPER-DJENT",
                subgenre = "Cyber-Demonic Deathcore",
                bpm = 220,
                tuning = "Drop F (8-String)",
                demonAngelBalance = 0.5f,
                coverRes = R.drawable.img_hero_demon_angel
            ),
            createTrack(
                title = "ARCHANGEL PROTOCOL 666",
                subgenre = "Angelic Blackened Symphonic Metal",
                bpm = 240,
                tuning = "Drop A (7-String)",
                demonAngelBalance = 0.7f,
                coverRes = R.drawable.img_cinematic_mosh
            ),
            createTrack(
                title = "VOID CHUG: THE SICK & TWISTED",
                subgenre = "Hyper-Djent Mathcore",
                bpm = 260,
                tuning = "Drop E Double Low",
                demonAngelBalance = 0.2f,
                coverRes = R.drawable.img_app_icon
            )
        )
    }

    fun generateTrack(prompt: String = "", customTitle: String? = null): MetalTrack {
        val title = customTitle ?: if (prompt.isNotBlank()) {
            prompt.uppercase().take(36) + " // SUNO 5.5"
        } else {
            PRESET_TITLES.random()
        }

        val subgenre = SUBGENRES.random()
        val bpm = (190..270 step 10).shuffled().first()
        val tuning = TUNINGS.random()
        val balance = (2..8).random() / 10.0f

        val coverRes = when ((1..3).random()) {
            1 -> R.drawable.img_hero_demon_angel
            2 -> R.drawable.img_cinematic_mosh
            else -> R.drawable.img_app_icon
        }

        return createTrack(title, subgenre, bpm, tuning, balance, coverRes, prompt)
    }

    private fun createTrack(
        title: String,
        subgenre: String,
        bpm: Int,
        tuning: String,
        demonAngelBalance: Float,
        coverRes: Int?,
        customTheme: String = ""
    ): MetalTrack {
        val themePhrase = if (customTheme.isNotBlank()) customTheme else "obsidian fractures and celestial choirs"

        val lyrics = listOf(
            LyricSection(
                sectionType = SectionType.INTRO,
                vocalStyle = VocalStyle.ETHEREAL_CHOIR,
                screamAnnotation = "[GREGORIAN ANGELIC VOCAL SWEEP]",
                lyrics = "In the fracture of the void, light bleeds turquoise...\nSanctus lucifer, seraphim burning in cyberspace...",
                timestampStartSec = 0,
                durationSec = 15
            ),
            LyricSection(
                sectionType = SectionType.VERSE_DEMON,
                vocalStyle = VocalStyle.DEMONIC_GROWL,
                screamAnnotation = "[SUB-HARMONIC GUTTURAL ROAR - DROP F]",
                lyrics = "WE TEAR THE FLESH OF TIME ASUNDER!\n8-STRING LOW-END GRAVITY COLLAPSE!\nDEMONIC CODE CORRUPTS THE PULSE OF CREATION!\n$themePhrase!",
                timestampStartSec = 15,
                durationSec = 25
            ),
            LyricSection(
                sectionType = SectionType.PRE_CHORUS_ANGEL,
                vocalStyle = VocalStyle.ANGELIC_CLEAN,
                screamAnnotation = "[FALSETTO HIGH CASCADES - CELESTIAL GLITCH]",
                lyrics = "Ascend above the neon static...\nHoly wings drenched in toxic emerald flame...\nWill we find salvation in the machine?",
                timestampStartSec = 40,
                durationSec = 20
            ),
            LyricSection(
                sectionType = SectionType.CHORUS_DUAL,
                vocalStyle = VocalStyle.DUAL_HARMONY,
                screamAnnotation = "[DUAL FALSE CHORD + OPERATIC SERAPH]",
                lyrics = "ANGELS IN CHAINS! DEMONS WITH HALOS!\nTHE SUNO 5.5 REVOLUTION REWRITES THE SKIES!\nPURPLE VOLTAGE, ACID GREEN BLOOD IN OUR VEINS!\nETERNAL RESONANCE IN THE VOID!",
                timestampStartSec = 60,
                durationSec = 30
            ),
            LyricSection(
                sectionType = SectionType.BREAKDOWN,
                vocalStyle = VocalStyle.BLEGH_BREAKDOWN,
                screamAnnotation = "[BLEGH + 320 BPM BLAST BEAT DOUBLE-BASS OBLITERATION]",
                lyrics = "*INHALE PIG SQUEAL* ... BLEGH!\nCHUG. CHUG-CHUG. CHUG-CHUG-CHUG-CHUG!\nOBLITERATE THE HORIZON!\nDROP-Z REVERBERATION!",
                timestampStartSec = 90,
                durationSec = 35
            ),
            LyricSection(
                sectionType = SectionType.GUITAR_SOLO,
                vocalStyle = VocalStyle.PIG_SQUEAL,
                screamAnnotation = "[HYPER-SPEED SWEEP PICKING & GLITCH ZAP FX]",
                lyrics = "[ARPEGGIOS AT LIGHT SPEED // 64TH NOTE WHAMMY DIVE-BOMBS]\n[CHROMATIC ABERRATION FEEDBACK FREQUENCY]",
                timestampStartSec = 125,
                durationSec = 30
            ),
            LyricSection(
                sectionType = SectionType.OUTRO,
                vocalStyle = VocalStyle.ETHEREAL_CHOIR,
                screamAnnotation = "[FADING CELESTIAL SHIMMER // VOID STATIC]",
                lyrics = "The battle is immortalized in silicon...\nDemon and angel merged as one hyper soundscape...\nAmen. Void. Amen.",
                timestampStartSec = 155,
                durationSec = 25
            )
        )

        val scenes = listOf(
            VideoScene(
                id = UUID.randomUUID().toString(),
                title = "SCENE I: THE AWAKENING",
                sceneType = SceneType.HELLFIRE_VOID,
                promptDescription = "Obsidian throne surrounded by violet lightning arcs, demonic silhouettes with jagged horns emerging from black smoke.",
                durationSec = 15f,
                glitchIntensity = 0.5f,
                cameraShake = 0.3f
            ),
            VideoScene(
                id = UUID.randomUUID().toString(),
                title = "SCENE II: DUAL ECLIPSE",
                sceneType = SceneType.ANGELIC_CATHEDRAL,
                promptDescription = "Cybernetic cathedral with stained glass windows radiating turquoise and acid green neon beams, six-winged seraph with chrome armor.",
                durationSec = 25f,
                glitchIntensity = 0.7f,
                cameraShake = 0.6f
            ),
            VideoScene(
                id = UUID.randomUUID().toString(),
                title = "SCENE III: THE MOSHPIT",
                sceneType = SceneType.CYBER_BLOODPIT,
                promptDescription = "Extreme metal moshpit in a toxic bio-reactor, strobing strobe lights, violent double-bass vibrations shaking the lens.",
                durationSec = 35f,
                glitchIntensity = 0.95f,
                cameraShake = 0.95f
            ),
            VideoScene(
                id = UUID.randomUUID().toString(),
                title = "SCENE IV: SINGULARITY",
                sceneType = SceneType.COSMIC_EVENT_HORIZON,
                promptDescription = "Black hole collapsing into a demonic mandala, angelic runes rotating in hyper-speed 3D orbit.",
                durationSec = 30f,
                glitchIntensity = 0.8f,
                cameraShake = 0.7f
            ),
            VideoScene(
                id = UUID.randomUUID().toString(),
                title = "SCENE V: GLITCH ETERNITY",
                sceneType = SceneType.GLITCH_CRYPT,
                promptDescription = "CRT monitor walls melting into digital acid green code, demon and angel fusing into a singular cyber-god.",
                durationSec = 25f,
                glitchIntensity = 1.0f,
                cameraShake = 0.4f
            )
        )

        return MetalTrack(
            id = UUID.randomUUID().toString(),
            title = title,
            subgenre = subgenre,
            bpm = bpm,
            tuning = tuning,
            demonAngelBalance = demonAngelBalance,
            lyrics = lyrics,
            scenes = scenes,
            durationSeconds = 180,
            coverResId = coverRes
        )
    }
}
