package edu.skku.cs.pp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

class AddBookmark : AppCompatActivity() {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS) // Set the connection timeout
        .readTimeout(60, TimeUnit.SECONDS) // Set the read timeout
        .build()
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bookmark)

        db = Room.databaseBuilder(
            applicationContext, AppDatabase::class.java, "bookmark-database"
        ).build()

        val editText = findViewById<EditText>(R.id.edittext_link)
        val button: Button = findViewById(R.id.button_add_link)
        button.setOnClickListener {
            Log.d("AddBookmark", "button clicked")
            val link = editText.text.toString()
            val localhost = "http://10.0.2.2:3000/"
            val json = """
                {
                    "url": "$link"
                }
            """.trimIndent()
            val gson = Gson()

            val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder().url(localhost + "extract").post(body).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")
                        val extractResponse =
                            gson.fromJson(response.body!!.string(), ExtractResponse::class.java)
                        Log.d("AddBookmark", extractResponse.toString())
                        val bookmark = Bookmark(
                            0,
                            extractResponse.title,
                            extractResponse.url,
                            extractResponse.content,
                            null
                        )

                        // Insert bookmark into database
                        CoroutineScope(Dispatchers.IO).launch {
                            db.bookmarkDao().insert(bookmark)
                            finish()
                        }
                    }
                }
            })
        }
    }
}
