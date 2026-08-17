package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.MetalAudioEngine
import com.example.ui.components.CyberMetalCard
import com.example.ui.components.DemonicAngelicRunicVisualizer
import com.example.ui.components.GlitchText
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
import com.example.ui.theme.SurfaceHighlight
import com.example.ui.theme.ToxicGreen
import com.example.ui.theme.VoidBlack

data class MoshPadItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: String,
    val color: Color,
    val action: (MetalAudioEngine) -> Unit
)

@Composable
fun MoshPadScreen(
    audioEngine: MetalAudioEngine,
    visualizerAmp: Float,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var lastTriggeredPad by remember { mutableIntStateOf(-1) }

    fun triggerVibration() {
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator?.vibrate(45)
            }
        } catch (_: Exception) {}
    }

    val pads = listOf(
        MoshPadItem("1", "DROP-Z CHUG", "8-STRING DJENT", "💀", HellfireRed) { it.triggerDjentChug(true) },
        MoshPadItem("2", "DEMONIC ROAR", "SUB-GUTTURAL", "👹", BloodCrimson) { it.triggerDemonicRoar() },
        MoshPadItem("3", "ANGELIC CHOIR", "SERAPH HARMONY", "🪽", CyberTurquoise) { it.triggerAngelicChoir() },
        MoshPadItem("4", "BLEGH DROP", "VOCAL BREAKDOWN", "⚡", ToxicGreen) { it.triggerBleghDrop() },
        MoshPadItem("5", "GLITCH LASER", "BITCRUSH ZAP", "👾", GlitchMagenta) { it.triggerGlitchLaser() },
        MoshPadItem("6", "SIREN SQUEAL", "WHAMMY DIVE", "🚨", AngelicGold) { it.triggerSirenSqueal() },
        MoshPadItem("7", "DOUBLE BLAST", "320 BPM BURST", "💥", NeonViolet) { it.triggerDjentChug(false) },
        MoshPadItem("8", "VOID CHIME", "CELESTIAL CHORD", "✨", AngelAqua) { it.triggerAngelicChoir() }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlitchText(
                text = "MOSH-PAD FX & LIVE DECK",
                fontSize = 18,
                color = HellfireRed
            )

            Surface(
                shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                color = HellfireRed.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, HellfireRed)
            ) {
                Text(
                    text = "REAL-TIME PCM",
                    color = HellfireRed,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }

        // Live Mandala Center Orb
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp))
                .background(SurfaceDark)
                .border(1.5.dp, Brush.horizontalGradient(listOf(BloodCrimson, ElectricPurple, CyberTurquoise)), CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp)),
            contentAlignment = Alignment.Center
        ) {
            DemonicAngelicRunicVisualizer(
                amplitude = visualizerAmp,
                balance = 0.4f,
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = "TAP MOSH PADS FOR LIVE STAGE BLASTS",
                color = NeonWhite.copy(alpha = 0.85f),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 6.dp)
            )
        }

        // 8 Live Performance Arcade Pads
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(pads) { pad ->
                val padIndex = pads.indexOf(pad)
                val isPressed = (lastTriggeredPad == padIndex)
                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.94f else 1.0f,
                    animationSpec = tween(120),
                    label = "pad_scale"
                )

                Surface(
                    modifier = Modifier
                        .height(96.dp)
                        .scale(scale)
                        .testTag("mosh_pad_${pad.id}")
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = pad.color),
                            onClick = {
                                lastTriggeredPad = padIndex
                                triggerVibration()
                                pad.action(audioEngine)
                            }
                        ),
                    shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
                    color = pad.color.copy(alpha = 0.18f),
                    border = androidx.compose.foundation.BorderStroke(
                        2.dp,
                        Brush.linearGradient(listOf(pad.color, pad.color.copy(alpha = 0.4f)))
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = pad.icon,
                                fontSize = 22.sp
                            )
                            Text(
                                text = "PAD 0${pad.id}",
                                color = pad.color,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        }

                        Column {
                            Text(
                                text = pad.title,
                                color = NeonWhite,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = pad.subtitle,
                                color = pad.color,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}
