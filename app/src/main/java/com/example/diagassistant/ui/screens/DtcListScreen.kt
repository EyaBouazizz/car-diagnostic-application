package com.example.diagassistant.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diagassistant.dtc.DtcCode
import com.example.diagassistant.dtc.DtcSeverity
import com.example.diagassistant.dtc.FakeDtcRepository
import com.example.diagassistant.ui.theme.DiagPalette
import kotlinx.coroutines.launch

@Composable
fun DtcListScreen() {
    val scope = rememberCoroutineScope()

    var scanning by remember { mutableStateOf(false) }
    var hasScanRun by remember { mutableStateOf(false) }
    var lastScanText by remember { mutableStateOf("Never") }
    var dtcs by remember { mutableStateOf<List<DtcCode>>(emptyList()) }

    var selectedDtc by remember { mutableStateOf<DtcCode?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        HeaderRow(
            activeCount = dtcs.size,
            lastScan = lastScanText
        )

        Spacer(Modifier.height(14.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = {
                    scanning = true
                    hasScanRun = true
                    scope.launch {
                        dtcs = FakeDtcRepository.runScan()
                            .sortedByDescending { severityRank(it.severity) }
                        scanning = false
                        lastScanText = "Just now"
                    }
                },
                enabled = !scanning,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DiagPalette.AccentBlueStrong,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.height(52.dp)
            ) {
                Text(if (scanning) "Scanning..." else "Start Diagnostic", fontSize = 16.sp)
            }

            Spacer(Modifier.width(12.dp))

            if (scanning) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 3.dp,
                    color = DiagPalette.AccentBlue
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        if (!hasScanRun) {
            EmptyStateCard(
                title = "No scan yet",
                subtitle = "Press Start Diagnostic to generate a fake DTC list."
            )
        } else if (!scanning && dtcs.isEmpty()) {
            EmptyStateCard(
                title = "No active DTC codes",
                subtitle = "Scan completed. No issues detected."
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
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

    if (selectedDtc != null) {
        DtcDetailsDialog(
            dtc = selectedDtc!!,
            onDismiss = { selectedDtc = null }
        )
    }
}

@Composable
private fun HeaderRow(activeCount: Int, lastScan: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = "ACTIVE DTC CODES ($activeCount)",
            color = DiagPalette.TextPrimary,
            fontSize = 14.sp,
            letterSpacing = 3.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "LAST SCAN: $lastScan",
            color = DiagPalette.TextSecondary,
            fontSize = 12.sp
        )
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
        Column(modifier = Modifier.padding(18.dp)) {
            Text(title, color = DiagPalette.TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            Spacer(Modifier.height(6.dp))
            Text(subtitle, color = DiagPalette.TextSecondary, fontSize = 14.sp)
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
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                // code badge
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = accent.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, accent.copy(alpha = 0.35f)),
                    modifier = Modifier.size(width = 72.dp, height = 64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            dtc.code,
                            color = accent,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            dtc.title,
                            color = DiagPalette.TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = accent.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, accent.copy(alpha = 0.35f))
                        ) {
                            Text(
                                chipText,
                                color = accent,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    Text(
                        dtc.description,
                        color = DiagPalette.TextSecondary,
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    )
                }

                Spacer(Modifier.width(12.dp))

                OutlinedButton(
                    onClick = onViewDetails,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, DiagPalette.Stroke),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = DiagPalette.AccentBlue
                    ),
                    modifier = Modifier.height(44.dp)
                ) {
                    Text("VIEW DETAILS", letterSpacing = 1.sp)
                }
            }

            Divider(color = DiagPalette.Stroke)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
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
        Text(k, color = DiagPalette.TextSecondary, fontSize = 11.sp, letterSpacing = 1.sp)
        Spacer(Modifier.width(6.dp))
        Text(v, color = accent.copy(alpha = 0.95f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun DtcDetailsDialog(dtc: DtcCode, onDismiss: () -> Unit) {
    val accent = severityAccent(dtc.severity)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = DiagPalette.AccentBlue) }
        },
        title = {
            Column {
                Text("${dtc.code} • ${dtc.title}", color = DiagPalette.TextPrimary, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Severity: ${dtc.severity.name}",
                    color = accent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(dtc.description, color = DiagPalette.TextSecondary, lineHeight = 18.sp)

                Section("Possible causes", dtc.possibleCauses)
                Section("Symptoms", dtc.symptoms)
                Section("Recommended actions", dtc.recommendedActions)

                Text("Freeze frame", color = DiagPalette.TextPrimary, fontWeight = FontWeight.SemiBold)
                dtc.freezeFrame.forEach { (k, v) ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(k, color = DiagPalette.TextSecondary, fontSize = 13.sp)
                        Text(v, color = DiagPalette.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(Modifier.height(6.dp))
                Text("Notes", color = DiagPalette.TextPrimary, fontWeight = FontWeight.SemiBold)
                Text(dtc.notes, color = DiagPalette.TextSecondary, lineHeight = 18.sp)
            }
        },
        containerColor = DiagPalette.Surface1,
        titleContentColor = DiagPalette.TextPrimary,
        textContentColor = DiagPalette.TextPrimary
    )
}

@Composable
private fun Section(title: String, items: List<String>) {
    Text(title, color = DiagPalette.TextPrimary, fontWeight = FontWeight.SemiBold)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        items.forEach { line ->
            Text("• $line", color = DiagPalette.TextSecondary, fontSize = 13.sp, lineHeight = 17.sp)
        }
    }
}

private fun severityAccent(s: DtcSeverity): Color = when (s) {
    DtcSeverity.CRITICAL -> Color(0xFFFF4D4D) // red
    DtcSeverity.ADVISORY -> DiagPalette.Warn  // orange/yellow
    DtcSeverity.INFO -> DiagPalette.AccentBlue // blue
}

private fun severityRank(s: DtcSeverity): Int = when (s) {
    DtcSeverity.CRITICAL -> 3
    DtcSeverity.ADVISORY -> 2
    DtcSeverity.INFO -> 1
}