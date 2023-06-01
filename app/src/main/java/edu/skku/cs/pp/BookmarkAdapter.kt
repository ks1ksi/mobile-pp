package edu.skku.cs.pp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookmarkAdapter(private val bookmarks: List<Bookmark>) : RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {

    class BookmarkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.bookmark_title)
        val link: TextView = itemView.findViewById(R.id.bookmark_link)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bookmark, parent, false)
        return BookmarkViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val bookmark = bookmarks[position]
        holder.title.text = bookmark.title ?: ""
        holder.link.text = bookmark.link
        holder.itemView.setOnClickListener {
            // open bookmark info activity
            val intent = Intent(it.context, BookmarkInfo::class.java)
            intent.putExtra("bookmark_id", bookmark.id)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount() = bookmarks.size
}
