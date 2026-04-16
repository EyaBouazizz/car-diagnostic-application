package com.example.diagassistant.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diagassistant.ui.theme.DiagPalette
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

private object GaugeColors {
    val Safe = DiagPalette.AccentBlue          // blue
    val Warn = DiagPalette.Warn                // orange/yellow (from your palette)
    val Danger = Color(0xFFFF4D4D)             // red (you can tweak)
    val Track = DiagPalette.Stroke             // dark track
    val Pin = Color.White
    val PinShadow = Color(0x66000000)
}

/**
 * Returns a color based on how "high" the value is (0..1).
 * < 0.65 => Safe (blue)
 * 0.65..0.85 => Warn (orange)
 * > 0.85 => Danger (red)
 */
private fun severityColor(pct: Float): Color {
    val p = pct.coerceIn(0f, 1f)
    return when {
        p < 0.65f -> GaugeColors.Safe
        p < 0.85f -> lerp(GaugeColors.Safe, GaugeColors.Warn, (p - 0.65f) / 0.20f)
        else -> lerp(GaugeColors.Warn, GaugeColors.Danger, (p - 0.85f) / 0.15f)
    }
}

private fun degToRad(deg: Float): Float = (deg * PI.toFloat() / 180f)



@Composable
fun GaugePanel(
    selected: GaugeItem,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        when (selected) {
            GaugeItem.RPM -> {
                val value = 3850
                val max = 8000
                DialGauge(
                    title = "RPM",
                    valueText = "%,d".format(value),
                    unitText = "RPM",
                    pct = value.toFloat() / max.toFloat(),
                    dangerWhenHigh = true,
                    subtitle = "Engine speed"
                )
            }

            GaugeItem.TEMP -> {
                val valueC = 92f
                val minC = 0f
                val maxC = 120f
                DialGauge(
                    title = "ENGINE TEMP",
                    valueText = valueC.toInt().toString(),
                    unitText = "°C",
                    pct = ((valueC - minC) / (maxC - minC)),
                    dangerWhenHigh = true,
                    subtitle = "Normal: 80–100°C"
                )
            }

            GaugeItem.COOLANT -> {
                val valueC = 88f
                val minC = 60f
                val maxC = 115f
                DialGauge(
                    title = "COOLANT",
                    valueText = valueC.toInt().toString(),
                    unitText = "°C",
                    pct = ((valueC - minC) / (maxC - minC)),
                    dangerWhenHigh = true,
                    subtitle = "Source: ECT sensor"
                )
            }

            GaugeItem.PRESSURE -> {
                val valueBar = 2.2f
                val minBar = 0f
                val maxBar = 3.5f
                DialGauge(
                    title = "AIR PRESSURE",
                    valueText = String.format("%.1f", valueBar),
                    unitText = "BAR",
                    pct = ((valueBar - minBar) / (maxBar - minBar)),
                    dangerWhenHigh = true,
                    subtitle = "Typical: 1.8–2.6 bar"
                )
            }

            GaugeItem.OIL -> {
                val valueBar = 2.6f
                val minBar = 0f
                val maxBar = 5f
                DialGauge(
                    title = "OIL PRESSURE",
                    valueText = String.format("%.1f", valueBar),
                    unitText = "BAR",
                    pct = ((valueBar - minBar) / (maxBar - minBar)),
                    dangerWhenHigh = false,
                    subtitle = "Low pressure is critical",
                    useSeverityColorForArc = true
                )
            }

            GaugeItem.FUEL -> {
                val pctFuel = 5f
                DialGauge(
                    title = "FUEL LEVEL",
                    valueText = pctFuel.toInt().toString(),
                    unitText = "%",
                    pct = pctFuel / 100f,
                    dangerWhenHigh = false,
                    subtitle = "Refuel < 15%",
                    useSeverityColorForArc = true
                )
            }

            GaugeItem.ABS -> {
                val health = 0.50f
                DialGauge(
                    title = "ABS STATUS",
                    valueText = "OK",
                    unitText = "HEALTH",
                    pct = 1f - health,
                    dangerWhenHigh = true,
                    subtitle = "Monitoring enabled"
                )
            }
        }
    }
}
@Composable
private fun DialGauge(
    title: String,
    valueText: String,
    unitText: String,
    pct: Float,                 // 0..1
    dangerWhenHigh: Boolean,    // true: high => red; false: low => red
    subtitle: String? = null,
    useSeverityColorForArc: Boolean = false
) {
    val p = pct.coerceIn(0f, 1f)
    val severityPct = if (dangerWhenHigh) p else (1f - p)
    val accent = severityColor(severityPct)

    val startAngle = 140f
    val sweepTotal = 260f
    val sweep = sweepTotal * p

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            // CHANGE 1: push the whole gauge DOWN a bit
            .padding(top = 26.dp)
    ) {
        // CHANGE 2: Box overlay so we can draw TEXT inside the center circle
        Box(
            modifier = Modifier.size(400.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stroke = 34.dp.toPx()
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.minDimension / 2.15f
                val rect = Rect(center = center, radius = radius)

                // Track arc
                drawArc(
                    color = DiagPalette.Stroke,
                    startAngle = startAngle,
                    sweepAngle = sweepTotal,
                    useCenter = false,
                    topLeft = Offset(rect.left, rect.top),
                    size = rect.size,
                    style = Stroke(stroke, cap = StrokeCap.Round)
                )

                // Progress arc
                drawArc(
                    brush = if (useSeverityColorForArc) {
                        // arc uses severity color (fuel/oil pressure low => red)
                        Brush.linearGradient(
                            colors = listOf(accent.copy(alpha = 0.65f), accent)
                        )
                    } else {
                        // original position-based gradient
                        Brush.sweepGradient(
                            colorStops = arrayOf(
                                0.00f to DiagPalette.AccentBlue,
                                0.70f to DiagPalette.AccentBlue,
                                0.86f to DiagPalette.Warn,
                                1.00f to Color(0xFFFF4D4D)
                            ),
                            center = center
                        )
                    },
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(rect.left, rect.top),
                    size = rect.size,
                    style = Stroke(stroke, cap = StrokeCap.Round)
                )



                drawCircle(
                    color = DiagPalette.Surface2,
                    radius = 108.dp.toPx(),
                    center = center
                )
                drawCircle(
                    color = accent.copy(alpha = 0.20f),
                    radius = 110.dp.toPx(),
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // Text inside the circle
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = valueText,
                    color = DiagPalette.TextPrimary,
                    fontSize = 54.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = unitText,
                    color = accent,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp
                )
            }
        }

        // Below-gauge labels (optional; keep them if you like)

        Text(title, color = DiagPalette.TextSecondary, letterSpacing = 2.sp, fontSize = 14.sp)

        if (subtitle != null) {

            Text(
                subtitle,
                color = DiagPalette.TextSecondary.copy(alpha = 0.95f),
                fontSize = 16.sp,
                lineHeight = 20.sp
            )
        }
    }
}




// helpers using Float trig (avoid Double conversions everywhere)
private fun cos(r: Float) = kotlin.math.cos(r)
private fun sin(r: Float) = kotlin.math.sin(r)