package com.uvg.uvgestor

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.uvg.uvgestor.ui.theme.UVGestorTheme
import com.uvg.uvgestor.navigation.NavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UVGestorTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
                }
            }
        }
    }