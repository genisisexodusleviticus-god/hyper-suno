package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.MetalAudioEngine
import com.example.data.DspSettings
import com.example.ui.components.CyberMetalCard
import com.example.ui.components.GlitchText
import com.example.ui.components.NeonMetalButton
import com.example.ui.theme.AcidGreen
import com.example.ui.theme.AngelAqua
import com.example.ui.theme.AngelicGold
import com.example.ui.theme.BloodCrimson
import com.example.ui.theme.CyberTurquoise
import com.example.ui.theme.DeepAbyss
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.GlitchMagenta
import com.example.ui.theme.HellfireRed
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.NeonWhite
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.ToxicGreen
import com.example.ui.theme.VoidBlack

@Composable
fun VocalDspScreen(
    audioEngine: MetalAudioEngine,
    modifier: Modifier = Modifier
) {
    var distortionGain by remember { mutableFloatStateOf(0.75f) }
    var bitcrushDepth by remember { mutableFloatStateOf(0.40f) }
    var pitchShiftSemitones by remember { mutableIntStateOf(-7) }
    var reverbWet by remember { mutableFloatStateOf(0.65f) }
    var glitchStutterRate by remember { mutableFloatStateOf(0.35f) }
    var masterVol by remember { mutableFloatStateOf(0.90f) }

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
                    text = "VOCAL DEMONIZER & AUDIO DSP",
                    fontSize = 18,
                    color = NeonViolet
                )

                Surface(
                    shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                    color = NeonViolet.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet)
                ) {
                    Text(
                        text = "RACK FX v5.5",
                        color = NeonViolet,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // DSP Module 1: Demonic Pitch Shifter & Formant Shifter
        item {
            CyberMetalCard(borderColor = HellfireRed, accentColor = ElectricPurple) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "👹 DEMONIC SUB-GUTTURAL PITCH SHIFTER",
                            color = HellfireRed,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = if (pitchShiftSemitones < 0) "$pitchShiftSemitones SEMITONES" else "+$pitchShiftSemitones SEMITONES",
                            color = NeonWhite,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                    Slider(
                        value = pitchShiftSemitones.toFloat(),
                        onValueChange = { pitchShiftSemitones = it.toInt() },
                        valueRange = -12f..12f,
                        steps = 24,
                        colors = SliderDefaults.colors(
                            thumbColor = HellfireRed,
                            activeTrackColor = HellfireRed,
                            inactiveTrackColor = SurfaceContainer
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Distortion Drive
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔥 TUBE OVERDRIVE & WAVESHAPER GAIN",
                            color = BloodCrimson,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "${(distortionGain * 100).toInt()}%",
                            color = NeonWhite,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                    Slider(
                        value = distortionGain,
                        onValueChange = {
                            distortionGain = it
                            audioEngine.masterDistortion = it
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = BloodCrimson,
                            activeTrackColor = BloodCrimson,
                            inactiveTrackColor = SurfaceContainer
                        )
                    )
                }
            }
        }

        // DSP Module 2: Angelic Reverb & Cyber Bitcrush
        item {
            CyberMetalCard(borderColor = CyberTurquoise, accentColor = AcidGreen) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🪽 CELESTIAL SERAPH REVERB (WET)",
                            color = CyberTurquoise,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${(reverbWet * 100).toInt()}%",
                            color = NeonWhite,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                    Slider(
                        value = reverbWet,
                        onValueChange = { reverbWet = it },
                        colors = SliderDefaults.colors(
                            thumbColor = CyberTurquoise,
                            activeTrackColor = CyberTurquoise,
                            inactiveTrackColor = SurfaceContainer
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Bitcrush Depth
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "👾 CYBER BITCRUSHER (SAMPLE DESTRUCTION)",
                            color = ToxicGreen,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "${(bitcrushDepth * 100).toInt()}%",
                            color = NeonWhite,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                    Slider(
                        value = bitcrushDepth,
                        onValueChange = { bitcrushDepth = it },
                        colors = SliderDefaults.colors(
                            thumbColor = ToxicGreen,
                            activeTrackColor = ToxicGreen,
                            inactiveTrackColor = SurfaceContainer
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Chaos Glitch Stutter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚡ CHAOS STUTTER & BUFFER REPEAT",
                            color = GlitchMagenta,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "${(glitchStutterRate * 100).toInt()}%",
                            color = NeonWhite,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                    Slider(
                        value = glitchStutterRate,
                        onValueChange = { glitchStutterRate = it },
                        colors = SliderDefaults.colors(
                            thumbColor = GlitchMagenta,
                            activeTrackColor = GlitchMagenta,
                            inactiveTrackColor = SurfaceContainer
                        )
                    )
                }
            }
        }

        // Audition Live DSP Buttons
        item {
            CyberMetalCard(borderColor = ElectricPurple, accentColor = ToxicGreen) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "DSP REAL-TIME AUDITION DECK",
                        color = AngelicGold,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NeonMetalButton(
                            text = "ROAR FX",
                            iconText = "👹",
                            onClick = { audioEngine.triggerDemonicRoar() },
                            primaryColor = HellfireRed,
                            accentColor = BloodCrimson,
                            modifier = Modifier.weight(1f),
                            testTagId = "btn_audition_demon"
                        )

                        NeonMetalButton(
                            text = "CHOIR FX",
                            iconText = "🪽",
                            onClick = { audioEngine.triggerAngelicChoir() },
                            primaryColor = CyberTurquoise,
                            accentColor = AngelAqua,
                            modifier = Modifier.weight(1f),
                            testTagId = "btn_audition_angel"
                        )

                        NeonMetalButton(
                            text = "BLEGH FX",
                            iconText = "⚡",
                            onClick = { audioEngine.triggerBleghDrop() },
                            primaryColor = ToxicGreen,
                            accentColor = ElectricPurple,
                            modifier = Modifier.weight(1f),
                            testTagId = "btn_audition_blegh"
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
