package com.example.diagassistant.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diagassistant.ui.theme.DiagPalette
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.TireRepair
import androidx.compose.material.icons.outlined.OilBarrel

// Keep the simple labels like your first sidebar version
enum class GaugeItem(val label: String, val icon: ImageVector) {
    RPM("RPM", Icons.Outlined.Speed),
    TEMP("TEMP", Icons.Outlined.Thermostat),
    COOLANT("COOLANT", Icons.Outlined.WaterDrop),
    OIL("OIL", Icons.Outlined.OilBarrel),
    ABS("ABS", Icons.Outlined.WaterDrop),
    PRESSURE("PRESSURE", Icons.Outlined.TireRepair),
    FUEL("FUEL", Icons.Outlined.LocalGasStation)
}

@Composable
fun GaugeSidebar(
    selected: GaugeItem,
    onSelect: (GaugeItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val scroll = rememberScrollState()

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = DiagPalette.Sidebar,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GaugeItem.values().forEach { item ->
                SidebarItem(
                    item = item,
                    selected = (item == selected),
                    onClick = { onSelect(item) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun SidebarItem(
    item: GaugeItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)

    val containerColor: Color = if (selected) DiagPalette.SidebarSelected else Color.Transparent
    val border: BorderStroke? = if (selected) BorderStroke(1.5.dp, DiagPalette.AccentBlue) else null
    val iconTint = if (selected) DiagPalette.AccentBlue else DiagPalette.TextSecondary
    val textTint = if (selected) DiagPalette.AccentBlue else DiagPalette.TextPrimary

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .noIndicationClickable(onClick = onClick),
        shape = shape,
        color = containerColor,
        border = border
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = iconTint,
                modifier = Modifier.size(34.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.label,
                color = textTint,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}