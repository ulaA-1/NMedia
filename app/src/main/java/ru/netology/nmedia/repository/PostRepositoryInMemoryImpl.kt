package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.R
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemoryImpl(
    private val context: Context
) : PostRepository {

    private var posts = listOf(
        Post(
            id = 1,
            author = context.getString(R.string.Netology),
            published = context.getString(R.string.Data),
            content = context.getString(R.string.NetologyText) + " (пост 1)",
            likedByMe = false,
            likes = 1999,
            shares = 5,
            views = 12_500
        ),
        Post(
            id = 2,
            author = context.getString(R.string.Netology),
            published = context.getString(R.string.Data),
            content = context.getString(R.string.NetologyText) + " (пост 2)",
            likedByMe = true,
            likes = 2500,
            shares = 12,
            views = 15_000
        ),
        Post(
            id = 3,
            author = context.getString(R.string.Netology),
            published = context.getString(R.string.Data),
            content = context.getString(R.string.NetologyText) + " (пост 3)",
            likedByMe = false,
            likes = 1750,
            shares = 7,
            views = 9_500
        ),
        Post(
            id = 4,
            author = context.getString(R.string.Netology),
            published = context.getString(R.string.Data),
            content = context.getString(R.string.NetologyText) + " (пост 4)",
            likedByMe = true,
            likes = 3000,
            shares = 25,
            views = 20_000
        )
    )

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
}