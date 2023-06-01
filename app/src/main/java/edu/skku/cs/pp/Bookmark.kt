package edu.skku.cs.pp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String?,
    val link: String,
    val content: String?,
    val summary: String?
) {}