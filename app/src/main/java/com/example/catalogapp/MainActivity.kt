package com.example.catalogapp

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.catalogapp.downloader.CatalogDownloader
import com.example.catalogapp.downloader.CatalogItem
import com.example.catalogapp.downloader.CatalogSection

private val Cream = androidx.compose.ui.graphics.Color(0xFFFFF3D6)
private val CreamSurface = androidx.compose.ui.graphics.Color(0xFFFFE8BE)
private val Orange = androidx.compose.ui.graphics.Color(0xFFFF9F2A)
private val OrangeDeep = androidx.compose.ui.graphics.Color(0xFFE27C10)
private val Brown = androidx.compose.ui.graphics.Color(0xFF7A4212)
private val Ink = androidx.compose.ui.graphics.Color(0xFF2B1A0B)
private val SoftShadow = androidx.compose.ui.graphics.Color(0x33A85A00)
private val Panel = androidx.compose.ui.graphics.Color(0xFFFFF9EC)
private val PanelMuted = androidx.compose.ui.graphics.Color(0xFFFFE2AA)

private enum class ScreenTab(val title: String) {
    Catalog("Catalog"),
    Settings("Settings")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = Cream) {
                    ESthoreApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ESthoreApp() {
    val downloader = remember { CatalogDownloader() }
    var selectedTab by remember { mutableStateOf(ScreenTab.Catalog) }
    var activeCatalogUrl by rememberSaveable { mutableStateOf("") }
    var draftCatalogUrl by rememberSaveable { mutableStateOf("") }
    var catalogSections by remember { mutableStateOf<List<CatalogSection>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var statusMessage by rememberSaveable { mutableStateOf("Using the built-in catalog") }
    var downloadedTitles by remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(activeCatalogUrl) {
        isLoading = true
        catalogSections = downloader.loadCatalog(activeCatalogUrl)
        statusMessage = if (activeCatalogUrl.isBlank()) {
            "Showing the built-in catalog"
        } else {
            "Loaded catalog from $activeCatalogUrl"
        }
        isLoading = false
    }

    LaunchedEffect(selectedTab, activeCatalogUrl) {
        if (selectedTab == ScreenTab.Settings) {
            draftCatalogUrl = activeCatalogUrl
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "eSthore", fontWeight = FontWeight.ExtraBold)
                        Text(
                            text = "3DS-style storefront",
                            fontSize = 12.sp,
                            color = Brown
                        )
                    }
                },
                backgroundColor = CreamSurface,
                contentColor = Ink,
                elevation = 8.dp
            )
        },
        backgroundColor = Cream
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HeroCard()
            TabBar(
                selectedTab = selectedTab,
                onSelect = { selectedTab = it }
            )
            when (selectedTab) {
                ScreenTab.Catalog -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        CatalogGridScreen(
                            sections = catalogSections,
                            isLoading = isLoading,
                            downloadedTitles = downloadedTitles,
                            onDownload = { item ->
                                downloadedTitles = downloadedTitles + item.title
                                statusMessage = "Queued ${item.title} for download"
                            }
                        )
                    }
                }
                ScreenTab.Settings -> {
                    SettingsScreen(
                        currentCatalogUrl = activeCatalogUrl,
                        draftCatalogUrl = draftCatalogUrl,
                        onCatalogUrlChange = { draftCatalogUrl = it },
                        onSave = {
                            val normalized = draftCatalogUrl.trim()
                            draftCatalogUrl = normalized
                            activeCatalogUrl = normalized
                        }
                    )
                }
            }
            StatusCard(message = statusMessage)
        }
    }
}

@Composable
private fun HeroCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = CreamSurface,
        elevation = 8.dp,
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "eSthore",
                color = Ink,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "A storefront made for handheld browsing",
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
                    text = "Two-column catalog cards, cover art, and a settings panel that actually saves.",
                    modifier = Modifier.padding(14.dp),
                    color = androidx.compose.ui.graphics.Color.White,
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
                    contentColor = if (isSelected) androidx.compose.ui.graphics.Color.White else Ink
                ),
                elevation = ButtonDefaults.elevation(defaultElevation = 4.dp, pressedElevation = 0.dp)
            ) {
                Text(text = tab.title, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CatalogGridScreen(
    sections: List<CatalogSection>,
    isLoading: Boolean,
    downloadedTitles: Set<String>,
    onDownload: (CatalogItem) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = Panel,
        elevation = 6.dp,
        shape = RoundedCornerShape(22.dp)
    ) {
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = OrangeDeep)
                }
            }
            sections.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No catalog items found.",
                        color = Brown,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    sections.forEach { section ->
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            SectionHeader(title = section.title)
                        }
                        items(section.items, key = { item -> item.title }) { item ->
                            CatalogCard(
                                item = item,
                                downloaded = item.title in downloadedTitles,
                                onDownload = { onDownload(item) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = Ink,
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
    )
}

@Composable
private fun CatalogCard(
    item: CatalogItem,
    downloaded: Boolean,
    onDownload: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SoftShadow, RoundedCornerShape(22.dp)),
        backgroundColor = androidx.compose.ui.graphics.Color(0xFFFFFBF2),
        elevation = 0.dp,
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(126.dp)
                    .background(PanelMuted, RoundedCornerShape(18.dp))
            ) {
                AsyncImage(
                    model = item.imageUrl?.takeIf { it.isNotBlank() } ?: android.R.drawable.sym_def_app_icon,
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    color = Orange,
                    shape = RoundedCornerShape(16.dp),
                    elevation = 0.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = item.tag,
                        color = androidx.compose.ui.graphics.Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = item.title,
                    color = OrangeDeep,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    maxLines = 2
                )
                Text(
                    text = item.subtitle,
                    color = Brown,
                    fontSize = 12.sp,
                    maxLines = 3
                )
            }

            Button(
                onClick = onDownload,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = OrangeDeep)
            ) {
                Text(
                    text = if (downloaded) "Downloaded" else "Download",
                    color = androidx.compose.ui.graphics.Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    currentCatalogUrl: String,
    draftCatalogUrl: String,
    onCatalogUrlChange: (String) -> Unit,
    onSave: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

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
            Text(
                text = "Point the catalog at a JSON feed and save it to refresh the store.",
                color = Brown,
                fontSize = 13.sp
            )
            OutlinedTextField(
                value = draftCatalogUrl,
                onValueChange = onCatalogUrlChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                label = { Text("Catalog URL") },
                singleLine = true
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        onSave()
                    },
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = OrangeDeep)
                ) {
                    Text(text = "Save", color = androidx.compose.ui.graphics.Color.White)
                }
            }
            Text(
                text = if (currentCatalogUrl.isBlank()) {
                    "Current source: built-in catalog"
                } else {
                    "Current source: $currentCatalogUrl"
                },
                color = Brown,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun StatusCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = PanelMuted,
        elevation = 2.dp,
        shape = RoundedCornerShape(18.dp)
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            color = Ink,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
