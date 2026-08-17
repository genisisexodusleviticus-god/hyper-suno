package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.MetalAudioEngine
import com.example.data.MetalTrack
import com.example.data.SceneType
import com.example.data.VideoScene
import com.example.ui.components.CrtScanlineOverlay
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
import com.example.ui.theme.GlitchMagenta
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun VideoStudioScreen(
    currentTrack: MetalTrack,
    audioEngine: MetalAudioEngine,
    isPlaying: Boolean,
    currentStep: Int,
    visualizerAmp: Float,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var selectedSceneIndex by remember { mutableIntStateOf(0) }
    var glitchIntensity by remember { mutableFloatStateOf(0.75f) }
    var cameraShakeFactor by remember { mutableFloatStateOf(0.60f) }
    var strobeFlashActive by remember { mutableStateOf(true) }
    var isRenderingVideo by remember { mutableStateOf(false) }
    var renderProgress by remember { mutableFloatStateOf(0f) }
    var renderStatusMessage by remember { mutableStateOf<String?>(null) }

    val scenes = currentTrack.scenes
    val activeScene = scenes.getOrElse(selectedSceneIndex) { scenes.first() }

    val infiniteTransition = rememberInfiniteTransition(label = "video_glitch")
    val timeParam by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    // Dynamic Camera Shake based on audio kick & breakdown steps
    val beatShake = if (isPlaying && (currentStep % 2 == 0)) (visualizerAmp * 14f * cameraShakeFactor) else 0f
    val shakeOffsetX = (sin(timeParam * 10f) * beatShake).dp
    val shakeOffsetY = (cos(timeParam * 12f) * beatShake).dp

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Video Director Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlitchText(
                    text = "CINEMATIC VIDEO SYNTH",
                    fontSize = 18,
                    color = CyberTurquoise
                )

                Surface(
                    shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                    color = ElectricPurple.copy(alpha = 0.3f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricPurple)
                ) {
                    Text(
                        text = "SUNO 5.5 V-ENGINE",
                        color = NeonWhite,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // Live Animated Canvas Video Viewport
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .offset(x = shakeOffsetX, y = shakeOffsetY)
                    .clip(CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp))
                    .border(
                        2.dp,
                        Brush.horizontalGradient(
                            listOf(
                                Color(activeScene.sceneType.themeColor),
                                ElectricPurple,
                                AcidGreen
                            )
                        ),
                        CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp)
                    )
                    .background(DeepAbyss)
            ) {
                // Real-time Visual Synthesizer Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val cx = w / 2f
                    val cy = h / 2f

                    // 1. Background Void / Scene Theme Gradient
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(activeScene.sceneType.themeColor).copy(alpha = 0.45f),
                                VoidBlack
                            ),
                            center = Offset(cx, cy),
                            radius = w * 0.75f
                        )
                    )

                    // 2. DEMON HALF (Left side): Horn Flame Arcs & Hellfire
                    val demonColor = HellfireRed.copy(alpha = 0.8f)
                    val hornPath = Path()
                    hornPath.moveTo(cx - 30f, cy + 60f)
                    hornPath.cubicTo(
                        cx - 100f, cy - 20f,
                        cx - 140f - (visualizerAmp * 40f), cy - 100f,
                        cx - 80f, cy - 130f
                    )
                    hornPath.cubicTo(
                        cx - 110f, cy - 60f,
                        cx - 60f, cy,
                        cx - 10f, cy + 40f
                    )
                    hornPath.close()

                    drawPath(
                        path = hornPath,
                        brush = Brush.verticalGradient(listOf(BloodCrimson, HellfireRed, ElectricPurple))
                    )

                    // 3. ANGEL HALF (Right side): Celestial Six-Wing Array & Sacred Halo
                    val angelColor = CyberTurquoise.copy(alpha = 0.85f)
                    val wingPath = Path()
                    wingPath.moveTo(cx + 30f, cy + 60f)
                    wingPath.cubicTo(
                        cx + 100f, cy - 20f,
                        cx + 150f + (visualizerAmp * 40f), cy - 110f,
                        cx + 90f, cy - 140f
                    )
                    wingPath.cubicTo(
                        cx + 120f, cy - 60f,
                        cx + 60f, cy,
                        cx + 10f, cy + 40f
                    )
                    wingPath.close()

                    drawPath(
                        path = wingPath,
                        brush = Brush.verticalGradient(listOf(AngelicGold, CyberTurquoise, AngelAqua))
                    )

                    // 4. Central Duality Orb & Resonant Mandala
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                NeonWhite,
                                if (currentStep % 4 == 0) ToxicGreen else ElectricPurple,
                                Color.Transparent
                            ),
                            center = Offset(cx, cy - 20f),
                            radius = 60f * (1f + visualizerAmp * 0.8f)
                        ),
                        radius = 60f * (1f + visualizerAmp * 0.8f),
                        center = Offset(cx, cy - 20f)
                    )

                    // 5. Laser Ray Matrix
                    for (i in 0 until 12) {
                        val angle = (i * 30f + timeParam * 2f) * PI.toFloat() / 180f
                        val rayLen = (120f + 60f * sin(timeParam * 3f + i)) * (1f + visualizerAmp)
                        val start = Offset(cx, cy - 20f)
                        val end = Offset(cx + cos(angle) * rayLen, cy - 20f + sin(angle) * rayLen)

                        drawLine(
                            color = if (i % 2 == 0) CyberTurquoise.copy(alpha = 0.6f) else ElectricPurple.copy(alpha = 0.6f),
                            start = start,
                            end = end,
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    // 6. Glitch Artifacts & Slicing Scanlines
                    if (glitchIntensity > 0.3f && isPlaying) {
                        val glitchCount = (glitchIntensity * 8).toInt()
                        for (g in 0 until glitchCount) {
                            val gy = (h * ((g * 37 + (timeParam * 5).toInt()) % 100) / 100f)
                            val gh = (4f + (g * 3) % 12).dp.toPx()
                            val gOffset = ((sin(timeParam * 8f + g) * 30f * glitchIntensity)).dp.toPx()

                            drawRect(
                                color = if (g % 2 == 0) GlitchMagenta.copy(alpha = 0.4f) else ToxicGreen.copy(alpha = 0.4f),
                                topLeft = Offset(gOffset, gy),
                                size = Size(w, gh)
                            )
                        }
                    }

                    // 7. Strobe Light Flash on Heavy Steps
                    if (strobeFlashActive && isPlaying && (currentStep == 0 || currentStep == 8) && visualizerAmp > 0.4f) {
                        drawRect(
                            color = Color.White.copy(alpha = 0.25f),
                            topLeft = Offset.Zero,
                            size = size
                        )
                    }
                }

                // CRT Overlay
                CrtScanlineOverlay(modifier = Modifier.fillMaxSize(), scanlineAlpha = 0.2f)

                // Top Viewport Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = VoidBlack.copy(alpha = 0.8f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(activeScene.sceneType.themeColor))
                    ) {
                        Text(
                            text = "● REC [60 FPS // 4K RAW]",
                            color = if (isPlaying) BloodCrimson else MutedSlate,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    Text(
                        text = "SCENE: ${activeScene.sceneType.title.uppercase()}",
                        color = Color(activeScene.sceneType.themeColor),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                // Bottom Live Kinetic Lyrics
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(listOf(Color.Transparent, VoidBlack.copy(alpha = 0.9f)))
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    val currentLyric = currentTrack.lyrics.getOrElse((currentStep / 3) % currentTrack.lyrics.size) { currentTrack.lyrics.first() }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = currentLyric.screamAnnotation,
                            color = AngelicGold,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                        GlitchText(
                            text = currentLyric.lyrics.lines().firstOrNull() ?: "",
                            fontSize = 14,
                            color = NeonWhite,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        // Scene Selector Strip
        item {
            Column {
                Text(
                    text = "CINEMATIC SCENE TIMELINE",
                    color = ToxicGreen,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(scenes) { scene ->
                        val isSelected = scenes.indexOf(scene) == selectedSceneIndex
                        val sceneColor = Color(scene.sceneType.themeColor)

                        Surface(
                            shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                            color = if (isSelected) SurfaceHighlight else SurfaceDark,
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) sceneColor else DarkBorder
                            ),
                            modifier = Modifier
                                .width(160.dp)
                                .clickable { selectedSceneIndex = scenes.indexOf(scene) }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = scene.title,
                                    color = if (isSelected) NeonWhite else MutedSlate,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = scene.promptDescription,
                                    color = MutedSlate,
                                    fontFamily = FontFamily.Default,
                                    fontSize = 10.sp,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }

        // Director Controls: Glitch & Camera Shaders
        item {
            CyberMetalCard(
                borderColor = ElectricPurple,
                accentColor = CyberTurquoise
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "DIRECTOR FX & GLITCH SHADERS",
                        color = CyberTurquoise,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Glitch Chaos Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "GLITCH CHAOS FACTOR", color = GlitchMagenta, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            Text(text = "${(glitchIntensity * 100).toInt()}%", color = NeonWhite, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                        Slider(
                            value = glitchIntensity,
                            onValueChange = { glitchIntensity = it },
                            colors = SliderDefaults.colors(
                                thumbColor = GlitchMagenta,
                                activeTrackColor = GlitchMagenta,
                                inactiveTrackColor = SurfaceContainer
                            )
                        )
                    }

                    // Camera Shake Factor Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "BASS CAMERA SHAKE", color = ToxicGreen, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            Text(text = "${(cameraShakeFactor * 100).toInt()}%", color = NeonWhite, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                        Slider(
                            value = cameraShakeFactor,
                            onValueChange = { cameraShakeFactor = it },
                            colors = SliderDefaults.colors(
                                thumbColor = ToxicGreen,
                                activeTrackColor = ToxicGreen,
                                inactiveTrackColor = SurfaceContainer
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Render Video Clip Simulator
                    NeonMetalButton(
                        text = if (isRenderingVideo) "RENDERING 4K MUSIC VIDEO..." else "EXPORT 4K MUSIC VIDEO CLIP",
                        iconText = if (isRenderingVideo) "⏳" else "🎬",
                        onClick = {
                            scope.launch {
                                isRenderingVideo = true
                                renderProgress = 0f
                                for (p in 1..10) {
                                    delay(200)
                                    renderProgress = p / 10f
                                }
                                isRenderingVideo = false
                                renderStatusMessage = "4K Music Video Exported Successfully to Vault!"
                            }
                        },
                        primaryColor = ElectricPurple,
                        accentColor = ToxicGreen,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isRenderingVideo,
                        testTagId = "btn_export_video"
                    )

                    if (isRenderingVideo) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { renderProgress },
                            color = ToxicGreen,
                            trackColor = SurfaceContainer,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (renderStatusMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = renderStatusMessage!!,
                            color = ToxicGreen,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
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
