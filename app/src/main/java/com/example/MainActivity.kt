package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.MetalAudioEngine
import com.example.data.MetalAiGenerator
import com.example.data.MetalTrack
import com.example.ui.screens.HyperWorkstationScreen
import com.example.ui.screens.MoshPadScreen
import com.example.ui.screens.StepSequencerScreen
import com.example.ui.screens.VideoStudioScreen
import com.example.ui.screens.VocalDspScreen
import com.example.ui.theme.AcidGreen
import com.example.ui.theme.AngelAqua
import com.example.ui.theme.BloodCrimson
import com.example.ui.theme.CyberTurquoise
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DeepAbyss
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.HellfireRed
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.NeonWhite
import com.example.ui.theme.SunoHyperTheme
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceHighlight
import com.example.ui.theme.ToxicGreen
import com.example.ui.theme.VoidBlack

enum class AppModule(val label: String, val icon: ImageVector, val themeColor: Color) {
    WORKSTATION("STUDIO", Icons.Default.MusicNote, ElectricPurple),
    VIDEO_SYNTH("CINEMA", Icons.Default.Movie, CyberTurquoise),
    BEAT_MATRIX("MATRIX", Icons.Default.GridView, ToxicGreen),
    MOSH_PADS("MOSH", Icons.Default.ElectricBolt, HellfireRed),
    VOCAL_DSP("DSP FX", Icons.Default.Tune, NeonViolet)
}

class MainActivity : ComponentActivity() {

    private val audioEngine = MetalAudioEngine.INSTANCE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SunoHyperTheme {
                MainAppContainer(audioEngine = audioEngine)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioEngine.release()
    }
}

@Composable
fun MainAppContainer(audioEngine: MetalAudioEngine) {
    var activeModule by remember { mutableStateOf(AppModule.WORKSTATION) }
    var currentTrack by remember { mutableStateOf(MetalAiGenerator.DEFAULT_TRACKS.first()) }
    var isPlaying by remember { mutableStateOf(audioEngine.isPlaying) }
    var currentStep by remember { mutableIntStateOf(0) }
    var visualizerAmp by remember { mutableFloatStateOf(0.1f) }
    var visualizerSpectrum by remember { mutableStateOf(FloatArray(8) { 0.2f }) }

    // Register audio engine listeners
    DisposableEffect(audioEngine) {
        audioEngine.onStepCallback = { step ->
            currentStep = step
            isPlaying = audioEngine.isPlaying
        }
        audioEngine.onVisualizerCallback = { amp, spec ->
            visualizerAmp = amp
            visualizerSpectrum = spec
        }
        onDispose {
            audioEngine.onStepCallback = null
            audioEngine.onVisualizerCallback = null
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = VoidBlack,
        bottomBar = {
            BottomCyberNavigationBar(
                activeModule = activeModule,
                onModuleSelected = { activeModule = it },
                isPlaying = isPlaying
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // Global Top Status HUD
            TopCyberStatusHud(
                currentTrack = currentTrack,
                isPlaying = isPlaying,
                currentStep = currentStep
            )

            // Active Module Screen Content
            Box(modifier = Modifier.weight(1f)) {
                when (activeModule) {
                    AppModule.WORKSTATION -> HyperWorkstationScreen(
                        currentTrack = currentTrack,
                        onTrackChanged = { currentTrack = it },
                        audioEngine = audioEngine,
                        isPlaying = isPlaying,
                        onTogglePlay = {
                            if (audioEngine.isPlaying) {
                                audioEngine.stopSequencer()
                                isPlaying = false
                            } else {
                                audioEngine.startSequencer()
                                isPlaying = true
                            }
                        },
                        currentStep = currentStep,
                        visualizerAmp = visualizerAmp,
                        visualizerSpectrum = visualizerSpectrum
                    )

                    AppModule.VIDEO_SYNTH -> VideoStudioScreen(
                        currentTrack = currentTrack,
                        audioEngine = audioEngine,
                        isPlaying = isPlaying,
                        currentStep = currentStep,
                        visualizerAmp = visualizerAmp
                    )

                    AppModule.BEAT_MATRIX -> StepSequencerScreen(
                        audioEngine = audioEngine,
                        isPlaying = isPlaying,
                        onTogglePlay = {
                            if (audioEngine.isPlaying) {
                                audioEngine.stopSequencer()
                                isPlaying = false
                            } else {
                                audioEngine.startSequencer()
                                isPlaying = true
                            }
                        },
                        currentStep = currentStep
                    )

                    AppModule.MOSH_PADS -> MoshPadScreen(
                        audioEngine = audioEngine,
                        visualizerAmp = visualizerAmp
                    )

                    AppModule.VOCAL_DSP -> VocalDspScreen(
                        audioEngine = audioEngine
                    )
                }
            }
        }
    }
}

@Composable
fun TopCyberStatusHud(
    currentTrack: MetalTrack,
    isPlaying: Boolean,
    currentStep: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark)
            .border(
                1.dp,
                DarkBorder,
                androidx.compose.foundation.shape.RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SYSTEM v5.5 REVAMPED",
                    color = AcidGreen,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    letterSpacing = 1.5.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "SUNO",
                        color = NeonWhite,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "//",
                        color = ElectricPurple,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "VOID",
                        color = NeonWhite,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Angelic Glyph Badge
                Surface(
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = SurfaceHighlight,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberTurquoise.copy(alpha = 0.6f)),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "𐕣",
                            color = CyberTurquoise,
                            fontSize = 14.sp
                        )
                    }
                }

                // Demonic Glyph Badge
                Surface(
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = SurfaceHighlight,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricPurple.copy(alpha = 0.6f)),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "𖤐",
                            color = ElectricPurple,
                            fontSize = 14.sp
                        )
                    }
                }

                // Status Badge
                Surface(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                    color = if (isPlaying) AcidGreen.copy(alpha = 0.15f) else SurfaceContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isPlaying) AcidGreen else DarkBorder)
                ) {
                    Text(
                        text = if (isPlaying) "PLAY // S${currentStep + 1}" else "READY",
                        color = if (isPlaying) AcidGreen else MutedSlate,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BottomCyberNavigationBar(
    activeModule: AppModule,
    onModuleSelected: (AppModule) -> Unit,
    isPlaying: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
        color = SurfaceDark,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            DarkBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppModule.values().forEach { module ->
                val isSelected = (activeModule == module)
                val itemColor = module.themeColor

                Column(
                    modifier = Modifier
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
                        .clickable { onModuleSelected(module) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("nav_tab_${module.name.lowercase()}"),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = module.icon,
                        contentDescription = module.label,
                        tint = if (isSelected) itemColor else MutedSlate,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = module.label,
                        color = if (isSelected) NeonWhite else MutedSlate,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 9.sp,
                        letterSpacing = 0.5.sp
                    )

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .padding(top = 3.dp)
                                .size(width = 14.dp, height = 2.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(itemColor)
                        )
                    }
                }
            }
        }
    }
}
