package com.example.diagassistant.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diagassistant.ui.viewModel.LiveViewModel
import com.example.diagassistant.ui.components.GaugeItem
import com.example.diagassistant.ui.components.GaugePanel
import com.example.diagassistant.ui.components.GaugeSidebar
import com.example.diagassistant.ui.theme.DiagPalette

@Composable
fun LiveScreen(vm: LiveViewModel = viewModel()) {
    var selectedGauge by rememberSaveable { mutableStateOf(GaugeItem.RPM) }
    val ui by vm.ui.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxSize()
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp, end = 10.dp, top = 6.dp, bottom = 1.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            GaugePanel(
                selected = selectedGauge,
                rpm = ui.rpm,
                temperatureC = ui.temperatureC,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
