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
    Surface(
        // CHANGE: do not force a small width/height here
        modifier = modifier,
        shape = RoundedCornerShape(34.dp),
        color = DiagPalette.Surface1
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (selected) {
                GaugeItem.RPM -> RpmArcGauge(value = 3850, max = 8000, label = "RPM")
                GaugeItem.TEMP -> ThermometerGauge(valueC = 92f, minC = 0f, maxC = 120f, label = "ENGINE TEMP")
                GaugeItem.COOLANT -> CoolantDialGauge(valueC = 88f, minC = 60f, maxC = 115f, label = "COOLANT")
                GaugeItem.OIL -> OilPressureGauge(valueBar = 2.6f, minBar = 0f, maxBar = 5f, label = "OIL")
                GaugeItem.ABS -> PulseRingGauge(value = 0.10f, label = "ABS STATUS", text = "OK")
                GaugeItem.PRESSURE -> DualArcGauge(value = 2.2f, min = 0f, max = 3.5f, label = "BAR")
                GaugeItem.FUEL -> FuelCapsuleGauge(valuePct = 63f, label = "FUEL")
            }
        }
    }
}

@Composable
private fun RpmArcGauge(value: Int, max: Int, label: String) {
    val v = value.coerceIn(0, max)
    val pct = v.toFloat() / max.toFloat()

    val startAngle = 150f
    val sweepTotal = 240f
    val sweep = sweepTotal * pct
    val colorNow = severityColor(pct)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(modifier = Modifier.size(460.dp)) {
            val stroke = 26.dp.toPx()
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2.15f
            val rect = Rect(center = center, radius = radius)

            // Track arc
            drawArc(
                color = GaugeColors.Track,
                startAngle = startAngle,
                sweepAngle = sweepTotal,
                useCenter = false,
                topLeft = Offset(rect.left, rect.top),
                size = rect.size,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            // Progress arc with severity gradient (blue -> warn -> red)
            drawArc(
                brush = Brush.sweepGradient(
                    colorStops = arrayOf(
                        0.00f to GaugeColors.Safe,
                        0.70f to GaugeColors.Safe,
                        0.86f to GaugeColors.Warn,
                        1.00f to GaugeColors.Danger
                    ),
                    center = center
                ),
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = Offset(rect.left, rect.top),
                size = rect.size,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            // Ticks
            val tickCount = 12
            for (i in 0..tickCount) {
                val t = i / tickCount.toFloat()
                val a = degToRad(startAngle + sweepTotal * t)
                val inner = radius - stroke * 1.15f
                val outer = radius + stroke * 0.20f
                val p1 = Offset(
                    x = center.x + inner * cos(a),
                    y = center.y + inner * sin(a)
                )
                val p2 = Offset(
                    x = center.x + outer * cos(a),
                    y = center.y + outer * sin(a)
                )
                drawLine(
                    color = DiagPalette.TextSecondary.copy(alpha = 0.55f),
                    start = p1,
                    end = p2,
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // Needle / Pin
            val pinAngle = degToRad(startAngle + sweep)
            val pinLen = radius - stroke * 0.3f
            val pinEnd = Offset(
                x = center.x + pinLen * cos(pinAngle),
                y = center.y + pinLen * sin(pinAngle)
            )

            // Pin shadow
            drawLine(
                color = GaugeColors.PinShadow,
                start = center + Offset(2.dp.toPx(), 2.dp.toPx()),
                end = pinEnd + Offset(2.dp.toPx(), 2.dp.toPx()),
                strokeWidth = 6.dp.toPx(),
                cap = StrokeCap.Round
            )
            // Pin
            drawLine(
                color = GaugeColors.Pin,
                start = center,
                end = pinEnd,
                strokeWidth = 5.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Pin knob (center)
            drawCircle(
                color = colorNow,
                radius = 18.dp.toPx(),
                center = center
            )
            drawCircle(
                color = Color.White,
                radius = 8.dp.toPx(),
                center = center
            )
        }

        Spacer(Modifier.height(8.dp))
        Text(label, color = DiagPalette.TextSecondary, letterSpacing = 2.sp, fontSize = 12.sp)
        Text("$v", color = DiagPalette.TextPrimary, fontSize = 52.sp, fontWeight = FontWeight.SemiBold)
        Text("of $max", color = DiagPalette.TextSecondary, fontSize = 14.sp)
    }
}

// helpers using Float trig (avoid Double conversions everywhere)
private fun cos(r: Float) = kotlin.math.cos(r)
private fun sin(r: Float) = kotlin.math.sin(r)

@Composable
private fun ThermometerGauge(valueC: Float, minC: Float, maxC: Float, label: String) {
    val v = valueC.coerceIn(minC, maxC)
    val pct = ((v - minC) / (maxC - minC)).coerceIn(0f, 1f)
    val fillColor = severityColor(pct)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(36.dp)
    ) {
        Canvas(modifier = Modifier.size(width = 140.dp, height = 420.dp)) {
            val tubeW = size.width * 0.30f
            val tubeLeft = (size.width - tubeW) / 2f
            val tubeTop = 18f
            val tubeBottom = size.height - 86f

            // Tube track
            drawRoundRect(
                color = DiagPalette.Stroke,
                topLeft = Offset(tubeLeft, tubeTop),
                size = androidx.compose.ui.geometry.Size(tubeW, tubeBottom - tubeTop),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(48f, 48f)
            )

            // Fill
            val fillH = (tubeBottom - tubeTop) * pct
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(
                        fillColor.copy(alpha = 0.65f),
                        fillColor
                    )
                ),
                topLeft = Offset(tubeLeft, tubeBottom - fillH),
                size = androidx.compose.ui.geometry.Size(tubeW, fillH),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(48f, 48f)
            )

            // Pin marker (small horizontal line)
            val y = (tubeBottom - fillH).coerceIn(tubeTop, tubeBottom)
            val pinStart = Offset(tubeLeft - 18.dp.toPx(), y)
            val pinEnd = Offset(tubeLeft + tubeW + 18.dp.toPx(), y)

            drawLine(
                color = Color.Black.copy(alpha = 0.35f),
                start = pinStart + Offset(2.dp.toPx(), 2.dp.toPx()),
                end = pinEnd + Offset(2.dp.toPx(), 2.dp.toPx()),
                strokeWidth = 6.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White,
                start = pinStart,
                end = pinEnd,
                strokeWidth = 5.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Bulb
            drawCircle(
                color = fillColor,
                radius = 36.dp.toPx(),
                center = Offset(size.width / 2f, size.height - 44.dp.toPx())
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.25f),
                radius = 14.dp.toPx(),
                center = Offset(size.width / 2f + 10.dp.toPx(), size.height - 54.dp.toPx())
            )
        }

        Column(horizontalAlignment = Alignment.Start) {
            Text(label, color = DiagPalette.TextSecondary, letterSpacing = 2.sp, fontSize = 12.sp)
            Text("${v.toInt()}°C", color = DiagPalette.TextPrimary, fontSize = 52.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(10.dp))
            Text(
                "Range: ${minC.toInt()}–${maxC.toInt()}°C",
                color = DiagPalette.TextSecondary,
                fontSize = 14.sp
            )
        }
    }
}
@Composable
private fun PulseRingGauge(value: Float, label: String, text: String) {
    val pct = value.coerceIn(0f, 1f)
    val ringColor = severityColor(pct)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(modifier = Modifier.size(440.dp)) {
            val stroke = 28.dp.toPx()
            val center = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2.35f
            val rect = Rect(center = center, radius = r)

            // Track full ring
            drawArc(
                color = DiagPalette.Stroke,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(rect.left, rect.top),
                size = rect.size,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            // Active ring segment (looks like "monitoring")
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(
                        ringColor.copy(alpha = 0.20f),
                        ringColor,
                        ringColor.copy(alpha = 0.20f)
                    ),
                    center = center
                ),
                startAngle = -90f,
                sweepAngle = 260f,
                useCenter = false,
                topLeft = Offset(rect.left, rect.top),
                size = rect.size,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            // Pin dot at "current" severity angle
            val startAngle = -90f
            val sweepTotal = 260f
            val a = degToRad(startAngle + sweepTotal * pct)
            val dotR = r + stroke * 0.05f
            val dot = Offset(
                x = center.x + dotR * cos(a),
                y = center.y + dotR * sin(a)
            )
            drawCircle(Color.Black.copy(alpha = 0.35f), radius = 14.dp.toPx(), center = dot + Offset(2.dp.toPx(), 2.dp.toPx()))
            drawCircle(Color.White, radius = 13.dp.toPx(), center = dot)

            // Center hub
            drawCircle(color = ringColor, radius = 18.dp.toPx(), center = center)
            drawCircle(color = Color.White, radius = 7.dp.toPx(), center = center)
        }

        Spacer(Modifier.height(8.dp))
        Text(label, color = DiagPalette.TextSecondary, letterSpacing = 2.sp, fontSize = 12.sp)
        Text(text, color = DiagPalette.TextPrimary, fontSize = 52.sp, fontWeight = FontWeight.SemiBold)
        Text(
            when {
                pct < 0.65f -> "NORMAL"
                pct < 0.85f -> "WARNING"
                else -> "DANGER"
            },
            color = severityColor(pct),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
@Composable
private fun DualArcGauge(value: Float, min: Float, max: Float, label: String) {
    val v = value.coerceIn(min, max)
    val pct = ((v - min) / (max - min)).coerceIn(0f, 1f)

    val startAngle = 210f
    val sweepTotal = 240f
    val sweep = sweepTotal * pct
    val colorNow = severityColor(pct)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(modifier = Modifier.size(460.dp)) {
            val stroke = 22.dp.toPx()
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2.25f
            val rect = Rect(center = center, radius = radius)

            // Track arc
            drawArc(
                color = GaugeColors.Track,
                startAngle = startAngle,
                sweepAngle = sweepTotal,
                useCenter = false,
                topLeft = Offset(rect.left, rect.top),
                size = rect.size,
                style = Stroke(stroke, cap = StrokeCap.Round)
            )

            // Progress gradient arc
            drawArc(
                brush = Brush.sweepGradient(
                    colorStops = arrayOf(
                        0.00f to GaugeColors.Safe,
                        0.70f to GaugeColors.Safe,
                        0.86f to GaugeColors.Warn,
                        1.00f to GaugeColors.Danger
                    ),
                    center = center
                ),
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = Offset(rect.left, rect.top),
                size = rect.size,
                style = Stroke(stroke, cap = StrokeCap.Round)
            )

            // Needle / Pin
            val pinAngle = degToRad(startAngle + sweep)
            val pinLen = radius - stroke * 0.25f
            val pinEnd = Offset(
                x = center.x + pinLen * cos(pinAngle),
                y = center.y + pinLen * sin(pinAngle)
            )

            drawLine(
                color = GaugeColors.PinShadow,
                start = center + Offset(2.dp.toPx(), 2.dp.toPx()),
                end = pinEnd + Offset(2.dp.toPx(), 2.dp.toPx()),
                strokeWidth = 6.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White,
                start = center,
                end = pinEnd,
                strokeWidth = 5.dp.toPx(),
                cap = StrokeCap.Round
            )

            drawCircle(color = colorNow, radius = 16.dp.toPx(), center = center)
            drawCircle(color = Color.White, radius = 7.dp.toPx(), center = center)
        }

        Spacer(Modifier.height(8.dp))
        Text("PRESSURE", color = DiagPalette.TextSecondary, letterSpacing = 2.sp, fontSize = 12.sp)
        Text(String.format("%.1f", v), color = DiagPalette.TextPrimary, fontSize = 52.sp, fontWeight = FontWeight.SemiBold)
        Text(label, color = DiagPalette.TextSecondary, fontSize = 14.sp)
    }
}
@Composable
private fun FuelCapsuleGauge(valuePct: Float, label: String) {
    val v = valuePct.coerceIn(0f, 100f)
    // For fuel, "danger" is LOW fuel, so severity is inverted:
    val pctFull = v / 100f
    val pctDanger = 1f - pctFull
    val fillColor = severityColor(pctDanger)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(modifier = Modifier.size(width = 620.dp, height = 240.dp)) {
            val stroke = 18.dp.toPx()
            val rect = Rect(
                left = stroke,
                top = stroke,
                right = size.width - stroke,
                bottom = size.height - stroke
            )

            // Track capsule outline
            drawRoundRect(
                color = DiagPalette.Stroke,
                topLeft = Offset(rect.left, rect.top),
                size = rect.size,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(100f, 100f),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            // Fill
            val fillW = rect.width * pctFull
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    listOf(fillColor.copy(alpha = 0.55f), fillColor)
                ),
                topLeft = Offset(rect.left, rect.top),
                size = androidx.compose.ui.geometry.Size(fillW, rect.height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(100f, 100f)
            )

            // Pin marker (vertical line at current level)
            val pinX = rect.left + rect.width * pctFull
            val pinTop = rect.top - 10.dp.toPx()
            val pinBottom = rect.bottom + 10.dp.toPx()

            drawLine(
                color = Color.Black.copy(alpha = 0.35f),
                start = Offset(pinX + 2.dp.toPx(), pinTop + 2.dp.toPx()),
                end = Offset(pinX + 2.dp.toPx(), pinBottom + 2.dp.toPx()),
                strokeWidth = 7.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White,
                start = Offset(pinX, pinTop),
                end = Offset(pinX, pinBottom),
                strokeWidth = 6.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Small pin head dot
            drawCircle(
                color = Color.White,
                radius = 10.dp.toPx(),
                center = Offset(pinX, rect.top - 6.dp.toPx())
            )
        }

        Spacer(Modifier.height(10.dp))
        Text(label, color = DiagPalette.TextSecondary, letterSpacing = 2.sp, fontSize = 12.sp)
        Text("${v.toInt()}%", color = DiagPalette.TextPrimary, fontSize = 52.sp, fontWeight = FontWeight.SemiBold)
        Text(
            when {
                pctDanger < 0.65f -> "NORMAL"
                pctDanger < 0.85f -> "LOW"
                else -> "CRITICAL"
            },
            color = severityColor(pctDanger),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun CoolantDialGauge(valueC: Float, minC: Float, maxC: Float, label: String) {
    val v = valueC.coerceIn(minC, maxC)
    val pct = ((v - minC) / (maxC - minC)).coerceIn(0f, 1f)
    val colorNow = severityColor(pct)

    val startAngle = 140f
    val sweepTotal = 260f
    val sweep = sweepTotal * pct

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(modifier = Modifier.size(480.dp)) {
            val stroke = 26.dp.toPx()
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2.18f
            val rect = Rect(center = center, radius = radius)

            // Track
            drawArc(
                color = DiagPalette.Stroke,
                startAngle = startAngle,
                sweepAngle = sweepTotal,
                useCenter = false,
                topLeft = Offset(rect.left, rect.top),
                size = rect.size,
                style = Stroke(stroke, cap = StrokeCap.Round)
            )

            // Progress
            drawArc(
                brush = Brush.sweepGradient(
                    colorStops = arrayOf(
                        0.00f to GaugeColors.Safe,
                        0.70f to GaugeColors.Safe,
                        0.86f to GaugeColors.Warn,
                        1.00f to GaugeColors.Danger
                    ),
                    center = center
                ),
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = Offset(rect.left, rect.top),
                size = rect.size,
                style = Stroke(stroke, cap = StrokeCap.Round)
            )

            // Pin / needle
            val a = degToRad(startAngle + sweep)
            val pinLen = radius - stroke * 0.35f
            val end = Offset(
                x = center.x + pinLen * cos(a),
                y = center.y + pinLen * sin(a)
            )
            drawLine(
                color = Color.Black.copy(alpha = 0.35f),
                start = center + Offset(2.dp.toPx(), 2.dp.toPx()),
                end = end + Offset(2.dp.toPx(), 2.dp.toPx()),
                strokeWidth = 6.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White,
                start = center,
                end = end,
                strokeWidth = 5.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Hub
            drawCircle(color = colorNow, radius = 18.dp.toPx(), center = center)
            drawCircle(color = Color.White, radius = 8.dp.toPx(), center = center)
        }

        Spacer(Modifier.height(8.dp))
        Text(label, color = DiagPalette.TextSecondary, letterSpacing = 2.sp, fontSize = 12.sp)
        Text("${v.toInt()}°C", color = DiagPalette.TextPrimary, fontSize = 52.sp, fontWeight = FontWeight.SemiBold)
        Text(
            when {
                pct < 0.65f -> "OPTIMAL"
                pct < 0.85f -> "WARM"
                else -> "OVERHEAT RISK"
            },
            color = severityColor(pct),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun OilPressureGauge(valueBar: Float, minBar: Float, maxBar: Float, label: String) {
    val v = valueBar.coerceIn(minBar, maxBar)
    val pct = ((v - minBar) / (maxBar - minBar)).coerceIn(0f, 1f)

    // For oil pressure, danger is often LOW pressure, so invert severity:
    val pctDanger = 1f - pct
    val colorNow = severityColor(pctDanger)

    val startAngle = 200f
    val sweepTotal = 220f
    val sweep = sweepTotal * pct

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(modifier = Modifier.size(480.dp)) {
            val stroke = 24.dp.toPx()
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2.20f
            val rect = Rect(center = center, radius = radius)

            // Track
            drawArc(
                color = DiagPalette.Stroke,
                startAngle = startAngle,
                sweepAngle = sweepTotal,
                useCenter = false,
                topLeft = Offset(rect.left, rect.top),
                size = rect.size,
                style = Stroke(stroke, cap = StrokeCap.Round)
            )

            // Progress (use blue for "good pressure"; shift to warn/red only when very low)
            // We'll still draw progress in AccentBlue, and use the center color to indicate danger.
            drawArc(
                color = DiagPalette.AccentBlue,
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = Offset(rect.left, rect.top),
                size = rect.size,
                style = Stroke(stroke, cap = StrokeCap.Round)
            )

            // Pin
            val a = degToRad(startAngle + sweep)
            val pinLen = radius - stroke * 0.35f
            val end = Offset(
                x = center.x + pinLen * cos(a),
                y = center.y + pinLen * sin(a)
            )
            drawLine(
                color = Color.Black.copy(alpha = 0.35f),
                start = center + Offset(2.dp.toPx(), 2.dp.toPx()),
                end = end + Offset(2.dp.toPx(), 2.dp.toPx()),
                strokeWidth = 6.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White,
                start = center,
                end = end,
                strokeWidth = 5.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Hub indicates danger for low pressure
            drawCircle(color = colorNow, radius = 18.dp.toPx(), center = center)
            drawCircle(color = Color.White, radius = 8.dp.toPx(), center = center)
        }

        Spacer(Modifier.height(8.dp))
        Text(label, color = DiagPalette.TextSecondary, letterSpacing = 2.sp, fontSize = 12.sp)
        Text(String.format("%.1f", v), color = DiagPalette.TextPrimary, fontSize = 52.sp, fontWeight = FontWeight.SemiBold)
        Text("BAR", color = DiagPalette.TextSecondary, fontSize = 14.sp)
        Text(
            when {
                pctDanger < 0.65f -> "NORMAL"
                pctDanger < 0.85f -> "LOW"
                else -> "CRITICAL LOW"
            },
            color = severityColor(pctDanger),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}