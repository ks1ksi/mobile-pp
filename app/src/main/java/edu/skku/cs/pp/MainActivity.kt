package edu.skku.cs.pp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var addBookmarkButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerview)
        addBookmarkButton = findViewById(R.id.add_bookmark_button)
        addBookmarkButton.setOnClickListener {
            val intent = Intent(this, AddBookmark::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        db = Room.databaseBuilder(
            applicationContext, AppDatabase::class.java, "bookmark-database"
        ).build()

        // Get bookmarks from database
        CoroutineScope(Dispatchers.IO).launch {
            val bookmarks = db.bookmarkDao().getAll()
            withContext(Dispatchers.Main) {
                recyclerView.layoutManager =
                    androidx.recyclerview.widget.LinearLayoutManager(this@MainActivity)
                recyclerView.adapter = BookmarkAdapter(bookmarks)
            }
        }

    }
}