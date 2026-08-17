package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.components.CyberMetalCard
import com.example.ui.components.GlitchText
import com.example.ui.components.NeonMetalButton
import com.example.ui.theme.AcidGreen
import com.example.ui.theme.AngelAqua
import com.example.ui.theme.AngelicGold
import com.example.ui.theme.BloodCrimson
import com.example.ui.theme.CyberTurquoise
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DeepAbyss
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.HellfireRed
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.NeonWhite
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceHighlight
import com.example.ui.theme.ToxicGreen
import com.example.ui.theme.VoidBlack

@Composable
fun StepSequencerScreen(
    audioEngine: MetalAudioEngine,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    var bpmState by remember { mutableIntStateOf(audioEngine.bpm) }
    var refreshKey by remember { mutableIntStateOf(0) }

    fun loadPreset(presetName: String) {
        when (presetName) {
            "BLASTBEAT 280" -> {
                audioEngine.bpm = 280
                bpmState = 280
                for (i in 0 until 16) {
                    audioEngine.stepGrid[i].kick = (i % 2 == 0)
                    audioEngine.stepGrid[i].snare = (i % 4 == 2)
                    audioEngine.stepGrid[i].hihat = true
                    audioEngine.stepGrid[i].djentChug = (i % 4 == 0)
                    audioEngine.stepGrid[i].angelPad = (i == 0 || i == 8)
                    audioEngine.stepGrid[i].glitchZap = (i == 15)
                }
            }
            "DROP-Z BREAKDOWN" -> {
                audioEngine.bpm = 160
                bpmState = 160
                for (i in 0 until 16) {
                    audioEngine.stepGrid[i].kick = (i == 0 || i == 3 || i == 6 || i == 10 || i == 12)
                    audioEngine.stepGrid[i].snare = (i == 8)
                    audioEngine.stepGrid[i].hihat = (i % 2 == 0)
                    audioEngine.stepGrid[i].djentChug = (i == 0 || i == 3 || i == 6 || i == 10 || i == 12)
                    audioEngine.stepGrid[i].angelPad = false
                    audioEngine.stepGrid[i].glitchZap = (i == 7 || i == 15)
                }
            }
            "DJENT 7/8" -> {
                audioEngine.bpm = 210
                bpmState = 210
                for (i in 0 until 16) {
                    audioEngine.stepGrid[i].kick = (i == 0 || i == 3 || i == 5 || i == 8 || i == 11 || i == 14)
                    audioEngine.stepGrid[i].snare = (i == 4 || i == 12)
                    audioEngine.stepGrid[i].hihat = (i % 2 != 0)
                    audioEngine.stepGrid[i].djentChug = (i == 0 || i == 3 || i == 5 || i == 8 || i == 11 || i == 14)
                    audioEngine.stepGrid[i].angelPad = (i == 0)
                    audioEngine.stepGrid[i].glitchZap = (i == 7)
                }
            }
        }
        refreshKey++
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlitchText(
                    text = "16-STEP BEAT & CHUG MATRIX",
                    fontSize = 18,
                    color = ToxicGreen
                )

                Surface(
                    shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                    color = ToxicGreen.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ToxicGreen)
                ) {
                    Text(
                        text = "$bpmState BPM",
                        color = ToxicGreen,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Tempo & Master Play Controls
        item {
            CyberMetalCard(borderColor = ToxicGreen, accentColor = ElectricPurple) {
                Column(modifier = Modifier.fillMaxWidth()) {
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
                            modifier = Modifier.weight(1f),
                            testTagId = "btn_toggle_matrix"
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // BPM Steppers
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    if (bpmState > 100) {
                                        bpmState -= 5
                                        audioEngine.bpm = bpmState
                                    }
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease BPM", tint = CyberTurquoise)
                            }

                            Text(
                                text = "$bpmState",
                                color = NeonWhite,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )

                            IconButton(
                                onClick = {
                                    if (bpmState < 320) {
                                        bpmState += 5
                                        audioEngine.bpm = bpmState
                                    }
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase BPM", tint = ToxicGreen)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preset Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("BLASTBEAT 280", "DROP-Z BREAKDOWN", "DJENT 7/8").forEach { pName ->
                            Surface(
                                shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                                color = SurfaceContainer,
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { loadPreset(pName) }
                            ) {
                                Text(
                                    text = pName,
                                    color = CyberTurquoise,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // The 16-Step Sequencer Grid
        item {
            CyberMetalCard(borderColor = ElectricPurple, accentColor = CyberTurquoise) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "POLYPHONIC METAL CHANNELS",
                        color = CyberTurquoise,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val scrollState = rememberScrollState()
                    Box(modifier = Modifier.horizontalScroll(scrollState)) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Step Header numbers (1..16)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Spacer(modifier = Modifier.width(90.dp))
                                for (step in 0 until 16) {
                                    val isCurrent = isPlaying && (currentStep == step)
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .padding(2.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(if (isCurrent) ToxicGreen else SurfaceContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${step + 1}",
                                            color = if (isCurrent) VoidBlack else MutedSlate,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }

                            // 1. DJENT CHUG
                            SequencerTrackRow(
                                title = "8S DJENT",
                                color = HellfireRed,
                                steps = 16,
                                isStepActive = { audioEngine.stepGrid[it].djentChug },
                                onToggleStep = {
                                    audioEngine.stepGrid[it].djentChug = !audioEngine.stepGrid[it].djentChug
                                    audioEngine.triggerDjentChug(true)
                                    refreshKey++
                                },
                                currentStep = if (isPlaying) currentStep else -1
                            )

                            // 2. BLAST KICK
                            SequencerTrackRow(
                                title = "BLAST KICK",
                                color = ToxicGreen,
                                steps = 16,
                                isStepActive = { audioEngine.stepGrid[it].kick },
                                onToggleStep = {
                                    audioEngine.stepGrid[it].kick = !audioEngine.stepGrid[it].kick
                                    refreshKey++
                                },
                                currentStep = if (isPlaying) currentStep else -1
                            )

                            // 3. SNARE
                            SequencerTrackRow(
                                title = "SNARE CRACK",
                                color = AngelicGold,
                                steps = 16,
                                isStepActive = { audioEngine.stepGrid[it].snare },
                                onToggleStep = {
                                    audioEngine.stepGrid[it].snare = !audioEngine.stepGrid[it].snare
                                    refreshKey++
                                },
                                currentStep = if (isPlaying) currentStep else -1
                            )

                            // 4. HI-HAT
                            SequencerTrackRow(
                                title = "HI-HAT",
                                color = CyberTurquoise,
                                steps = 16,
                                isStepActive = { audioEngine.stepGrid[it].hihat },
                                onToggleStep = {
                                    audioEngine.stepGrid[it].hihat = !audioEngine.stepGrid[it].hihat
                                    refreshKey++
                                },
                                currentStep = if (isPlaying) currentStep else -1
                            )

                            // 5. ANGEL PAD
                            SequencerTrackRow(
                                title = "ANGEL PAD",
                                color = AngelAqua,
                                steps = 16,
                                isStepActive = { audioEngine.stepGrid[it].angelPad },
                                onToggleStep = {
                                    audioEngine.stepGrid[it].angelPad = !audioEngine.stepGrid[it].angelPad
                                    audioEngine.triggerAngelicChoir()
                                    refreshKey++
                                },
                                currentStep = if (isPlaying) currentStep else -1
                            )

                            // 6. GLITCH ZAP
                            SequencerTrackRow(
                                title = "GLITCH ZAP",
                                color = NeonViolet,
                                steps = 16,
                                isStepActive = { audioEngine.stepGrid[it].glitchZap },
                                onToggleStep = {
                                    audioEngine.stepGrid[it].glitchZap = !audioEngine.stepGrid[it].glitchZap
                                    audioEngine.triggerGlitchLaser()
                                    refreshKey++
                                },
                                currentStep = if (isPlaying) currentStep else -1
                            )
                        }
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
fun SequencerTrackRow(
    title: String,
    color: Color,
    steps: Int = 16,
    isStepActive: (Int) -> Boolean,
    onToggleStep: (Int) -> Unit,
    currentStep: Int
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = title,
            color = color,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            modifier = Modifier.width(90.dp)
        )

        for (s in 0 until steps) {
            val active = isStepActive(s)
            val isCurrent = (currentStep == s)

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .padding(2.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        if (active) color else if (isCurrent) SurfaceHighlight else SurfaceDark
                    )
                    .border(
                        1.dp,
                        if (isCurrent) NeonWhite else if (active) color else DarkBorder,
                        RoundedCornerShape(3.dp)
                    )
                    .clickable { onToggleStep(s) }
            )
        }
    }
}
