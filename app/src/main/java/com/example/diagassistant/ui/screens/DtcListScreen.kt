package com.example.diagassistant.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.diagassistant.dtc.DtcCode
import com.example.diagassistant.dtc.DtcSeverity
import com.example.diagassistant.dtc.FakeDtcRepository
import com.example.diagassistant.ui.theme.DiagPalette
import com.example.diagassistant.ui.components.noIndicationClickable
import kotlinx.coroutines.launch

@Composable
fun DtcListScreen() {
    val scope = rememberCoroutineScope()

    var scanning by remember { mutableStateOf(false) }
    var hasScanRun by remember { mutableStateOf(false) }
    var lastScanText by remember { mutableStateOf("Never") }
    var dtcs by remember { mutableStateOf<List<DtcCode>>(emptyList()) }

    var selectedDtc by remember { mutableStateOf<DtcCode?>(null) }

    fun runScan() {
        scanning = true
        hasScanRun = true
        scope.launch {
            dtcs = FakeDtcRepository.runScan()
                .sortedByDescending { severityRank(it.severity) }
            scanning = false
            lastScanText = "Just now"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DiagPalette.Bg)
            .padding(16.dp)
    ) {
        if (!hasScanRun) {
            // BIG CENTER START BUTTON (initial state)
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Diagnostics",
                    color = DiagPalette.TextPrimary,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Run a scan to view active DTC codes (demo data).",
                    color = DiagPalette.TextSecondary,
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )

                Spacer(Modifier.height(26.dp))

                Button(
                    onClick = { runScan() },
                    enabled = !scanning,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DiagPalette.AccentBlueStrong,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .height(74.dp)
                        .widthIn(min = 380.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PlayArrow,
                        contentDescription = "Start",
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = if (scanning) "Scanning..." else "Start Diagnostic",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (scanning) {
                    Spacer(Modifier.height(16.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(30.dp),
                        strokeWidth = 3.dp,
                        color = DiagPalette.AccentBlue
                    )
                }
            }
        } else {
            // TOP HEADER + RESCAN BUTTON + LIST
            Column(modifier = Modifier.fillMaxSize()) {
                DtcHeaderRow(
                    activeCount = dtcs.size,
                    lastScan = lastScanText,
                    scanning = scanning,
                    onRescan = { runScan() }
                )

                Spacer(Modifier.height(16.dp))

                if (!scanning && dtcs.isEmpty()) {
                    EmptyStateCard(
                        title = "No active DTC codes",
                        subtitle = "Scan completed. No issues detected."
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(bottom = 18.dp)
                    ) {
                        items(dtcs) { dtc ->
                            DtcCard(
                                dtc = dtc,
                                onViewDetails = { selectedDtc = dtc }
                            )
                        }
                    }
                }
            }
        }
    }

    if (selectedDtc != null) {
        BigDtcDetailsDialog(
            dtc = selectedDtc!!,
            onDismiss = { selectedDtc = null }
        )
    }
}

@Composable
private fun DtcHeaderRow(
    activeCount: Int,
    lastScan: String,
    scanning: Boolean,
    onRescan: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            Text(
                text = "ACTIVE DTC CODES ($activeCount)",
                color = DiagPalette.TextPrimary,
                fontSize = 16.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "LAST SCAN: $lastScan",
                color = DiagPalette.TextSecondary,
                fontSize = 13.sp
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (scanning) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 3.dp,
                    color = DiagPalette.AccentBlue
                )
                Spacer(Modifier.width(12.dp))
            }

            OutlinedButton(
                onClick = onRescan,
                enabled = !scanning,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, DiagPalette.Stroke),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DiagPalette.AccentBlue
                ),
                modifier = Modifier.height(54.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = "Rescan",
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    if (scanning) "Scanning" else "Rescan",
                    letterSpacing = 1.sp,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun EmptyStateCard(title: String, subtitle: String) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = DiagPalette.Surface1,
        border = BorderStroke(1.dp, DiagPalette.Stroke),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, color = DiagPalette.TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
            Spacer(Modifier.height(8.dp))
            Text(subtitle, color = DiagPalette.TextSecondary, fontSize = 16.sp, lineHeight = 22.sp)
        }
    }
}

@Composable
private fun DtcCard(
    dtc: DtcCode,
    onViewDetails: () -> Unit
) {
    val accent = severityAccent(dtc.severity)
    val chipText = when (dtc.severity) {
        DtcSeverity.CRITICAL -> "CRITICAL"
        DtcSeverity.ADVISORY -> "ADVISORY"
        DtcSeverity.INFO -> "INFO"
    }

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = DiagPalette.Surface1,
        border = BorderStroke(1.dp, DiagPalette.Stroke),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.Top
            ) {
                // code badge (bigger)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = accent.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, accent.copy(alpha = 0.35f)),
                    modifier = Modifier.size(width = 92.dp, height = 82.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            dtc.code,
                            color = accent,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 22.sp
                        )
                    }
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            dtc.title,
                            color = DiagPalette.TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )

                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = accent.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, accent.copy(alpha = 0.35f))
                        ) {
                            Text(
                                chipText,
                                color = accent,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        dtc.description,
                        color = DiagPalette.TextSecondary,
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    )
                }

                Spacer(Modifier.width(14.dp))

                OutlinedButton(
                    onClick = onViewDetails,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, DiagPalette.Stroke),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = DiagPalette.AccentBlue
                    ),
                    modifier = Modifier.height(52.dp)
                ) {
                    Text(
                        "VIEW DETAILS",
                        letterSpacing = 1.sp,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Divider(color = DiagPalette.Stroke)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                MetaLine("SYSTEM:", dtc.system, accent)
                MetaLine("FREQUENCY:", dtc.frequency, accent)
            }
        }
    }
}

@Composable
private fun MetaLine(k: String, v: String, accent: Color) {
    Row {
        Text(k, color = DiagPalette.TextSecondary, fontSize = 12.sp, letterSpacing = 1.sp)
        Spacer(Modifier.width(8.dp))
        Text(v, color = accent.copy(alpha = 0.95f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

/**
 * Big dialog with clear sections (no image, no AI section).
 */
@Composable
private fun BigDtcDetailsDialog(dtc: DtcCode, onDismiss: () -> Unit) {
    val accent = severityAccent(dtc.severity)
    val scroll = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .padding(22.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .fillMaxHeight(0.90f),
                shape = RoundedCornerShape(28.dp),
                color = DiagPalette.Surface1,
                border = BorderStroke(1.dp, DiagPalette.Stroke)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(22.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = accent.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, accent.copy(alpha = 0.35f)),
                                modifier = Modifier.size(86.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Outlined.WarningAmber,
                                        contentDescription = null,
                                        tint = accent,
                                        modifier = Modifier.size(34.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.width(18.dp))

                            Column {
                                Text(
                                    dtc.code,
                                    color = DiagPalette.TextPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 42.sp
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    dtc.title,
                                    color = DiagPalette.TextSecondary,
                                    fontSize = 20.sp,
                                    lineHeight = 26.sp
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = accent.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, accent.copy(alpha = 0.35f))
                        ) {
                            Text(
                                text = when (dtc.severity) {
                                    DtcSeverity.CRITICAL -> "CRITICAL SEVERITY"
                                    DtcSeverity.ADVISORY -> "ADVISORY SEVERITY"
                                    DtcSeverity.INFO -> "INFO SEVERITY"
                                },
                                color = accent,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Divider(color = DiagPalette.Stroke)
                    Spacer(Modifier.height(16.dp))

                    // Content sections (scrollable)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(scroll),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DetailsSection(
                            title = "SYSTEM EXPLANATION",
                            content = {
                                Text(
                                    dtc.description,
                                    color = DiagPalette.TextSecondary,
                                    fontSize = 16.sp,
                                    lineHeight = 22.sp
                                )
                            }
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            InfoPill(title = "SYSTEM", value = dtc.system, accent = accent, modifier = Modifier.weight(1f))
                            InfoPill(title = "FREQUENCY", value = dtc.frequency, accent = accent, modifier = Modifier.weight(1f))
                            InfoPill(title = "SEVERITY", value = dtc.severity.name, accent = accent, modifier = Modifier.weight(1f))
                        }

                        DetailsSection(
                            title = "PROBABLE CAUSES",
                            content = { BulletList(dtc.possibleCauses) }
                        )

                        DetailsSection(
                            title = "SYMPTOMS",
                            content = { BulletList(dtc.symptoms) }
                        )

                        DetailsSection(
                            title = "RECOMMENDED ACTIONS",
                            content = { BulletList(dtc.recommendedActions) }
                        )

                        DetailsSection(
                            title = "FREEZE FRAME",
                            content = {
                                dtc.freezeFrame.forEach { (k, v) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(k, color = DiagPalette.TextSecondary, fontSize = 15.sp)
                                        Text(v, color = DiagPalette.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                                    }
                                    Spacer(Modifier.height(8.dp))
                                }
                            }
                        )

                        DetailsSection(
                            title = "NOTES",
                            content = {
                                Text(
                                    dtc.notes,
                                    color = DiagPalette.TextSecondary,
                                    fontSize = 16.sp,
                                    lineHeight = 22.sp
                                )
                            }
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    // Footer actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, DiagPalette.Stroke),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DiagPalette.AccentBlue),
                            modifier = Modifier.height(54.dp)
                        ) {
                            Text("Close", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            color = DiagPalette.AccentBlue,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 2.sp
        )
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = DiagPalette.Surface2,
            border = BorderStroke(1.dp, DiagPalette.Stroke),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun InfoPill(title: String, value: String, accent: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = DiagPalette.Surface2,
        border = BorderStroke(1.dp, DiagPalette.Stroke)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, color = DiagPalette.TextSecondary, fontSize = 12.sp, letterSpacing = 1.sp)
            Spacer(Modifier.height(6.dp))
            Text(value, color = accent, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun BulletList(items: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { line ->
            Text("• $line", color = DiagPalette.TextSecondary, fontSize = 16.sp, lineHeight = 22.sp)
        }
    }
}

private fun severityAccent(s: DtcSeverity): Color = when (s) {
    DtcSeverity.CRITICAL -> Color(0xFFFF4D4D)
    DtcSeverity.ADVISORY -> DiagPalette.Warn
    DtcSeverity.INFO -> DiagPalette.AccentBlue
}

private fun severityRank(s: DtcSeverity): Int = when (s) {
    DtcSeverity.CRITICAL -> 3
    DtcSeverity.ADVISORY -> 2
    DtcSeverity.INFO -> 1
}