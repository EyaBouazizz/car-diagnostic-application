package com.example.diagassistant.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.diagassistant.ui.theme.DiagPalette

enum class NavItem(val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    HOME(Icons.Outlined.Description),
    LIVE(Icons.Outlined.Speed),
    ALERT(Icons.Outlined.NotificationsNone),
    ASSIST(Icons.Outlined.ChatBubbleOutline),
}

@Composable
fun SimpleBottomNavBar(
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(0.56f)   // slightly shorter
            .height(96.dp),        // thick bar
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
            NavItem.values().forEachIndexed { index, item ->
                val isSelected = index == selectedIndex

                val tileColor = if (isSelected) DiagPalette.SidebarSelected else Color.Transparent
                val border = if (isSelected) BorderStroke(1.5.dp, DiagPalette.AccentBlue) else null
                val iconTint = if (isSelected) DiagPalette.AccentBlue else DiagPalette.TextSecondary

                Surface(
                    modifier = Modifier
                        .size(width = 72.dp, height = 68.dp)
                        .noIndicationClickable { onSelect(index) },
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