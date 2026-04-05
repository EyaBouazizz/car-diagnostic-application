package com.example.diagassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.diagassistant.ui.navigation.AppNavGraph
import com.example.diagassistant.ui.theme.DiagTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiagTheme {
                AppNavGraph()
            }
        }
    }
}