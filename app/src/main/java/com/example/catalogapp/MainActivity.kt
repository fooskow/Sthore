package com.example.catalogapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(color = Color(0xFF0B0F1A)) {
                CatalogShell()
            }
        }
    }
}

@Composable
private fun CatalogShell() {
    val shellGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF1B2450), Color(0xFF101426), Color(0xFF090B12)),
        start = Offset.Zero,
        end = Offset(900f, 1600f)
    )
    var selectedTab by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(shellGradient)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HeaderCard()
            TabRow(selectedTab = selectedTab, onSelect = { selectedTab = it })
            MainCard(selectedTab = selectedTab)
            Spacer(modifier = Modifier.weight(1f))
            FooterStrip()
        }
    }
}

@Composable
private fun HeaderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0xFF151C33),
        elevation = 10.dp,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "eSthore",
                color = Color(0xFFF6F1FF),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "A 3DS-style Android app skeleton",
                color = Color(0xFFB8C2FF),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun TabRow(selectedTab: Int, onSelect: (Int) -> Unit) {
    val tabs = listOf("Library", "Catalog", "Settings")
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        tabs.forEachIndexed { index, title ->
            Button(
                onClick = { onSelect(index) },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (selectedTab == index) Color(0xFFFF4FD8) else Color(0xFF26314F)
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(text = title, color = Color.White)
            }
        }
    }
}

@Composable
private fun MainCard(selectedTab: Int) {
    val title = when (selectedTab) {
        0 -> "Featured titles"
        1 -> "Browse the catalog"
        else -> "System options"
    }

    val body = when (selectedTab) {
        0 -> "Launch your favorite items, continue progress, and keep the UI focused on a handheld-console feel."
        1 -> "This skeleton is ready for product grids, detail pages, and future repository-backed content."
        else -> "Add preferences, save data, and account screens here when the app grows beyond the starter shell."
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color(0xFF8B7CFF), RoundedCornerShape(28.dp))
            .background(Color(0xFF11182B), RoundedCornerShape(28.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = title, color = Color(0xFFFFE66D), fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        Text(text = body, color = Color(0xFFE5E9FF), fontSize = 15.sp)

        Card(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0xFF1D2745),
            elevation = 6.dp,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CatalogRow(primary = "Storefront", secondary = "3 sections ready to wire")
                CatalogRow(primary = "Handheld layout", secondary = "Top screen + touch-friendly cards")
                CatalogRow(primary = "Build pipeline", secondary = "GitHub Actions debug APK artifact")
            }
        }
    }
}

@Composable
private fun CatalogRow(primary: String, secondary: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = primary, color = Color(0xFFF7F8FF), fontWeight = FontWeight.Medium)
        Text(text = secondary, color = Color(0xFFB5BEDD), fontSize = 13.sp)
    }
}

@Composable
private fun FooterStrip() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF151A2A),
        shape = RoundedCornerShape(18.dp),
        elevation = 4.dp
    ) {
        Text(
            text = "Ready for feature screens, lists, and game-card layouts.",
            modifier = Modifier.padding(14.dp),
            color = Color(0xFF9FB0FF),
            fontSize = 13.sp
        )
    }
}
