package edu.skku.cs.pp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmark")
    suspend fun getAll(): List<Bookmark>

    @Query("SELECT * FROM bookmark WHERE id = :id")
    suspend fun getBookmark(id: Int): Bookmark

    @Insert
    suspend fun insert(bookmark: Bookmark)

    @Insert
    suspend fun insertAll(vararg bookmarks: Bookmark)


    @Query("DELETE FROM bookmark WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Update
    suspend fun update(bookmark: Bookmark)
}