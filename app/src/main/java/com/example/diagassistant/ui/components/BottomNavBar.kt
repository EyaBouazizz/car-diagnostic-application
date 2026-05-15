package com.example.diagassistant.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ManageSearch
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.diagassistant.ui.theme.DiagPalette
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.Assessment

enum class NavItem(val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    DASHBOARD(Icons.Outlined.Speed),
    DTC(Icons.AutoMirrored.Outlined.ManageSearch),
    REPORT(Icons.Outlined.Assessment),
    ASSIST(Icons.Outlined.SmartToy),
}

@Composable
fun BottomNavBar(
    selected: NavItem,
    onSelect: (NavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(0.56f)
            .height(96.dp),
        shape = RoundedCornerShape(30.dp),
        color = DiagPalette.Surface2,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NavItem.values().forEach { item ->
                val isSelected = item == selected

                val tileColor = if (isSelected) DiagPalette.SidebarSelected else Color.Transparent
                val border = if (isSelected) BorderStroke(1.5.dp, DiagPalette.AccentBlue) else null
                val iconTint = if (isSelected) DiagPalette.AccentBlue else DiagPalette.TextSecondary

                Surface(
                    modifier = Modifier.size(width = 72.dp, height = 68.dp)
                        .noIndicationClickable { onSelect(item) },
                    shape = RoundedCornerShape(18.dp),
                    color = tileColor,
                    border = border
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.name,
                            tint = iconTint,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}