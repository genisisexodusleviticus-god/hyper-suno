package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.MetalAudioEngine
import com.example.data.MetalScale
import com.example.data.TrackCategory
import com.example.data.TrackChannelId
import com.example.ui.components.CyberMetalCard
import com.example.ui.components.GlitchText
import com.example.ui.components.NeonMetalButton
import com.example.ui.theme.AcidGreen
import com.example.ui.theme.AngelAqua
import com.example.ui.theme.AngelicGold
import com.example.ui.theme.BloodCrimson
import com.example.ui.theme.CelestialCyan
import com.example.ui.theme.CyberTurquoise
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DeepAbyss
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.GlitchMagenta
import com.example.ui.theme.HellfireRed
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.NeonWhite
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceHighlight
import com.example.ui.theme.ToxicGreen
import com.example.ui.theme.VoidBlack
import kotlin.random.Random

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StepSequencerScreen(
    audioEngine: MetalAudioEngine,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    var bpmState by remember { mutableIntStateOf(audioEngine.bpm) }
    var swingState by remember { mutableStateOf(audioEngine.swingAmount) }
    var activeScaleState by remember { mutableStateOf(audioEngine.activeScale) }
    var selectedPattern by remember { mutableIntStateOf(audioEngine.currentPatternIndex) }
    var activeCategoryFilter by remember { mutableStateOf<TrackCategory?>(null) }
    var refreshKey by remember { mutableIntStateOf(0) }

    // Tap tempo state
    var lastTapTime by remember { mutableLongStateOf(0L) }
    val tapIntervals = remember { mutableStateListOf<Long>() }

    // Pitch & synth note editor state
    var pitchPickerTrack by remember { mutableStateOf<TrackChannelId?>(null) }
    var pitchPickerStep by remember { mutableIntStateOf(-1) }
    var showScaleDialog by remember { mutableStateOf(false) }
    var showSynthEngineDrawer by remember { mutableStateOf(false) }
    var showTrackActionsFor by remember { mutableStateOf<TrackChannelId?>(null) }

    // Preset Loaders
    fun loadPreset(presetName: String) {
        val currentGrid = audioEngine.stepGrid
        when (presetName) {
            "BLASTBEAT 280" -> {
                audioEngine.bpm = 280
                bpmState = 280
                for (i in 0 until 16) {
                    currentGrid[i].kick = (i % 2 == 0)
                    currentGrid[i].isKickAccented = (i % 4 == 0)
                    currentGrid[i].snare = (i == 4 || i == 12)
                    currentGrid[i].isSnareAccented = true
                    currentGrid[i].hihat = true
                    currentGrid[i].china = (i == 0 || i == 8)
                    currentGrid[i].djentChug = (i % 4 == 0)
                    currentGrid[i].djentPitchIndex = 0
                    currentGrid[i].cyberSynth = (i % 2 == 0)
                    currentGrid[i].synthPitchIndex = (i / 2) % 8
                    currentGrid[i].angelPad = (i == 0 || i == 8)
                    currentGrid[i].subBass = (i == 0)
                    currentGrid[i].glitchZap = (i == 15)
                }
            }
            "DROP-Z BREAKDOWN" -> {
                audioEngine.bpm = 160
                bpmState = 160
                for (i in 0 until 16) {
                    currentGrid[i].kick = (i == 0 || i == 3 || i == 6 || i == 10 || i == 12)
                    currentGrid[i].isKickAccented = (i == 0 || i == 10)
                    currentGrid[i].snare = (i == 8)
                    currentGrid[i].isSnareAccented = true
                    currentGrid[i].hihat = (i % 2 == 0)
                    currentGrid[i].china = (i == 0 || i == 6 || i == 12)
                    currentGrid[i].djentChug = (i == 0 || i == 3 || i == 6 || i == 10 || i == 12)
                    currentGrid[i].djentPitchIndex = if (i == 12) 1 else 0
                    currentGrid[i].isDjentAccented = (i == 0 || i == 10)
                    currentGrid[i].cyberSynth = (i == 2 || i == 5 || i == 8 || i == 14)
                    currentGrid[i].synthPitchIndex = if (i == 14) 4 else 2
                    currentGrid[i].subBass = (i == 0 || i == 8)
                    currentGrid[i].angelPad = false
                    currentGrid[i].glitchZap = (i == 7 || i == 15)
                }
            }
            "DJENT 7/8" -> {
                audioEngine.bpm = 210
                bpmState = 210
                for (i in 0 until 16) {
                    currentGrid[i].kick = (i == 0 || i == 3 || i == 5 || i == 8 || i == 11 || i == 14)
                    currentGrid[i].isKickAccented = (i == 0 || i == 8)
                    currentGrid[i].snare = (i == 4 || i == 12)
                    currentGrid[i].hihat = (i % 2 != 0)
                    currentGrid[i].china = (i == 0 || i == 8)
                    currentGrid[i].djentChug = (i == 0 || i == 3 || i == 5 || i == 8 || i == 11 || i == 14)
                    currentGrid[i].djentPitchIndex = (i % 5)
                    currentGrid[i].cyberSynth = (i == 1 || i == 4 || i == 7 || i == 10 || i == 13)
                    currentGrid[i].synthPitchIndex = (i % 7)
                    currentGrid[i].angelPad = (i == 0)
                    currentGrid[i].subBass = (i == 0)
                    currentGrid[i].glitchZap = (i == 7)
                }
            }
            "INDUSTRIAL CYBER" -> {
                audioEngine.bpm = 240
                bpmState = 240
                for (i in 0 until 16) {
                    currentGrid[i].kick = (i % 4 == 0)
                    currentGrid[i].snare = (i % 4 == 2)
                    currentGrid[i].hihat = true
                    currentGrid[i].china = (i == 14)
                    currentGrid[i].djentChug = (i % 2 == 0)
                    currentGrid[i].djentPitchIndex = if (i > 8) 3 else 0
                    currentGrid[i].cyberSynth = true
                    currentGrid[i].synthPitchIndex = (i * 2) % 8
                    currentGrid[i].isSynthAccented = (i % 4 == 0)
                    currentGrid[i].angelPad = (i == 0 || i == 8)
                    currentGrid[i].subBass = (i == 0)
                    currentGrid[i].glitchZap = (i == 15)
                }
            }
            "CELESTIAL DOOM" -> {
                audioEngine.bpm = 130
                bpmState = 130
                for (i in 0 until 16) {
                    currentGrid[i].kick = (i == 0 || i == 8)
                    currentGrid[i].snare = (i == 4 || i == 12)
                    currentGrid[i].hihat = (i % 2 == 0)
                    currentGrid[i].china = (i == 0)
                    currentGrid[i].djentChug = (i == 0 || i == 6 || i == 8 || i == 14)
                    currentGrid[i].djentPitchIndex = if (i > 6) 2 else 0
                    currentGrid[i].cyberSynth = (i == 0 || i == 4 || i == 8 || i == 12)
                    currentGrid[i].synthPitchIndex = (i / 4)
                    currentGrid[i].angelPad = (i % 4 == 0)
                    currentGrid[i].subBass = (i == 0 || i == 8)
                    currentGrid[i].glitchZap = (i == 15)
                }
            }
        }
        refreshKey++
    }

    // Heavy Metal AI Mutator
    fun mutateMetalPattern() {
        val grid = audioEngine.stepGrid
        val syncopatedSteps = listOf(0, 3, 6, 8, 10, 11, 14)
        for (i in 0 until 16) {
            // Kick pattern
            grid[i].kick = (i == 0 || i in syncopatedSteps && Random.nextBoolean() || (i % 2 == 0 && Random.nextFloat() > 0.4f))
            grid[i].isKickAccented = (i == 0 || (grid[i].kick && Random.nextFloat() > 0.6f))
            // Snare on 4 and 12 or ghost snare
            grid[i].snare = (i == 4 || i == 12 || (Random.nextFloat() > 0.85f))
            grid[i].isSnareAccented = (i == 4 || i == 12)
            // Hihat / China
            grid[i].hihat = (Random.nextFloat() > 0.25f)
            grid[i].china = (i == 0 || i == 8 || (Random.nextFloat() > 0.85f))
            // Djent Chugs
            grid[i].djentChug = (grid[i].kick || Random.nextFloat() > 0.5f)
            grid[i].djentPitchIndex = if (Random.nextFloat() > 0.65f) Random.nextInt(0, 4) else 0
            grid[i].isDjentAccented = (grid[i].djentChug && Random.nextBoolean())
            // Melodic Cyber Synth
            grid[i].cyberSynth = (Random.nextFloat() > 0.45f)
            grid[i].synthPitchIndex = Random.nextInt(0, 8)
            grid[i].isSynthAccented = (grid[i].cyberSynth && Random.nextFloat() > 0.7f)
            // Pads & FX
            grid[i].angelPad = (i == 0 || (i == 8 && Random.nextBoolean()))
            grid[i].subBass = (i == 0 || (i == 8 && Random.nextBoolean()))
            grid[i].glitchZap = (i == 7 || i == 15 || Random.nextFloat() > 0.88f)
        }
        refreshKey++
    }

    // Tap Tempo Handler
    fun handleTapTempo() {
        val now = System.currentTimeMillis()
        if (lastTapTime > 0L) {
            val interval = now - lastTapTime
            if (interval in 180..1500) {
                tapIntervals.add(interval)
                if (tapIntervals.size > 4) tapIntervals.removeAt(0)
                val avgInterval = tapIntervals.average()
                val calculatedBpm = (60000.0 / avgInterval).toInt().coerceIn(60, 320)
                bpmState = calculatedBpm
                audioEngine.bpm = calculatedBpm
            } else {
                tapIntervals.clear()
            }
        }
        lastTapTime = now
    }

    // Filter tracks based on category tab
    val allChannels = TrackChannelId.values().toList()
    val displayedChannels = remember(activeCategoryFilter, refreshKey) {
        if (activeCategoryFilter == null) allChannels
        else allChannels.filter { it.category == activeCategoryFilter }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // Screen Title & Live HUD Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    GlitchText(
                        text = "MULTI-TRACK SEQUENCER",
                        fontSize = 18,
                        color = ToxicGreen
                    )
                    Text(
                        text = "LAYER SYNTH PATTERNS & HEAVY METAL RIFFS",
                        color = MutedSlate,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        letterSpacing = 1.sp
                    )
                }

                Surface(
                    shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                    color = ToxicGreen.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, ToxicGreen)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isPlaying) ToxicGreen else HellfireRed)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isPlaying) "STEP ${currentStep + 1}/16" else "IDLE",
                            color = ToxicGreen,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // 1. MASTER TRANSPORT & CONTROL CONSOLE
        item {
            CyberMetalCard(borderColor = ToxicGreen, accentColor = ElectricPurple) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Top Row: Play Button & Tap Tempo & BPM Steppers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NeonMetalButton(
                            text = if (isPlaying) "STOP MATRIX" else "START MATRIX",
                            iconText = if (isPlaying) "⏹" else "▶",
                            onClick = onTogglePlay,
                            primaryColor = if (isPlaying) BloodCrimson else ToxicGreen,
                            accentColor = if (isPlaying) HellfireRed else ElectricPurple,
                            modifier = Modifier.weight(1.3f),
                            testTagId = "btn_toggle_matrix"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Tap Tempo Button
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceDark,
                            border = BorderStroke(1.dp, CyberTurquoise),
                            modifier = Modifier
                                .height(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { handleTapTempo() }
                                .testTag("btn_tap_tempo")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "TAP",
                                        color = CyberTurquoise,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "TEMPO",
                                        color = MutedSlate,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 8.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // BPM Stepper Controls
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(SurfaceDark, RoundedCornerShape(10.dp))
                                .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 2.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    if (bpmState > 60) {
                                        bpmState -= 5
                                        audioEngine.bpm = bpmState
                                    }
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("btn_bpm_minus")
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease BPM", tint = CyberTurquoise, modifier = Modifier.size(18.dp))
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$bpmState",
                                    color = NeonWhite,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "BPM",
                                    color = ToxicGreen,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 8.sp
                                )
                            }

                            IconButton(
                                onClick = {
                                    if (bpmState < 320) {
                                        bpmState += 5
                                        audioEngine.bpm = bpmState
                                    }
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("btn_bpm_plus")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase BPM", tint = ToxicGreen, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Secondary Row: Scale Selector, Synth Drawer Toggle & Mutator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Scale Chip
                        Surface(
                            shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                            color = SurfaceDark,
                            border = BorderStroke(1.dp, ElectricPurple),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(38.dp)
                                .clip(CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp))
                                .clickable { showScaleDialog = true }
                                .testTag("btn_select_scale")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "SCALE",
                                        color = MutedSlate,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 8.sp
                                    )
                                    Text(
                                        text = activeScaleState.shortName,
                                        color = ElectricPurple,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                                Icon(Icons.Default.MusicNote, contentDescription = "Scale", tint = ElectricPurple, modifier = Modifier.size(14.dp))
                            }
                        }

                        // Synth Engine Settings Toggle
                        Surface(
                            shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                            color = if (showSynthEngineDrawer) NeonViolet.copy(alpha = 0.2f) else SurfaceDark,
                            border = BorderStroke(1.dp, if (showSynthEngineDrawer) NeonViolet else DarkBorder),
                            modifier = Modifier
                                .weight(1.1f)
                                .height(38.dp)
                                .clip(CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp))
                                .clickable { showSynthEngineDrawer = !showSynthEngineDrawer }
                                .testTag("btn_toggle_synth_dsp")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.Tune, contentDescription = "Synth Engine", tint = NeonViolet, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "SYNTH DSP",
                                    color = NeonViolet,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Heavy Metal Mutator Button
                        Surface(
                            shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                            color = HellfireRed.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, HellfireRed),
                            modifier = Modifier
                                .weight(1.1f)
                                .height(38.dp)
                                .clip(CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp))
                                .clickable { mutateMetalPattern() }
                                .testTag("btn_mutate_metal")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = "Mutate", tint = HellfireRed, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "MUTATE",
                                    color = HellfireRed,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    // Expandable Synth Engine DSP Drawer
                    AnimatedVisibility(
                        visible = showSynthEngineDrawer,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                                .background(SurfaceDark, RoundedCornerShape(8.dp))
                                .border(1.dp, NeonViolet.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "CYBER SYNTH SOUND SHAPING",
                                color = NeonViolet,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            // Waveform selector
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("SAW ACID", "PULSE/SQ", "FM METAL", "CYBER LEAD").forEachIndexed { idx, waveLabel ->
                                    val isSelected = audioEngine.synthWaveformIndex == idx
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (isSelected) NeonViolet else SurfaceContainer,
                                        border = BorderStroke(1.dp, if (isSelected) NeonViolet else DarkBorder),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                audioEngine.synthWaveformIndex = idx
                                                refreshKey++
                                            }
                                    ) {
                                        Text(
                                            text = waveLabel,
                                            color = if (isSelected) VoidBlack else MutedSlate,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 8.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Filter Cutoff & Swing Sliders
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("FILTER CUTOFF", color = MutedSlate, fontFamily = FontFamily.Monospace, fontSize = 9.sp)
                                        Text("${(audioEngine.synthFilterCutoff * 100).toInt()}%", color = CyberTurquoise, fontFamily = FontFamily.Monospace, fontSize = 9.sp)
                                    }
                                    Slider(
                                        value = audioEngine.synthFilterCutoff,
                                        onValueChange = {
                                            audioEngine.synthFilterCutoff = it
                                            refreshKey++
                                        },
                                        valueRange = 0.1f..1.0f,
                                        colors = SliderDefaults.colors(
                                            thumbColor = CyberTurquoise,
                                            activeTrackColor = CyberTurquoise,
                                            inactiveTrackColor = SurfaceContainer
                                        ),
                                        modifier = Modifier.height(28.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("SWING GROOVE", color = MutedSlate, fontFamily = FontFamily.Monospace, fontSize = 9.sp)
                                        Text("${(swingState * 100).toInt()}%", color = AcidGreen, fontFamily = FontFamily.Monospace, fontSize = 9.sp)
                                    }
                                    Slider(
                                        value = swingState,
                                        onValueChange = {
                                            swingState = it
                                            audioEngine.swingAmount = it
                                        },
                                        valueRange = 0.0f..0.5f,
                                        colors = SliderDefaults.colors(
                                            thumbColor = AcidGreen,
                                            activeTrackColor = AcidGreen,
                                            inactiveTrackColor = SurfaceContainer
                                        ),
                                        modifier = Modifier.height(28.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. PATTERN BANK SWITCHER & PRESETS
        item {
            CyberMetalCard(borderColor = CyberTurquoise, accentColor = ElectricPurple) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PATTERN BANKS",
                            color = CyberTurquoise,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            // Copy Pattern Button
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = SurfaceDark,
                                border = BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable {
                                        val nextPattern = (selectedPattern + 1) % MetalAudioEngine.PATTERN_COUNT
                                        audioEngine.copyPattern(selectedPattern, nextPattern)
                                        refreshKey++
                                    }
                                    .testTag("btn_copy_pattern")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CyberTurquoise, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("COPY NEXT", color = CyberTurquoise, fontFamily = FontFamily.Monospace, fontSize = 8.sp)
                                }
                            }

                            // Clear Pattern Button
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = SurfaceDark,
                                border = BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable {
                                        audioEngine.clearPattern(selectedPattern)
                                        refreshKey++
                                    }
                                    .testTag("btn_clear_pattern")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = BloodCrimson, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("CLEAR", color = BloodCrimson, fontFamily = FontFamily.Monospace, fontSize = 8.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Pattern A, B, C, D Tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("PATTERN A", "PATTERN B", "PATTERN C", "PATTERN D").forEachIndexed { idx, pLabel ->
                            val isSelected = selectedPattern == idx
                            val color = when (idx) {
                                0 -> ToxicGreen
                                1 -> HellfireRed
                                2 -> CyberTurquoise
                                else -> ElectricPurple
                            }

                            Surface(
                                shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                                color = if (isSelected) color.copy(alpha = 0.2f) else SurfaceDark,
                                border = BorderStroke(1.dp, if (isSelected) color else DarkBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedPattern = idx
                                        audioEngine.currentPatternIndex = idx
                                        refreshKey++
                                    }
                                    .testTag("btn_pattern_${pLabel.last().lowercase()}")
                            ) {
                                Text(
                                    text = pLabel,
                                    color = if (isSelected) color else MutedSlate,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    fontSize = 10.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Heavy Metal Genre Beat Presets
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "BLASTBEAT 280",
                            "DROP-Z BREAKDOWN",
                            "DJENT 7/8",
                            "INDUSTRIAL CYBER",
                            "CELESTIAL DOOM"
                        ).forEach { pName ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = SurfaceContainer,
                                border = BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier
                                    .clickable { loadPreset(pName) }
                                    .testTag("btn_preset_${pName.lowercase().replace(" ", "_")}")
                            ) {
                                Text(
                                    text = pName,
                                    color = CyberTurquoise,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. TRACK CATEGORY FILTER CHIPS
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = activeCategoryFilter == null,
                    onClick = { activeCategoryFilter = null },
                    label = { Text("ALL TRACKS (${allChannels.size})", fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricPurple.copy(alpha = 0.25f),
                        selectedLabelColor = NeonWhite,
                        containerColor = SurfaceDark,
                        labelColor = MutedSlate
                    ),
                    border = BorderStroke(1.dp, if (activeCategoryFilter == null) ElectricPurple else DarkBorder)
                )

                TrackCategory.values().forEach { cat ->
                    val count = allChannels.count { it.category == cat }
                    val isSelected = activeCategoryFilter == cat
                    val chipColor = when (cat) {
                        TrackCategory.RHYTHM -> ToxicGreen
                        TrackCategory.SYNTH_MELODIC -> HellfireRed
                        TrackCategory.HARMONIC -> CelestialCyan
                        TrackCategory.FX -> GlitchMagenta
                    }

                    FilterChip(
                        selected = isSelected,
                        onClick = { activeCategoryFilter = if (isSelected) null else cat },
                        label = { Text("${cat.label} ($count)", fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = chipColor.copy(alpha = 0.25f),
                            selectedLabelColor = NeonWhite,
                            containerColor = SurfaceDark,
                            labelColor = MutedSlate
                        ),
                        border = BorderStroke(1.dp, if (isSelected) chipColor else DarkBorder)
                    )
                }
            }
        }

        // 4. MULTI-TRACK MATRIX GRID
        item {
            CyberMetalCard(borderColor = ElectricPurple, accentColor = CyberTurquoise) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    val scrollState = rememberScrollState()

                    Box(modifier = Modifier.horizontalScroll(scrollState)) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Step Numbers Header (1 to 16 with Measure separation)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Spacer(modifier = Modifier.width(136.dp))
                                for (step in 0 until 16) {
                                    val isCurrent = isPlaying && (currentStep == step)
                                    val isMeasureStart = (step % 4 == 0)

                                    Box(
                                        modifier = Modifier
                                            .size(width = 30.dp, height = 22.dp)
                                            .padding(1.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(
                                                if (isCurrent) ToxicGreen
                                                else if (isMeasureStart) SurfaceHighlight
                                                else SurfaceContainer
                                            )
                                            .border(
                                                1.dp,
                                                if (isCurrent) NeonWhite else if (isMeasureStart) DarkBorder else Color.Transparent,
                                                RoundedCornerShape(2.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${step + 1}",
                                            color = if (isCurrent) VoidBlack else if (isMeasureStart) NeonWhite else MutedSlate,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = if (isCurrent || isMeasureStart) FontWeight.Black else FontWeight.Normal,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }

                            // Tracks Rows
                            displayedChannels.forEach { channelId ->
                                val trackColor = Color(channelId.colorHex)
                                val isMuted = audioEngine.trackMutes[channelId] == true
                                val isSolo = audioEngine.trackSolos[channelId] == true

                                MultiTrackSequencerRow(
                                    channelId = channelId,
                                    trackColor = trackColor,
                                    audioEngine = audioEngine,
                                    currentStep = if (isPlaying) currentStep else -1,
                                    isMuted = isMuted,
                                    isSolo = isSolo,
                                    onToggleMute = {
                                        audioEngine.trackMutes[channelId] = !isMuted
                                        refreshKey++
                                    },
                                    onToggleSolo = {
                                        audioEngine.trackSolos[channelId] = !isSolo
                                        refreshKey++
                                    },
                                    onAudition = {
                                        audioEngine.triggerChannelSound(channelId, 0)
                                    },
                                    onStepClicked = { stepIdx ->
                                        toggleOrCycleStep(audioEngine, channelId, stepIdx)
                                        refreshKey++
                                    },
                                    onStepLongClicked = { stepIdx ->
                                        // Open note pitch / velocity picker
                                        if (channelId == TrackChannelId.CYBER_SYNTH || channelId == TrackChannelId.DJENT_CHUG) {
                                            pitchPickerTrack = channelId
                                            pitchPickerStep = stepIdx
                                        } else {
                                            // Toggle accent for rhythm
                                            toggleAccent(audioEngine, channelId, stepIdx)
                                            refreshKey++
                                        }
                                    },
                                    onOpenTrackMenu = {
                                        showTrackActionsFor = channelId
                                    },
                                    refreshTrigger = refreshKey
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. INLINE NOTE PITCH & SCALE DEGREE PICKER (When a melodic synth/djent step is selected)
        if (pitchPickerTrack != null && pitchPickerStep in 0..15) {
            item {
                val track = pitchPickerTrack!!
                val stepIdx = pitchPickerStep
                val trackColor = Color(track.colorHex)
                val grid = audioEngine.stepGrid
                val step = grid[stepIdx]

                val currentPitchIdx = if (track == TrackChannelId.CYBER_SYNTH) step.synthPitchIndex else step.djentPitchIndex
                val isAccented = if (track == TrackChannelId.CYBER_SYNTH) step.isSynthAccented else step.isDjentAccented

                CyberMetalCard(borderColor = trackColor, accentColor = ElectricPurple) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${track.iconEmoji} ${track.title} // STEP ${stepIdx + 1}",
                                    color = trackColor,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${activeScaleState.displayName})",
                                    color = MutedSlate,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp
                                )
                            }

                            IconButton(
                                onClick = { pitchPickerTrack = null },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Clear, contentDescription = "Close", tint = MutedSlate, modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Scale Degree Note Selector Keys
                        Text(
                            text = "SELECT MELODIC SCALE NOTE:",
                            color = MutedSlate,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            activeScaleState.noteNames.forEachIndexed { nIdx, noteName ->
                                val isSelected = (currentPitchIdx == nIdx)

                                Surface(
                                    shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                                    color = if (isSelected) trackColor else SurfaceDark,
                                    border = BorderStroke(1.dp, if (isSelected) NeonWhite else DarkBorder),
                                    modifier = Modifier
                                        .width(42.dp)
                                        .height(44.dp)
                                        .clip(CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp))
                                        .clickable {
                                            if (track == TrackChannelId.CYBER_SYNTH) {
                                                step.cyberSynth = true
                                                step.synthPitchIndex = nIdx
                                            } else {
                                                step.djentChug = true
                                                step.djentPitchIndex = nIdx
                                            }
                                            audioEngine.triggerChannelSound(track, nIdx)
                                            refreshKey++
                                        }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = noteName,
                                                color = if (isSelected) VoidBlack else NeonWhite,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "DEG ${nIdx + 1}",
                                                color = if (isSelected) VoidBlack.copy(alpha = 0.7f) else MutedSlate,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 7.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Velocity & Accent Toggle for Step
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isAccented) HellfireRed.copy(alpha = 0.2f) else SurfaceDark,
                                border = BorderStroke(1.dp, if (isAccented) HellfireRed else DarkBorder),
                                modifier = Modifier
                                    .clickable {
                                        if (track == TrackChannelId.CYBER_SYNTH) {
                                            step.isSynthAccented = !step.isSynthAccented
                                        } else {
                                            step.isDjentAccented = !step.isDjentAccented
                                        }
                                        refreshKey++
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isAccented) "💥 ACCENTED (HIGH GAIN)" else "NORMAL GAIN",
                                        color = if (isAccented) HellfireRed else MutedSlate,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            // Clear this step
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SurfaceDark,
                                border = BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier.clickable {
                                    if (track == TrackChannelId.CYBER_SYNTH) step.cyberSynth = false
                                    else step.djentChug = false
                                    pitchPickerTrack = null
                                    refreshKey++
                                }
                            ) {
                                Text(
                                    text = "TURN OFF STEP",
                                    color = BloodCrimson,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    // Scale Picker Dialog
    if (showScaleDialog) {
        AlertDialog(
            onDismissRequest = { showScaleDialog = false },
            containerColor = SurfaceDark,
            title = {
                Text(
                    text = "HEAVY METAL SCALES & MODES",
                    color = ElectricPurple,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Harmonize your synth arpeggios and djent riffs across extreme metal modes:",
                        color = MutedSlate,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )

                    MetalScale.values().forEach { scale ->
                        val isSelected = activeScaleState == scale
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) ElectricPurple.copy(alpha = 0.2f) else SurfaceContainer,
                            border = BorderStroke(1.dp, if (isSelected) ElectricPurple else DarkBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    activeScaleState = scale
                                    audioEngine.activeScale = scale
                                    showScaleDialog = false
                                    refreshKey++
                                }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = scale.displayName,
                                    color = if (isSelected) NeonWhite else CyberTurquoise,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = scale.noteNames.joinToString(" - "),
                                    color = if (isSelected) AcidGreen else MutedSlate,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showScaleDialog = false }) {
                    Text("CLOSE", color = ToxicGreen, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Track Quick Actions Dialog (Fill 4th, Fill 2nd, Clear, Invert)
    showTrackActionsFor?.let { channel ->
        val trackColor = Color(channel.colorHex)
        AlertDialog(
            onDismissRequest = { showTrackActionsFor = null },
            containerColor = SurfaceDark,
            title = {
                Text(
                    text = "${channel.iconEmoji} ${channel.title} ACTIONS",
                    color = trackColor,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "FILL 4-ON-FLOOR (1, 5, 9, 13)" to { fillChannelSteps(audioEngine, channel, 4) },
                        "FILL 8TH NOTES (1, 3, 5, 7, ...)" to { fillChannelSteps(audioEngine, channel, 2) },
                        "FILL ALL 16 STEPS (ROLL)" to { fillChannelSteps(audioEngine, channel, 1) },
                        "RANDOMIZE TRACK STEPS" to { randomizeChannelSteps(audioEngine, channel) },
                        "CLEAR THIS TRACK" to { clearChannelSteps(audioEngine, channel) }
                    ).forEach { (label, action) ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SurfaceContainer,
                            border = BorderStroke(1.dp, DarkBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    action()
                                    showTrackActionsFor = null
                                    refreshKey++
                                }
                        ) {
                            Text(
                                text = label,
                                color = if (label.startsWith("CLEAR")) BloodCrimson else CyberTurquoise,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTrackActionsFor = null }) {
                    Text("CANCEL", color = MutedSlate, fontFamily = FontFamily.Monospace)
                }
            }
        )
    }
}

// ------------------------------------------------------------------------------------------------
// Multi-Track Sequencer Row Component
// ------------------------------------------------------------------------------------------------

@Composable
fun MultiTrackSequencerRow(
    channelId: TrackChannelId,
    trackColor: Color,
    audioEngine: MetalAudioEngine,
    currentStep: Int,
    isMuted: Boolean,
    isSolo: Boolean,
    onToggleMute: () -> Unit,
    onToggleSolo: () -> Unit,
    onAudition: () -> Unit,
    onStepClicked: (Int) -> Unit,
    onStepLongClicked: (Int) -> Unit,
    onOpenTrackMenu: () -> Unit,
    refreshTrigger: Int
) {
    val grid = audioEngine.stepGrid
    val activeScale = audioEngine.activeScale

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Track Header (Name + Audition + Mute + Solo + Actions)
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = SurfaceDark,
            border = BorderStroke(1.dp, if (isSolo) CelestialCyan else if (isMuted) BloodCrimson.copy(alpha = 0.5f) else DarkBorder),
            modifier = Modifier
                .width(132.dp)
                .height(38.dp)
                .clip(RoundedCornerShape(4.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Title & Audition Tap
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onAudition() }
                ) {
                    Text(
                        text = channelId.iconEmoji,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(end = 3.dp)
                    )
                    Text(
                        text = channelId.shortName,
                        color = if (isMuted) MutedSlate else trackColor,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                }

                // Mute / Solo Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    // Mute Button
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (isMuted) BloodCrimson else SurfaceContainer)
                            .border(1.dp, if (isMuted) BloodCrimson else DarkBorder, RoundedCornerShape(3.dp))
                            .clickable { onToggleMute() }
                            .testTag("track_mute_${channelId.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "M",
                            color = if (isMuted) VoidBlack else MutedSlate,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 8.sp
                        )
                    }

                    // Solo Button
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (isSolo) CelestialCyan else SurfaceContainer)
                            .border(1.dp, if (isSolo) CelestialCyan else DarkBorder, RoundedCornerShape(3.dp))
                            .clickable { onToggleSolo() }
                            .testTag("track_solo_${channelId.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "S",
                            color = if (isSolo) VoidBlack else MutedSlate,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 8.sp
                        )
                    }

                    // Menu dots
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(SurfaceContainer)
                            .clickable { onOpenTrackMenu() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⋮",
                            color = MutedSlate,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        // 16 Step Grid Tiles
        for (stepIdx in 0 until 16) {
            val step = grid[stepIdx]
            val isActive = isStepActiveForChannel(step, channelId)
            val isAccented = isStepAccentedForChannel(step, channelId)
            val isCurrent = (currentStep == stepIdx)
            val isBeatBar = (stepIdx % 4 == 0)

            // Note pitch label if melodic
            val noteLabel = when (channelId) {
                TrackChannelId.CYBER_SYNTH -> if (isActive) activeScale.noteNames.getOrElse(step.synthPitchIndex % activeScale.noteNames.size) { "N" } else ""
                TrackChannelId.DJENT_CHUG -> if (isActive) activeScale.noteNames.getOrElse(step.djentPitchIndex % activeScale.noteNames.size) { "R" } else ""
                else -> if (isAccented) "⚡" else ""
            }

            Box(
                modifier = Modifier
                    .size(width = 30.dp, height = 38.dp)
                    .padding(1.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        when {
                            isActive && isAccented -> trackColor
                            isActive -> trackColor.copy(alpha = 0.75f)
                            isCurrent -> SurfaceHighlight
                            isBeatBar -> SurfaceHighlight.copy(alpha = 0.5f)
                            else -> SurfaceDark
                        }
                    )
                    .border(
                        width = if (isCurrent) 2.dp else if (isAccented) 1.5.dp else 1.dp,
                        color = when {
                            isCurrent -> NeonWhite
                            isActive && isAccented -> NeonWhite
                            isActive -> trackColor
                            isBeatBar -> DarkBorder
                            else -> BorderSubtleColor
                        },
                        shape = RoundedCornerShape(3.dp)
                    )
                    .clickable { onStepClicked(stepIdx) },
                contentAlignment = Alignment.Center
            ) {
                if (isActive) {
                    Text(
                        text = noteLabel,
                        color = if (isAccented) VoidBlack else NeonWhite,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = if (noteLabel.length > 1) 8.sp else 9.sp,
                        textAlign = TextAlign.Center
                    )
                } else if (isBeatBar) {
                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .clip(CircleShape)
                            .background(MutedSlate.copy(alpha = 0.4f))
                    )
                }
            }
        }
    }
}

val BorderSubtleColor = Color(0xFF1E1E1E)

// ------------------------------------------------------------------------------------------------
// Step Helper Functions
// ------------------------------------------------------------------------------------------------

fun isStepActiveForChannel(step: com.example.data.BeatStep, channel: TrackChannelId): Boolean {
    return when (channel) {
        TrackChannelId.BLAST_KICK -> step.kick
        TrackChannelId.SNARE_CRACK -> step.snare
        TrackChannelId.HI_HAT -> step.hihat
        TrackChannelId.CHINA_CYMBAL -> step.china
        TrackChannelId.DJENT_CHUG -> step.djentChug
        TrackChannelId.CYBER_SYNTH -> step.cyberSynth
        TrackChannelId.ANGEL_PAD -> step.angelPad
        TrackChannelId.SUB_BASS -> step.subBass
        TrackChannelId.GLITCH_FX -> step.glitchZap
    }
}

fun isStepAccentedForChannel(step: com.example.data.BeatStep, channel: TrackChannelId): Boolean {
    return when (channel) {
        TrackChannelId.BLAST_KICK -> step.isKickAccented
        TrackChannelId.SNARE_CRACK -> step.isSnareAccented
        TrackChannelId.DJENT_CHUG -> step.isDjentAccented
        TrackChannelId.CYBER_SYNTH -> step.isSynthAccented
        else -> false
    }
}

fun toggleOrCycleStep(audioEngine: MetalAudioEngine, channel: TrackChannelId, stepIdx: Int) {
    val step = audioEngine.stepGrid[stepIdx]
    when (channel) {
        TrackChannelId.BLAST_KICK -> {
            step.kick = !step.kick
            if (step.kick) audioEngine.triggerChannelSound(channel)
        }
        TrackChannelId.SNARE_CRACK -> {
            step.snare = !step.snare
            if (step.snare) audioEngine.triggerChannelSound(channel)
        }
        TrackChannelId.HI_HAT -> {
            step.hihat = !step.hihat
            if (step.hihat) audioEngine.triggerChannelSound(channel)
        }
        TrackChannelId.CHINA_CYMBAL -> {
            step.china = !step.china
            if (step.china) audioEngine.triggerChannelSound(channel)
        }
        TrackChannelId.DJENT_CHUG -> {
            step.djentChug = !step.djentChug
            if (step.djentChug) audioEngine.triggerChannelSound(channel, step.djentPitchIndex)
        }
        TrackChannelId.CYBER_SYNTH -> {
            step.cyberSynth = !step.cyberSynth
            if (step.cyberSynth) audioEngine.triggerChannelSound(channel, step.synthPitchIndex)
        }
        TrackChannelId.ANGEL_PAD -> {
            step.angelPad = !step.angelPad
            if (step.angelPad) audioEngine.triggerChannelSound(channel)
        }
        TrackChannelId.SUB_BASS -> {
            step.subBass = !step.subBass
            if (step.subBass) audioEngine.triggerChannelSound(channel)
        }
        TrackChannelId.GLITCH_FX -> {
            step.glitchZap = !step.glitchZap
            if (step.glitchZap) audioEngine.triggerChannelSound(channel)
        }
    }
}

fun toggleAccent(audioEngine: MetalAudioEngine, channel: TrackChannelId, stepIdx: Int) {
    val step = audioEngine.stepGrid[stepIdx]
    when (channel) {
        TrackChannelId.BLAST_KICK -> step.isKickAccented = !step.isKickAccented
        TrackChannelId.SNARE_CRACK -> step.isSnareAccented = !step.isSnareAccented
        TrackChannelId.DJENT_CHUG -> step.isDjentAccented = !step.isDjentAccented
        TrackChannelId.CYBER_SYNTH -> step.isSynthAccented = !step.isSynthAccented
        else -> {}
    }
}

fun fillChannelSteps(audioEngine: MetalAudioEngine, channel: TrackChannelId, interval: Int) {
    val grid = audioEngine.stepGrid
    for (i in 0 until 16) {
        val active = (i % interval == 0)
        when (channel) {
            TrackChannelId.BLAST_KICK -> grid[i].kick = active
            TrackChannelId.SNARE_CRACK -> grid[i].snare = active
            TrackChannelId.HI_HAT -> grid[i].hihat = active
            TrackChannelId.CHINA_CYMBAL -> grid[i].china = active
            TrackChannelId.DJENT_CHUG -> grid[i].djentChug = active
            TrackChannelId.CYBER_SYNTH -> grid[i].cyberSynth = active
            TrackChannelId.ANGEL_PAD -> grid[i].angelPad = active
            TrackChannelId.SUB_BASS -> grid[i].subBass = active
            TrackChannelId.GLITCH_FX -> grid[i].glitchZap = active
        }
    }
}

fun clearChannelSteps(audioEngine: MetalAudioEngine, channel: TrackChannelId) {
    val grid = audioEngine.stepGrid
    for (i in 0 until 16) {
        when (channel) {
            TrackChannelId.BLAST_KICK -> grid[i].kick = false
            TrackChannelId.SNARE_CRACK -> grid[i].snare = false
            TrackChannelId.HI_HAT -> grid[i].hihat = false
            TrackChannelId.CHINA_CYMBAL -> grid[i].china = false
            TrackChannelId.DJENT_CHUG -> grid[i].djentChug = false
            TrackChannelId.CYBER_SYNTH -> grid[i].cyberSynth = false
            TrackChannelId.ANGEL_PAD -> grid[i].angelPad = false
            TrackChannelId.SUB_BASS -> grid[i].subBass = false
            TrackChannelId.GLITCH_FX -> grid[i].glitchZap = false
        }
    }
}

fun randomizeChannelSteps(audioEngine: MetalAudioEngine, channel: TrackChannelId) {
    val grid = audioEngine.stepGrid
    for (i in 0 until 16) {
        val active = Random.nextFloat() > 0.6f
        when (channel) {
            TrackChannelId.BLAST_KICK -> grid[i].kick = active
            TrackChannelId.SNARE_CRACK -> grid[i].snare = active
            TrackChannelId.HI_HAT -> grid[i].hihat = active
            TrackChannelId.CHINA_CYMBAL -> grid[i].china = active
            TrackChannelId.DJENT_CHUG -> {
                grid[i].djentChug = active
                if (active) grid[i].djentPitchIndex = Random.nextInt(0, 4)
            }
            TrackChannelId.CYBER_SYNTH -> {
                grid[i].cyberSynth = active
                if (active) grid[i].synthPitchIndex = Random.nextInt(0, 8)
            }
            TrackChannelId.ANGEL_PAD -> grid[i].angelPad = active
            TrackChannelId.SUB_BASS -> grid[i].subBass = active
            TrackChannelId.GLITCH_FX -> grid[i].glitchZap = active
        }
    }
}
