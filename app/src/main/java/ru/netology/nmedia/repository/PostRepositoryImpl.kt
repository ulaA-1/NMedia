package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                throw IOException("Unexpected code $response")
            }

            val responseBody = response.body?.string()
            return gson.fromJson(responseBody, typeToken.type)
        } catch (e: Exception) {
            throw IOException("Error loading posts: ${e.message}")
        }
    }

    override fun likeById(id: Long): Post {
        val request = Request.Builder()
            .post("".toRequestBody())
            .url("$BASE_URL/api/posts/$id/likes")
            .build()

        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                throw IOException("Unexpected code $response")
            }

            val body = response.body?.string() ?: throw IOException("Body is null")
            return gson.fromJson(body, Post::class.java)
        } catch (e: Exception) {
            throw IOException("Error liking post: ${e.message}")
        }
    }

    override fun unlikeById(id: Long): Post {
        val request = Request.Builder()
            .delete()
            .url("$BASE_URL/api/posts/$id/likes")
            .build()

        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                throw IOException("Unexpected code $response")
            }

            val body = response.body?.string() ?: throw IOException("Body is null")
            return gson.fromJson(body, Post::class.java)
        } catch (e: Exception) {
            throw IOException("Error unliking post: ${e.message}")
        }
    }

    override fun save(post: Post): Post {
        val request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("$BASE_URL/api/slow/posts")
            .build()

        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                throw IOException("Unexpected code $response")
            }

            val body = response.body?.string() ?: throw IOException("Body is null")
            return gson.fromJson(body, Post::class.java)
        } catch (e: Exception) {
            throw IOException("Error saving post: ${e.message}")
        }
    }

    override fun removeById(id: Long) {
        val request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                throw IOException("Unexpected code $response")
            }
        } catch (e: Exception) {
            throw IOException("Error removing post: ${e.message}")
        }
    }

    override fun shareById(id: Long): Post {
        val request = Request.Builder()
            .post("".toRequestBody())
            .url("$BASE_URL/api/slow/posts/$id/share")
            .build()

        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                throw IOException("Unexpected code $response")
            }

            val body = response.body?.string() ?: throw IOException("Body is null")
            return gson.fromJson(body, Post::class.java)
        } catch (e: Exception) {
            throw IOException("Error sharing post: ${e.message}")
        }
    }

}
