package edu.skku.cs.pp

data class ExtractResponse(
    val url: String,
    val title: String,
    val description: String,
    val links: List<String>,
    val image: String,
    val content: String,
    val author: String,
    val source: String,
    val published: String,
    val ttr: Int,
)