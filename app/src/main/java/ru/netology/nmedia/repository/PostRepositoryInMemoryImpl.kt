package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.R
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemoryImpl(
    private val context: Context
) : PostRepository {

    private var post = Post(
        id = 1,
        author = context.getString(R.string.Netology),
        published = context.getString(R.string.Data),
        content = context.getString(R.string.NetologyText),
        likedByMe = false,
        likes = 1999,
        shares = 5,
        views = 12_500
    )

    private val data = MutableLiveData(post)

    override fun get(): LiveData<Post> = data

    override fun like() {
        post = post.copy(
            likedByMe = !post.likedByMe,
            likes = if (!post.likedByMe) post.likes + 1 else post.likes - 1
        )
        data.value = post
    }

    override fun share() {
        post = post.copy(shares = post.shares + 1)
        data.value = post
    }
}