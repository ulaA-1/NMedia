package ru.netology.nmedia.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Post
import java.io.IOException

class PostRepositoryImpl : PostRepository {

    override fun getAllAsync(callback: PostRepository.GetAllCallback) {
        PostsApi.retrofitService.getAll().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (!response.isSuccessful) {
                    callback.onError(IOException("Ошибка сервера: ${response.code()} - ${response.message()}"))
                    return
                }
                val body = response.body() ?: return callback.onError(IOException("Тело ответа пусто"))
                callback.onSuccess(body)
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                callback.onError(IOException("Ошибка сети: ${t.message}", t))
            }
        })
    }

    override fun likeByIdAsync(id: Long, callback: PostRepository.PostCallback) {
        PostsApi.retrofitService.likeById(id).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    callback.onError(IOException("Ошибка сервера: ${response.code()} - ${response.message()}"))
                    return
                }
                val body = response.body() ?: return callback.onError(IOException("Тело ответа пусто"))
                callback.onSuccess(body)
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(IOException("Ошибка сети: ${t.message}", t))
            }
        })
    }

    override fun unlikeByIdAsync(id: Long, callback: PostRepository.PostCallback) {
        PostsApi.retrofitService.dislikeById(id).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    callback.onError(IOException("Ошибка сервера: ${response.code()} - ${response.message()}"))
                    return
                }
                val body = response.body() ?: return callback.onError(IOException("Тело ответа пусто"))
                callback.onSuccess(body)
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(IOException("Ошибка сети: ${t.message}", t))
            }
        })
    }

    override fun saveAsync(post: Post, callback: PostRepository.PostCallback) {
        PostsApi.retrofitService.save(post).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    callback.onError(IOException("Ошибка сервера: ${response.code()} - ${response.message()}"))
                    return
                }
                val body = response.body() ?: return callback.onError(IOException("Тело ответа пусто"))
                callback.onSuccess(body)
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(IOException("Ошибка сети: ${t.message}", t))
            }
        })
    }

    override fun removeByIdAsync(id: Long, callback: PostRepository.ActionCallback) {
        PostsApi.retrofitService.removeById(id).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (!response.isSuccessful) {
                    callback.onError(IOException("Ошибка сервера: ${response.code()} - ${response.message()}"))
                    return
                }
                callback.onSuccess()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callback.onError(IOException("Ошибка сети: ${t.message}", t))
            }
        })
    }

    override fun shareByIdAsync(id: Long, callback: PostRepository.PostCallback) {
        PostsApi.retrofitService.shareById(id).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    callback.onError(IOException("Ошибка сервера: ${response.code()} - ${response.message()}"))
                    return
                }
                val body = response.body() ?: return callback.onError(IOException("Тело ответа пусто"))
                callback.onSuccess(body)
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(IOException("Ошибка сети: ${t.message}", t))
            }
        })
    }
}
