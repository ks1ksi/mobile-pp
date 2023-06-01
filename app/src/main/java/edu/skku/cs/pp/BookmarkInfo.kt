package edu.skku.cs.pp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.stream.JsonWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.io.StringWriter
import java.util.concurrent.TimeUnit

class BookmarkInfo : AppCompatActivity() {

    private val client =
        OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS) // Set the read timeout
            .build()
    private lateinit var db: AppDatabase
    private lateinit var bookmark: Bookmark

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark_info)

        db = Room.databaseBuilder(
            applicationContext, AppDatabase::class.java, "bookmark-database"
        ).build()

        val titleTextView: TextView = findViewById(R.id.textview_title)
        val linkTextView: TextView = findViewById(R.id.textview_link)
        val summaryTextView: TextView = findViewById(R.id.textview_summary)

        val bookmarkId = intent.getIntExtra("bookmark_id", 0)

        CoroutineScope(Dispatchers.IO).launch {
            bookmark = db.bookmarkDao().getBookmark(bookmarkId)
            titleTextView.text = bookmark.title
            linkTextView.text = bookmark.link
            summaryTextView.text = bookmark.summary

            linkTextView.setOnClickListener {
                // open browser
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(bookmark.link))
                startActivity(intent)
            }
        }

        val summaryButton: Button = findViewById(R.id.button_add_summary)

        summaryButton.setOnClickListener {
            Log.d("BookmarkInfo", "summary button clicked")
            summaryButton.isEnabled = false
            val localhost = "http://10.0.2.2:3000/"
            val gson = Gson()

            val bookmarkContentJson = StringWriter()
            val jsonWriter = JsonWriter(bookmarkContentJson)
            jsonWriter.value(bookmark.content)
            jsonWriter.flush()

            val json = """{
                                "content": $bookmarkContentJson
                           }
                        """.trimIndent()

            val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder().url(localhost + "summary").post(body).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")
                        val summaryResponse =
                            gson.fromJson(response.body!!.string(), SummaryResponse::class.java)

                        val summary = summaryResponse.summary
                        CoroutineScope(Dispatchers.IO).launch {
                            db.bookmarkDao().update(
                                Bookmark(
                                    bookmarkId,
                                    bookmark.title,
                                    bookmark.link,
                                    bookmark.content,
                                    summary
                                )
                            )
                            withContext(Dispatchers.Main) {
                                summaryTextView.text = summary
                            }
                        }
                    }
                }
            })
        }

        val deleteButton: Button = findViewById(R.id.button_delete)
        deleteButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                db.bookmarkDao().deleteById(bookmarkId)
                withContext(Dispatchers.Main) {
                    finish()
                }
            }
        }

    }
}