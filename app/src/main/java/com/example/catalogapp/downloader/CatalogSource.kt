package com.example.catalogapp.downloader

data class CatalogItem(
    val title: String,
    val subtitle: String,
    val tag: String,
    val imageUrl: String? = null
)

data class CatalogSection(
    val title: String,
    val items: List<CatalogItem>
)

interface CatalogSource {
    fun loadCatalog(): List<CatalogSection>
}

object DefaultCatalogSource : CatalogSource {
    override fun loadCatalog(): List<CatalogSection> = listOf(
        CatalogSection(
            title = "Featured",
            items = listOf(
                CatalogItem(
                    title = "Sky Circuit",
                    subtitle = "Fast-paced racing with a bright storefront card.",
                    tag = "HOT"
                ),
                CatalogItem(
                    title = "Pocket Quest",
                    subtitle = "Adventure pick with a clean cover frame and download button.",
                    tag = "NEW"
                )
            )
        ),
        CatalogSection(
            title = "Popular",
            items = listOf(
                CatalogItem(
                    title = "Tiny Tower Defense",
                    subtitle = "Quick sessions, playful art, and easy browsing.",
                    tag = "TOP"
                ),
                CatalogItem(
                    title = "Retro Rally",
                    subtitle = "Classic handheld vibes for the grid layout.",
                    tag = "PLAY"
                )
            )
        )
    )
}
