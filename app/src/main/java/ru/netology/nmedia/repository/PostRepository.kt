package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAllAsync(callback: GetAllCallback)
    fun likeByIdAsync(id: Long, callback: PostCallback)
    fun unlikeByIdAsync(id: Long, callback: PostCallback)
    fun saveAsync(post: Post, callback: PostCallback)
    fun removeByIdAsync(id: Long, callback: ActionCallback)
    fun shareByIdAsync(id: Long, callback: PostCallback)

    interface GetAllCallback {
        fun onSuccess(posts: List<Post>)
        fun onError(e: Exception)
    }

    interface PostCallback {
        fun onSuccess(post: Post)
        fun onError(e: Exception)
    }

    interface ActionCallback {
        fun onSuccess()
        fun onError(e: Exception)
    }
}
