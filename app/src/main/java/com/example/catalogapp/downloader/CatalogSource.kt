package com.example.catalogapp.downloader

data class CatalogItem(
    val title: String,
    val subtitle: String,
    val tag: String
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
                    title = "Nintendo classics",
                    subtitle = "Highlighted games and launch-ready cards for the storefront.",
                    tag = "TOP"
                ),
                CatalogItem(
                    title = "Daily picks",
                    subtitle = "Curated rows for the home screen and future live data.",
                    tag = "NEW"
                )
            )
        ),
        CatalogSection(
            title = "Browse",
            items = listOf(
                CatalogItem(
                    title = "Demo shelf",
                    subtitle = "Simple placeholder content for repository wiring.",
                    tag = "UI"
                ),
                CatalogItem(
                    title = "Download queue",
                    subtitle = "Ready for backend data, fetchers, and caching logic.",
                    tag = "DATA"
                )
            )
        )
    )
}
