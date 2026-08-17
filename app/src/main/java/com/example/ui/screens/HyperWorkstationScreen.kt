package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.MetalAudioEngine
import com.example.data.GeminiClient
import com.example.data.LyricSection
import com.example.data.MetalAiGenerator
import com.example.data.MetalTrack
import com.example.ui.components.AudioSpectrumVisualizer
import com.example.ui.components.CrtScanlineOverlay
import com.example.ui.components.CyberMetalCard
import com.example.ui.components.DemonicAngelicRunicVisualizer
import com.example.ui.components.GlitchText
import com.example.ui.components.NeonMetalButton
import com.example.ui.theme.AcidGreen
import com.example.ui.theme.AngelAqua
import com.example.ui.theme.AngelicGold
import com.example.ui.theme.BloodCrimson
import com.example.ui.theme.CyberTurquoise
import com.example.ui.theme.DeepAbyss
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.HellfireRed
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.NeonWhite
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceGlass
import com.example.ui.theme.SurfaceHighlight
import com.example.ui.theme.ToxicGreen
import com.example.ui.theme.VoidBlack
import kotlinx.coroutines.launch

@Composable
fun HyperWorkstationScreen(
    currentTrack: MetalTrack,
    onTrackChanged: (MetalTrack) -> Unit,
    audioEngine: MetalAudioEngine,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    currentStep: Int,
    visualizerAmp: Float,
    visualizerSpectrum: FloatArray,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var promptInput by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    var activeLyricsTab by remember { mutableStateOf(0) } // 0 = Composition & Lyrics, 1 = Stem Mixer & Stems

    // Generator Preset Quick Selectors
    val presetPrompts = listOf(
        "Demon vs Archangel Cyber-Warfare in Drop-F",
        "Eldritch Djent breakdown with 320 BPM blast beat",
        "Angelic Seraphim choir burning in acid green void",
        "Luciferian hyper-deathcore with 8-string polyrhythms"
    )

    // Stem Mute states
    var guitarsMuted by remember { mutableStateOf(false) }
    var drumsMuted by remember { mutableStateOf(false) }
    var angelPadsMuted by remember { mutableStateOf(false) }
    var glitchFxMuted by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            // Hero Brand Banner with Demon/Angel Cover
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp))
                    .border(
                        1.5.dp,
                        Brush.horizontalGradient(listOf(ElectricPurple, AcidGreen, CyberTurquoise)),
                        CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp)
                    )
            ) {
                val coverId = currentTrack.coverResId ?: R.drawable.img_hero_demon_angel
                Image(
                    painter = painterResource(id = coverId),
                    contentDescription = "Track Artwork",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark Glitch Gradient & Scanline Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, VoidBlack.copy(alpha = 0.85f), VoidBlack)
                            )
                        )
                )
                CrtScanlineOverlay(modifier = Modifier.fillMaxSize(), scanlineAlpha = 0.25f)

                // Overlay Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = ElectricPurple.copy(alpha = 0.85f)
                        ) {
                            Text(
                                text = "SUNO 5.5 HYPER ENGINE",
                                color = NeonWhite,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = ToxicGreen.copy(alpha = 0.25f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ToxicGreen)
                        ) {
                            Text(
                                text = "${currentTrack.bpm} BPM // ${currentTrack.tuning}",
                                color = ToxicGreen,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Column {
                        GlitchText(
                            text = currentTrack.title,
                            fontSize = 18,
                            color = NeonWhite
                        )
                        Text(
                            text = "SUBGENRE: ${currentTrack.subgenre.uppercase()}",
                            color = CyberTurquoise,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        // Live Audio Spectrum Analyzer & Transport Controls
        item {
            CyberMetalCard(
                borderColor = ElectricPurple,
                accentColor = AcidGreen
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = if (isPlaying) ToxicGreen else MutedSlate,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isPlaying) "PCM SYNTH: ACTIVE (STEP ${currentStep + 1}/16)" else "PCM SYNTH: STANDBY",
                                color = if (isPlaying) ToxicGreen else MutedSlate,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Text(
                            text = "96kHz 32-BIT",
                            color = CyberTurquoise,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 8-Band Audio Spectrum
                    AudioSpectrumVisualizer(
                        spectrum = visualizerSpectrum,
                        primaryColor = ElectricPurple,
                        secondaryColor = ToxicGreen
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Transport Play/Pause + Tempo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NeonMetalButton(
                            text = if (isPlaying) "PAUSE BLAST" else "PLAY HYPER TRACK",
                            iconText = if (isPlaying) "⏸" else "▶",
                            onClick = onTogglePlay,
                            primaryColor = if (isPlaying) HellfireRed else ElectricPurple,
                            accentColor = if (isPlaying) BloodCrimson else AcidGreen,
                            modifier = Modifier.weight(1f),
                            testTagId = "btn_play_track"
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        // Quick Demon Riff Trigger
                        NeonMetalButton(
                            text = "CHUG!",
                            iconText = "🎸",
                            onClick = { audioEngine.triggerDjentChug(true) },
                            primaryColor = DeepAbyss,
                            accentColor = CyberTurquoise,
                            testTagId = "btn_quick_chug"
                        )
                    }
                }
            }
        }

        // Suno 5.5 AI Prompt Generator Studio
        item {
            CyberMetalCard(
                borderColor = AcidGreen,
                accentColor = CyberTurquoise
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = AcidGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SUNO 5.5 AI GENERATOR",
                                color = AcidGreen,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Text(
                            text = "GEMINI 3.5 FLASH",
                            color = AngelicGold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_metal_prompt"),
                        placeholder = {
                            Text(
                                text = "Enter theme e.g. 'Demonic Archangel cyber war in Drop-F'...",
                                color = MutedSlate,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AcidGreen,
                            unfocusedBorderColor = ElectricPurple.copy(alpha = 0.5f),
                            focusedContainerColor = SurfaceDark,
                            unfocusedContainerColor = SurfaceDark,
                            focusedTextColor = NeonWhite,
                            unfocusedTextColor = NeonWhite
                        ),
                        shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                        singleLine = false,
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Prompt Preset Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presetPrompts.take(2).forEach { preset ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = SurfaceContainer,
                                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricPurple.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { promptInput = preset }
                            ) {
                                Text(
                                    text = preset,
                                    color = MutedSlate,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Generate Button
                    NeonMetalButton(
                        text = if (isGenerating) "AI SYNTHESIZING OPUS..." else "GENERATE HYPER METAL OPUS",
                        iconText = if (isGenerating) "⏳" else "⚡",
                        onClick = {
                            scope.launch {
                                isGenerating = true
                                // Try Gemini AI or smart procedural composition
                                val aiResult = GeminiClient.generateExtremeMetalTrack(promptInput)
                                val newTrack = MetalAiGenerator.generateTrack(
                                    prompt = promptInput,
                                    customTitle = if (aiResult != null && aiResult.lines().isNotEmpty()) {
                                        aiResult.lines().firstOrNull { it.contains("Title", ignoreCase = true) }
                                            ?.replace("Title:", "")?.trim()?.take(36)
                                    } else null
                                )
                                onTrackChanged(newTrack)
                                isGenerating = false
                            }
                        },
                        primaryColor = AcidGreen,
                        accentColor = ElectricPurple,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isGenerating,
                        testTagId = "btn_generate_metal_track"
                    )
                }
            }
        }

        // Section Tabs: Lyrics & Structure vs Stem Mixer
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark, CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp))
                    .padding(4.dp)
            ) {
                Surface(
                    shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
                    color = if (activeLyricsTab == 0) ElectricPurple else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeLyricsTab = 0 }
                ) {
                    Text(
                        text = "📜 SICK & TWISTED LYRICS",
                        color = if (activeLyricsTab == 0) NeonWhite else MutedSlate,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }

                Surface(
                    shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
                    color = if (activeLyricsTab == 1) CyberTurquoise else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeLyricsTab = 1 }
                ) {
                    Text(
                        text = "🎛️ STEM MIXER & DSP",
                        color = if (activeLyricsTab == 1) VoidBlack else MutedSlate,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
            }
        }

        // Tab 0: Lyrics & Vocal Structure
        if (activeLyricsTab == 0) {
            itemsIndexed(currentTrack.lyrics) { index, section ->
                LyricSectionCard(section = section, index = index)
            }
        } else {
            // Tab 1: 6-Track Stem Mixer Deck
            item {
                CyberMetalCard(borderColor = CyberTurquoise, accentColor = ElectricPurple) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "EXTREME METAL STEM CONTROLS",
                            color = CyberTurquoise,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // 1. Guitars (Drop-F Djent)
                        StemControlRow(
                            name = "8-STRING DJENT GUITARS",
                            icon = "🎸",
                            color = HellfireRed,
                            isMuted = guitarsMuted,
                            volume = audioEngine.stemGuitarsVolume,
                            onToggleMute = {
                                guitarsMuted = !guitarsMuted
                                audioEngine.stemGuitarsVolume = if (guitarsMuted) 0f else 1f
                            },
                            onVolumeChange = {
                                audioEngine.stemGuitarsVolume = it
                                guitarsMuted = it < 0.05f
                            }
                        )

                        // 2. Blast Drums
                        StemControlRow(
                            name = "DOUBLE-BASS BLAST DRUMS",
                            icon = "🥁",
                            color = ToxicGreen,
                            isMuted = drumsMuted,
                            volume = audioEngine.stemDrumsVolume,
                            onToggleMute = {
                                drumsMuted = !drumsMuted
                                audioEngine.stemDrumsVolume = if (drumsMuted) 0f else 1f
                            },
                            onVolumeChange = {
                                audioEngine.stemDrumsVolume = it
                                drumsMuted = it < 0.05f
                            }
                        )

                        // 3. Angelic Pads
                        StemControlRow(
                            name = "CELESTIAL ANGEL PADS",
                            icon = "🪽",
                            color = AngelAqua,
                            isMuted = angelPadsMuted,
                            volume = audioEngine.stemAngelPadsVolume,
                            onToggleMute = {
                                angelPadsMuted = !angelPadsMuted
                                audioEngine.stemAngelPadsVolume = if (angelPadsMuted) 0f else 1f
                            },
                            onVolumeChange = {
                                audioEngine.stemAngelPadsVolume = it
                                angelPadsMuted = it < 0.05f
                            }
                        )

                        // 4. Glitch & Industrial FX
                        StemControlRow(
                            name = "CYBER GLITCH & SUB FX",
                            icon = "👾",
                            color = NeonViolet,
                            isMuted = glitchFxMuted,
                            volume = audioEngine.stemGlitchFxVolume,
                            onToggleMute = {
                                glitchFxMuted = !glitchFxMuted
                                audioEngine.stemGlitchFxVolume = if (glitchFxMuted) 0f else 1f
                            },
                            onVolumeChange = {
                                audioEngine.stemGlitchFxVolume = it
                                glitchFxMuted = it < 0.05f
                            }
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun LyricSectionCard(section: LyricSection, index: Int) {
    val styleColor = Color(section.vocalStyle.colorHex)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp))
            .background(SurfaceDark)
            .border(1.dp, styleColor.copy(alpha = 0.4f), CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = section.sectionType.displayName,
                    color = styleColor,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                Surface(
                    shape = RoundedCornerShape(3.dp),
                    color = styleColor.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, styleColor)
                ) {
                    Text(
                        text = section.vocalStyle.label,
                        color = NeonWhite,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = section.screamAnnotation,
                color = AngelicGold,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = section.lyrics,
                color = NeonWhite,
                fontFamily = FontFamily.Default,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        }
    }
}

@Composable
fun StemControlRow(
    name: String,
    icon: String,
    color: Color,
    isMuted: Boolean,
    volume: Float,
    onToggleMute: () -> Unit,
    onVolumeChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 16.sp, modifier = Modifier.width(28.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                color = if (isMuted) MutedSlate else color,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
            Slider(
                value = if (isMuted) 0f else volume,
                onValueChange = onVolumeChange,
                colors = SliderDefaults.colors(
                    thumbColor = color,
                    activeTrackColor = color,
                    inactiveTrackColor = SurfaceContainer
                ),
                modifier = Modifier.height(24.dp)
            )
        }

        IconButton(
            onClick = onToggleMute,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                contentDescription = "Mute Stem",
                tint = if (isMuted) HellfireRed else color,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
