package ru.netology.nmedia.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import ru.netology.nmedia.dto.Post

class PostDaoImpl(private val db: SQLiteDatabase) : PostDao {

    override fun getAll(): List<Post> {
        val posts = mutableListOf<Post>()
        val cursor = db.query(
            "posts", null, null, null, null, null, "id DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                posts += Post(
                    id = it.getLong(it.getColumnIndexOrThrow("id")),
                    author = it.getString(it.getColumnIndexOrThrow("author")),
                    content = it.getString(it.getColumnIndexOrThrow("content")),
                    published = it.getString(it.getColumnIndexOrThrow("published")),
                    likedByMe = it.getInt(it.getColumnIndexOrThrow("likedByMe")) != 0,
                    likes = it.getInt(it.getColumnIndexOrThrow("likes")),
                    shares = it.getInt(it.getColumnIndexOrThrow("shares")),
                    views = it.getInt(it.getColumnIndexOrThrow("views")),
                    video = it.getString(it.getColumnIndexOrThrow("video"))
                )
            }
        }
        return posts
    }

    override fun save(post: Post): Post {
        val values = ContentValues().apply {
            put("id", post.id)
            put("author", post.author)
            put("published", post.published)
            put("content", post.content)
            put("likedByMe", post.likedByMe)
            put("likes", post.likes)
            put("shares", post.shares)
            put("views", post.views)
            put("video", post.video)
        }
        db.replace("posts", null, values)
        return post
    }

    override fun likeById(id: Long) {
        db.execSQL(
            """
            UPDATE posts SET
                likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
                likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
            WHERE id = ?;
        """.trimIndent(),
            arrayOf(id)
        )
    }


    override fun removeById(id: Long) {
        db.delete("posts", "id = ?", arrayOf(id.toString()))
    }

    override fun shareById(id: Long) {
        db.execSQL(
            """
        UPDATE posts SET
            shares = shares + 1
        WHERE id = ?;
        """.trimIndent(),
            arrayOf(id)
        )
    }

}
