package ru.netology.nmedia.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.*
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException

class PostRepositoryImpl(private val dao: PostDao) : PostRepository {
    override val data = dao.getAll()
        .map(List<PostEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override suspend fun getAll() {
        try {
            val response = PostsApi.service.getAll()
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.removeAll()
            dao.insert(body.toEntity(true))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun dislikeById(id: Long) {
        val post = dao.getPostById(id) ?: return
        dao.updatePost(post.copy(likedByMe = false, likes = post.likes - 1))
    }

    override fun getNewerCount(): Flow<Int> = dao.countHidden()

    override suspend fun save(post: Post) {
        try {
            val response = PostsApi.service.save(post)
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        dao.removeById(id)
        val response = PostsApi.service.removeById(id)
        if (!response.isSuccessful) throw ApiError(response.code(), response.message())
    }

    override suspend fun likeById(id: Long) {
        val response = PostsApi.service.likeById(id)
        if (!response.isSuccessful) throw ApiError(response.code(), response.message())
        val body = response.body() ?: throw ApiError(response.code(), response.message())
        dao.insert(PostEntity.fromDto(body))
    }

    override suspend fun checkNewer() {
        val maxId = dao.maxId() ?: 0L
        val response = PostsApi.service.getNewer(maxId)
        if (!response.isSuccessful) throw ApiError(response.code(), response.message())
        val body = response.body() ?: throw ApiError(response.code(), response.message())
        if (body.isNotEmpty()) {
            dao.insert(body.toEntity(false))
        }
    }

    override suspend fun markAllVisible() {
        dao.showAllHidden()
    }
}
