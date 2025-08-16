package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
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

    private fun parsePosts(body: String): List<Post> {
        return gson.fromJson(body, typeToken.type)
    }

    private fun parsePost(body: String): Post {
        return gson.fromJson(body, Post::class.java)
    }

    override fun getAllAsync(callback: PostRepository.GetAllCallback) {
        val request = Request.Builder()
            .url("$BASE_URL/api/slow/posts")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    try {
                        if (!it.isSuccessful) {
                            callback.onError(IOException("Unexpected code $it"))
                            return
                        }

                        val body = it.body?.string()
                            ?: return callback.onError(IOException("Response body is null"))

                        val posts = try {
                            parsePosts(body)
                        } catch (e: Exception) {
                            return callback.onError(IOException("Invalid response format: ${e.message}"))
                        }

                        callback.onSuccess(posts)
                    } catch (e: Exception) {
                        callback.onError(IOException("Error parsing response: ${e.message}"))
                    }
                }
            }
        })
    }

    override fun likeByIdAsync(id: Long, callback: PostRepository.PostCallback) {
        val request = Request.Builder()
            .post("".toRequestBody())
            .url("$BASE_URL/api/posts/$id/likes")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = callback.onError(e)
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        callback.onError(IOException("Unexpected code $it"))
                        return
                    }
                    val body =
                        it.body?.string() ?: return callback.onError(IOException("Body is null"))
                    try {
                        callback.onSuccess(parsePost(body))
                    } catch (e: Exception) {
                        callback.onError(IOException("Parse error: ${e.message}"))
                    }
                }
            }
        })
    }

    override fun unlikeByIdAsync(id: Long, callback: PostRepository.PostCallback) {
        val request = Request.Builder()
            .delete()
            .url("$BASE_URL/api/posts/$id/likes")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = callback.onError(e)
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        callback.onError(IOException("Unexpected code $it"))
                        return
                    }
                    val body =
                        it.body?.string() ?: return callback.onError(IOException("Body is null"))
                    try {
                        callback.onSuccess(parsePost(body))
                    } catch (e: Exception) {
                        callback.onError(IOException("Parse error: ${e.message}"))
                    }
                }
            }
        })
    }

    override fun saveAsync(post: Post, callback: PostRepository.PostCallback) {
        val request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("$BASE_URL/api/slow/posts")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = callback.onError(e)
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        callback.onError(IOException("Unexpected code $it"))
                        return
                    }
                    val body =
                        it.body?.string() ?: return callback.onError(IOException("Body is null"))
                    try {
                        callback.onSuccess(parsePost(body))
                    } catch (e: Exception) {
                        callback.onError(IOException("Parse error: ${e.message}"))
                    }
                }
            }
        })
    }

    override fun removeByIdAsync(id: Long, callback: PostRepository.ActionCallback) {
        val request = Request.Builder()
            .delete()
            .url("$BASE_URL/api/slow/posts/$id")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = callback.onError(e)
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        callback.onError(IOException("Unexpected code $it"))
                        return
                    }
                    callback.onSuccess()
                }
            }
        })
    }

    override fun shareByIdAsync(id: Long, callback: PostRepository.PostCallback) {
        val request = Request.Builder()
            .post("".toRequestBody())
            .url("$BASE_URL/api/slow/posts/$id/share")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = callback.onError(e)
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        callback.onError(IOException("Unexpected code $it"))
                        return
                    }
                    val body =
                        it.body?.string() ?: return callback.onError(IOException("Body is null"))
                    try {
                        callback.onSuccess(parsePost(body))
                    } catch (e: Exception) {
                        callback.onError(IOException("Parse error: ${e.message}"))
                    }
                }
            }
        })
    }
}
