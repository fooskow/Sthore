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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.catalogapp.downloader.CatalogDownloader
import com.example.catalogapp.downloader.CatalogSection

private val Cream = Color(0xFFFFF3D6)
private val CreamSurface = Color(0xFFFFE8BE)
private val Orange = Color(0xFFFF9F2A)
private val OrangeDeep = Color(0xFFE27C10)
private val Brown = Color(0xFF7A4212)
private val Ink = Color(0xFF2B1A0B)
private val SoftShadow = Color(0x33A85A00)
private val Panel = Color(0xFFFFF9EC)
private val PanelMuted = Color(0xFFFFE2AA)

private enum class ScreenTab(val title: String) {
    Catalog("Catalog"),
    Settings("Settings")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = Cream) {
                    SthoreShell()
                }
            }
        }
    }
}

@Composable
private fun SthoreShell() {
    val downloader = remember { CatalogDownloader() }
    val catalogSections = remember { downloader.loadCatalog() }
    var selectedTab by remember { mutableStateOf(ScreenTab.Catalog) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            HeaderCard()
            TabBar(
                selectedTab = selectedTab,
                onSelect = { selectedTab = it }
            )
            when (selectedTab) {
                ScreenTab.Catalog -> CatalogScreen(catalogSections)
                ScreenTab.Settings -> SettingsScreen()
            }
        }
    }
}

@Composable
private fun HeaderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = CreamSurface,
        elevation = 8.dp,
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Sthore",
                color = Ink,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "3DS eShop skeleton in an orange / cream shell",
                color = Brown,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                backgroundColor = Orange,
                elevation = 0.dp,
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Touch-friendly cards, rounded panels, and a handheld-console layout.",
                    modifier = Modifier.padding(14.dp),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun TabBar(selectedTab: ScreenTab, onSelect: (ScreenTab) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        ScreenTab.values().forEach { tab ->
            val isSelected = selectedTab == tab
            Button(
                onClick = { onSelect(tab) },
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (isSelected) OrangeDeep else PanelMuted,
                    contentColor = if (isSelected) Color.White else Ink
                ),
                elevation = ButtonDefaults.elevation(defaultElevation = 4.dp, pressedElevation = 0.dp)
            ) {
                Text(text = tab.title, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CatalogScreen(sections: List<CatalogSection>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        sections.forEach { section ->
            SectionCard(section)
        }
    }
}

@Composable
private fun SectionCard(section: CatalogSection) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Panel,
        elevation = 6.dp,
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = section.title,
                color = Ink,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            section.items.forEach { item ->
                CatalogTile(
                    title = item.title,
                    subtitle = item.subtitle,
                    tag = item.tag
                )
            }
        }
    }
}

@Composable
private fun CatalogTile(title: String, subtitle: String, tag: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SoftShadow, RoundedCornerShape(22.dp)),
        backgroundColor = Color(0xFFFFFBF2),
        elevation = 0.dp,
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = title, color = OrangeDeep, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                Surface(color = Orange, shape = RoundedCornerShape(22.dp), elevation = 0.dp) {
                    Text(
                        text = tag,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            Text(text = subtitle, color = Brown, fontSize = 13.sp)
        }
    }
}

@Composable
private fun SettingsScreen() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Panel,
        elevation = 6.dp,
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Settings",
                color = Ink,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            SettingRow(
                label = "Handheld mode",
                value = "Landscape only"
            )
            SettingRow(
                label = "Palette",
                value = "Orange / cream"
            )
            SettingRow(
                label = "Sections",
                value = "Catalog + Settings"
            )
        }
    }
}

@Composable
private fun SettingRow(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0xFFFFF8EA),
        elevation = 0.dp,
        shape = RoundedCornerShape(22.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, color = Ink, fontWeight = FontWeight.SemiBold)
            Text(text = value, color = OrangeDeep, fontWeight = FontWeight.Bold)
        }
    }
}
