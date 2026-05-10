package com.example.catalogapp.downloader

class CatalogDownloader(
    private val source: CatalogSource = DefaultCatalogSource
) {
    fun loadCatalog(): List<CatalogSection> = source.loadCatalog()
}
