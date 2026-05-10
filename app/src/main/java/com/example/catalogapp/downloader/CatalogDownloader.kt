package com.example.catalogapp.downloader

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class CatalogDownloader(
    private val source: CatalogSource = DefaultCatalogSource
) {
    suspend fun loadCatalog(catalogUrl: String? = null): List<CatalogSection> = withContext(Dispatchers.IO) {
        val normalizedUrl = catalogUrl?.trim().orEmpty()
        if (normalizedUrl.isBlank()) {
            return@withContext source.loadCatalog()
        }

        runCatching {
            fetchCatalog(normalizedUrl)
        }.getOrElse {
            source.loadCatalog()
        }
    }

    private fun fetchCatalog(urlValue: String): List<CatalogSection> {
        val connection = (URL(urlValue).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 5_000
            readTimeout = 5_000
            instanceFollowRedirects = true
        }

        return try {
            val payload = connection.inputStream.bufferedReader().use { it.readText() }
            parseCatalog(payload)
        } finally {
            connection.disconnect()
        }
    }

    private fun parseCatalog(payload: String): List<CatalogSection> {
        val trimmed = payload.trim()
        if (trimmed.isEmpty()) {
            return source.loadCatalog()
        }

        return try {
            when {
                trimmed.startsWith("[") -> listOf(
                    CatalogSection(
                        title = "Catalog",
                        items = parseItemArray(JSONArray(trimmed))
                    )
                )
                else -> parseRootObject(JSONObject(trimmed))
            }
        } catch (_: Exception) {
            source.loadCatalog()
        }
    }

    private fun parseRootObject(root: JSONObject): List<CatalogSection> {
        root.optJSONArray("sections")?.let { sectionsArray ->
            return parseSectionArray(sectionsArray)
        }

        root.optJSONArray("items")?.let { itemsArray ->
            return listOf(
                CatalogSection(
                    title = root.optString("title", root.optString("name", "Catalog")),
                    items = parseItemArray(itemsArray)
                )
            )
        }

        root.optJSONObject("catalog")?.let { nestedCatalog ->
            return parseRootObject(nestedCatalog)
        }

        return listOf(
            CatalogSection(
                title = root.optString("title", root.optString("name", "Catalog")),
                items = listOf(parseItem(root))
            )
        )
    }

    private fun parseSectionArray(array: JSONArray): List<CatalogSection> {
        val sections = mutableListOf<CatalogSection>()
        for (index in 0 until array.length()) {
            val sectionObject = array.optJSONObject(index) ?: continue
            sections += parseSection(sectionObject)
        }
        return if (sections.isEmpty()) source.loadCatalog() else sections
    }

    private fun parseSection(section: JSONObject): CatalogSection {
        val title = section.optString("title", section.optString("name", "Catalog"))
        val itemsArray = section.optJSONArray("items")
            ?: section.optJSONArray("games")
            ?: section.optJSONArray("entries")
            ?: section.optJSONArray("catalog")

        return CatalogSection(
            title = title,
            items = itemsArray?.let(::parseItemArray) ?: listOf(parseItem(section))
        )
    }

    private fun parseItemArray(array: JSONArray): List<CatalogItem> {
        val items = mutableListOf<CatalogItem>()
        for (index in 0 until array.length()) {
            val itemObject = array.optJSONObject(index) ?: continue
            items += parseItem(itemObject)
        }
        return if (items.isEmpty()) source.loadCatalog().firstOrNull()?.items.orEmpty() else items
    }

    private fun parseItem(item: JSONObject): CatalogItem {
        return CatalogItem(
            title = item.optString("title", item.optString("name", "Untitled")),
            subtitle = item.optString(
                "subtitle",
                item.optString("description", item.optString("summary", ""))
            ),
            tag = item.optString("tag", item.optString("category", item.optString("badge", "GAME"))),
            imageUrl = firstNonBlank(
                item.optString("image_url", ""),
                item.optString("icon", ""),
                item.optString("image", "")
            )
        )
    }

    private fun firstNonBlank(vararg values: String): String? {
        for (value in values) {
            val trimmed = value.trim()
            if (trimmed.isNotEmpty()) {
                return trimmed
            }
        }
        return null
    }
}
