package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AcidGreen
import com.example.ui.theme.AngelicGold
import com.example.ui.theme.BloodCrimson
import com.example.ui.theme.CyberTurquoise
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DeepAbyss
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.GlitchMagenta
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.NeonWhite
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceHighlight
import com.example.ui.theme.ToxicGreen
import com.example.ui.theme.VoidBlack
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Animated Glitch Header with chromatic aberration split (Cyan/Magenta offset)
 */
@Composable
fun GlitchText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: Int = 22,
    color: Color = NeonWhite,
    glitchEnabled: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glitch")
    val shiftX by infiniteTransition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(90, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shiftX"
    )

    Box(modifier = modifier) {
        if (glitchEnabled) {
            // Magenta Chromatic Split Layer
            Text(
                text = text,
                color = GlitchMagenta.copy(alpha = 0.75f),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = fontSize.sp,
                letterSpacing = 1.2.sp,
                modifier = Modifier.offset(x = shiftX.dp, y = (shiftX * 0.4f).dp)
            )
            // Cyber Turquoise Split Layer
            Text(
                text = text,
                color = CyberTurquoise.copy(alpha = 0.75f),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = fontSize.sp,
                letterSpacing = 1.2.sp,
                modifier = Modifier.offset(x = (-shiftX).dp, y = (-shiftX * 0.3f).dp)
            )
        }
        // Main Core Text
        Text(
            text = text,
            color = color,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            fontSize = fontSize.sp,
            letterSpacing = 1.2.sp
        )
    }
}

/**
 * Cyber/Metal Card with glowing multi-color gradient border & beveled cut-corners
 */
@Composable
fun CyberMetalCard(
    modifier: Modifier = Modifier,
    borderColor: Color = ElectricPurple,
    accentColor: Color = AcidGreen,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(borderColor.copy(alpha = 0.6f), DarkBorder, accentColor.copy(alpha = 0.4f))
                ),
                shape = shape
            )
            .clip(shape)
            .background(SurfaceContainer)
            .padding(16.dp)
    ) {
        content()
    }
}

/**
 * High-Impact Neon Action Button with minimum 48dp touch target
 */
@Composable
fun NeonMetalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconText: String? = null,
    primaryColor: Color = ElectricPurple,
    accentColor: Color = AcidGreen,
    testTagId: String = "neon_action_button",
    enabled: Boolean = true
) {
    val shape = RoundedCornerShape(12.dp)
    
    Surface(
        modifier = modifier
            .heightIn(min = 48.dp)
            .testTag(testTagId)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = accentColor),
                enabled = enabled,
                onClick = onClick
            ),
        shape = shape,
        color = if (enabled) SurfaceHighlight else SurfaceDark,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (enabled) Brush.horizontalGradient(listOf(primaryColor, accentColor)) else Brush.linearGradient(listOf(DarkBorder, Color.DarkGray))
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (iconText != null) {
                Text(
                    text = iconText,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Text(
                text = text.uppercase(),
                color = if (enabled) NeonWhite else Color.Gray,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 0.8.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Real-time 8-Band Extreme Metal Audio Spectrum Analyzer
 */
@Composable
fun AudioSpectrumVisualizer(
    spectrum: FloatArray,
    modifier: Modifier = Modifier,
    primaryColor: Color = ElectricPurple,
    secondaryColor: Color = ToxicGreen,
    barCount: Int = 12
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        val width = size.width
        val height = size.height
        val barWidth = (width / barCount) * 0.72f
        val gap = (width / barCount) * 0.28f

        for (i in 0 until barCount) {
            val specIndex = (i % spectrum.size)
            val rawAmp = spectrum.getOrElse(specIndex) { 0.2f }
            val barHeight = (height * rawAmp).coerceIn(4f, height)

            val x = i * (barWidth + gap) + gap / 2
            val y = height - barHeight

            val brush = Brush.verticalGradient(
                colors = listOf(
                    if (i % 3 == 0) BloodCrimson else if (i % 2 == 0) ToxicGreen else CyberTurquoise,
                    primaryColor
                ),
                startY = y,
                endY = height
            )

            drawRect(
                brush = brush,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight)
            )

            // Top peak cap
            drawRect(
                color = NeonWhite,
                topLeft = Offset(x, (y - 3f).coerceAtLeast(0f)),
                size = Size(barWidth, 2f)
            )
        }
    }
}

/**
 * Circular Demon/Angel Duality Runic Mandala Visualizer
 */
@Composable
fun DemonicAngelicRunicVisualizer(
    amplitude: Float,
    balance: Float, // 0.0 demon, 1.0 angel
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rune_rot")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rot"
    )

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = (size.minDimension / 2) * 0.85f

        // 1. Background Void Ring
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    if (balance < 0.5f) ElectricPurple.copy(alpha = 0.35f) else CyberTurquoise.copy(alpha = 0.35f),
                    Color.Transparent
                ),
                center = center,
                radius = radius * (1.0f + amplitude * 0.4f)
            ),
            radius = radius * (1.0f + amplitude * 0.3f),
            center = center
        )

        // 2. Rotating Demonic Star / Angelic Ring
        rotate(rotation, center) {
            // Outer Octagram
            val points = 8
            val path = Path()
            for (i in 0 until points * 2) {
                val r = if (i % 2 == 0) radius else radius * 0.55f * (1.0f + amplitude * 0.2f)
                val angle = (i * PI / points).toFloat()
                val px = center.x + r * cos(angle)
                val py = center.y + r * sin(angle)
                if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
            }
            path.close()

            drawPath(
                path = path,
                color = if (balance < 0.5f) BloodCrimson.copy(alpha = 0.8f) else CyberTurquoise.copy(alpha = 0.8f),
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // 3. Counter-rotating Inner Celestial Hexagon
        rotate(-rotation * 1.5f, center) {
            val hexPoints = 6
            val innerPath = Path()
            for (i in 0 until hexPoints) {
                val angle = (i * 2 * PI / hexPoints).toFloat()
                val r = radius * 0.45f * (1.0f + amplitude * 0.35f)
                val px = center.x + r * cos(angle)
                val py = center.y + r * sin(angle)
                if (i == 0) innerPath.moveTo(px, py) else innerPath.lineTo(px, py)
            }
            innerPath.close()

            drawPath(
                path = innerPath,
                color = if (balance < 0.5f) ToxicGreen else AngelicGold,
                style = Stroke(width = 1.5.dp.toPx())
            )
        }

        // 4. Core Pulsing Reactor Eye
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    NeonWhite,
                    if (balance < 0.5f) ElectricPurple else CyberTurquoise,
                    Color.Transparent
                ),
                center = center,
                radius = radius * 0.25f * (1.0f + amplitude * 0.5f)
            ),
            radius = radius * 0.25f * (1.0f + amplitude * 0.5f),
            center = center
        )
    }
}

/**
 * CRT Scanline & Glitch Canvas Overlay
 */
@Composable
fun CrtScanlineOverlay(
    modifier: Modifier = Modifier,
    scanlineAlpha: Float = 0.15f
) {
    Canvas(modifier = modifier) {
        val height = size.height
        val lineSpacing = 4.dp.toPx()
        var y = 0f
        while (y < height) {
            drawLine(
                color = Color.Black.copy(alpha = scanlineAlpha),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.5f
            )
            y += lineSpacing
        }
    }
}
