package ru.netology.nmedia.repository

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.R
import ru.netology.nmedia.dto.Post
import java.text.SimpleDateFormat
import java.util.*

class PostRepositoryInFilesImpl(private val context: Context) : PostRepository {


    private var posts = emptyList<Post>()
        set(value) {
            field = value
            data.value = value
            sync()
        }
    private var nextId = 5L
    private val data = MutableLiveData(posts)

    init {
        val file = context.filesDir.resolve(FILENAME)
        if (file.exists()) {
            context.openFileInput(FILENAME).bufferedReader().use {
                posts = gson.fromJson(it, type)
                nextId = (posts.maxOfOrNull { it.id } ?: 0) + 1
            }
        }
    }

    override fun getAll(): LiveData<List<Post>> = data

    override fun likeById(id: Long) {
        posts = posts.map { post ->
            if (post.id != id) {
                post
            } else {
                val newLikedByMe = !post.likedByMe
                val newLikes = if (newLikedByMe) post.likes + 1 else post.likes - 1
                post.copy(likedByMe = newLikedByMe, likes = newLikes)
            }
        }
    }


    override fun shareById(id: Long) {
        posts = posts.map { post ->
            if (post.id != id) post else post.copy(shares = post.shares + 1)
        }
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
    }

    override fun save(post: Post) {
        if (post.id == 0L) {
            posts = listOf(
                post.copy(
                    id = nextId++, author = "Me", published = getCurrentDate()
                )
            ) + posts
        } else {
            posts = posts.map {
                if (it.id != post.id) it else it.copy(content = post.content)
            }
        }
        sync()
    }

    private fun sync() {
        context.openFileOutput(FILENAME, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write(gson.toJson(posts))
        }

    }

    private fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return formatter.format(Date())

    }

    companion object {
        private const val FILENAME = "posts.json"
        private val gson = Gson()
        private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type

    }
}
