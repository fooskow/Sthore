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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

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
    Catalog("Catalog"), Settings("Settings")
}

data class CatalogSection(
    val title: String,
    val items: List<CatalogItem>
)

data class CatalogItem(
    val title: String,
    val subtitle: String,
    val tag: String,
    val imageUrl: String?
)

private val demoSections = listOf(
    CatalogSection(
        title = "Featured",
        items = listOf(
            CatalogItem(
                title = "Retro Runner",
                subtitle = "Fast-paced platforming with bold colors and crisp art.",
                tag = "Hot",
                imageUrl = "https://images.unsplash.com/photo-1511512578047-dfb367046420?auto=format&fit=crop&w=900&q=80"
            ),
            CatalogItem(
                title = "Sky Grid",
                subtitle = "Puzzle strategy with a clean handheld dashboard feel.",
                tag = "New",
                imageUrl = "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=900&q=80"
            )
        )
    ),
    CatalogSection(
        title = "Deals",
        items = listOf(
            CatalogItem(
                title = "Pocket Quest",
                subtitle = "Adventure game card with room for ratings and price.",
                tag = "-20%",
                imageUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?auto=format&fit=crop&w=900&q=80"
            ),
            CatalogItem(
                title = "Color Burst",
                subtitle = "Bright, arcade-inspired storefront tile for the library.",
                tag = "Sale",
                imageUrl = "https://images.unsplash.com/photo-1493711662062-fa541adb3fc8?auto=format&fit=crop&w=900&q=80"
            )
        )
    )
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ESthoreApp() {
    var selectedTab by remember { mutableStateOf(ScreenTab.Catalog) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var statusMessage by rememberSaveable { mutableStateOf("Browse the eShop-style catalog") }
    var downloadedTitles by remember { mutableStateOf(setOf<String>()) }

    val filteredSections = remember(searchQuery) {
        val normalized = searchQuery.trim().lowercase()
        if (normalized.isBlank()) {
            demoSections
        } else {
            demoSections.map { section ->
                section.copy(
                    items = section.items.filter {
                        it.title.lowercase().contains(normalized) ||
                            it.subtitle.lowercase().contains(normalized) ||
                            it.tag.lowercase().contains(normalized)
                    }
                )
            }.filter { it.items.isNotEmpty() }
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
                actions = {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = null,
                        tint = Brown
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CreamSurface,
                    titleContentColor = Ink
                )
            )
        },
        containerColor = Cream
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HeroCard()
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )
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
                            sections = filteredSections,
                            isLoading = isLoading,
                            downloadedTitles = downloadedTitles,
                            onDownload = { item ->
                                downloadedTitles = downloadedTitles + item.title
                                statusMessage = "Queued " + item.title + " for download"
                            }
                        )
                    }
                }
                ScreenTab.Settings -> {
                    SettingsScreen(
                        statusMessage = statusMessage,
                        onReload = {
                            isLoading = true
                            statusMessage = "Refreshing catalog"
                            isLoading = false
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
        colors = CardDefaults.cardColors(containerColor = CreamSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
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
                colors = CardDefaults.cardColors(containerColor = Orange),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Two-column catalog cards, cover art, and a settings panel that actually saves.",
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
private fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = null)
        },
        label = { Text("Search catalog") },
        singleLine = true,
        shape = RoundedCornerShape(18.dp)
    )
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
                    containerColor = if (isSelected) OrangeDeep else PanelMuted,
                    contentColor = if (isSelected) Color.White else Ink
                )
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
        colors = CardDefaults.cardColors(containerColor = Panel),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBF2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
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
                    model = item.imageUrl ?: android.R.drawable.sym_def_app_icon,
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = Orange),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = item.tag,
                        color = Color.White,
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
                colors = ButtonDefaults.buttonColors(containerColor = OrangeDeep)
            ) {
                Icon(
                    imageVector = Icons.Filled.Download,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 6.dp)
                )
                Text(
                    text = if (downloaded) "Downloaded" else "Download",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    statusMessage: String,
    onReload: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Panel),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
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
                text = "This starter keeps the UI local and ready for catalog feeds, preferences, and downloads.",
                color = Brown,
                fontSize = 13.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        onReload()
                    },
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeDeep)
                ) {
                    Text(text = "Refresh", color = Color.White)
                }
            }
            Text(
                text = statusMessage,
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
        colors = CardDefaults.cardColors(containerColor = PanelMuted),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
