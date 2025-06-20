package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.R
import ru.netology.nmedia.dto.Post
import java.text.SimpleDateFormat
import java.util.*

class PostRepositoryInMemoryImpl(
    private val context: Context
) : PostRepository {

    private var posts = listOf(
        Post(
            id = 1,
            author = context.getString(R.string.Netology),
            published = context.getString(R.string.Data),
            content = context.getString(R.string.NetologyText),
            likedByMe = false,
            likes = 1999,
            shares = 5,
            views = 12_500
        ),

        Post(
            id = 2,
            author = context.getString(R.string.Netology),
            published = context.getString(R.string.Data),
            content =
                " Привет, это новая Нетология, и у нас для вас полезный разбор актуальной темы. " +
                        "Быстрее переходи на Rutube по ссылке",
            likedByMe = true,
            likes = 3_000,
            shares = 25,
            views = 20_000,
            video = "https://rutube.ru/video/e660f8ff093ce6029ddca2907dafb88c/?r=wd"
        ),

        Post(
            id = 3,
            author = context.getString(R.string.Netology),
            published = context.getString(R.string.Data),
            content = context.getString(R.string.NetologyText),
            likedByMe = false,
            likes = 2500,
            shares = 12,
            views = 15_000
        ), Post(
            id = 4,
            author = context.getString(R.string.Netology),
            published = context.getString(R.string.Data),
            content = context.getString(R.string.NetologyText),
            likedByMe = false,
            likes = 1750,
            shares = 7,
            views = 9_500
        ), Post(
            id = 5,
            author = context.getString(R.string.Netology),
            published = context.getString(R.string.Data),
            content = context.getString(R.string.NetologyText),
            likedByMe = false,
            likes = 3000,
            shares = 25,
            views = 20_000
        )
    )
    private var nextId = 5L
    private val data = MutableLiveData(posts)

    override fun getAll(): LiveData<List<Post>> = data

    override fun likeById(id: Long) {
        posts = posts.map { post ->
            if (post.id != id) post else post.copy(
                likedByMe = !post.likedByMe,
                likes = if (post.likedByMe) post.likes - 1 else post.likes + 1
            )
        }
        data.value = posts
    }

    override fun shareById(id: Long) {
        posts = posts.map { post ->
            if (post.id != id) post else post.copy(shares = post.shares + 1)
        }
        data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
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
        data.value = posts
    }

    private fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return formatter.format(Date())
    }
}
