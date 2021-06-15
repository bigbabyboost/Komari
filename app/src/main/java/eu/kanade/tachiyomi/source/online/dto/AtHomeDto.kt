package eu.kanade.tachiyomi.source.online.dto

import kotlinx.serialization.Serializable

@Serializable
data class AtHomeDto(
    val baseUrl: String,
)

@Serializable
data class AtHomeImageReportDto(
    val url: String,
    val success: Boolean,
    val bytes: Int?,
    val cached: Boolean,
    val duration: Long,
)
