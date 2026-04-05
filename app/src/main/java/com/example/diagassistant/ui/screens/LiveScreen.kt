package com.example.diagassistant.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diagassistant.ui.components.GaugeItem
import com.example.diagassistant.ui.components.GaugePanel
import com.example.diagassistant.ui.components.GaugeSidebar
import com.example.diagassistant.ui.components.SimpleBottomNavBar
import com.example.diagassistant.ui.theme.DiagPalette

@Composable
fun LiveScreen() {
    var selectedGauge by remember { mutableStateOf(GaugeItem.RPM) }
    var selectedNav by remember { mutableStateOf(1) }

    Scaffold(containerColor = DiagPalette.Bg) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(DiagPalette.Bg)
        ) {
            GaugeSidebar(
                selected = selectedGauge,
                onSelect = { selectedGauge = it },
                modifier = Modifier
                    .width(150.dp)
                    .fillMaxHeight()
                    .padding(start = 12.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        // CHANGE: slightly bigger than before (more space for gauge)
                        .weight(1.15f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    GaugePanel(
                        selected = selectedGauge,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }

                // CHANGE: smaller spacer
                Spacer(modifier = Modifier.height(8.dp))

                // CHANGE: simple bar centered under the gauge area
                SimpleBottomNavBar(
                    selectedIndex = selectedNav,
                    onSelect = { selectedNav = it }
                )
            }
        }
    }
}