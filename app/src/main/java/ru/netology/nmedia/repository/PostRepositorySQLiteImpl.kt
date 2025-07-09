package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post

class PostRepositorySQLiteImpl(private val dao: PostDao) : PostRepository {
    private val data = MutableLiveData(dao.getAll())

    override fun getAll(): LiveData<List<Post>> = data

    override fun likeById(id: Long) {
        dao.likeById(id)
        data.value = dao.getAll()
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
        data.value = dao.getAll()
    }

    override fun save(post: Post) {
        dao.save(post)
        data.value = dao.getAll()
    }

    override fun shareById(id: Long) {
        val posts = dao.getAll()
        val post = posts.find { it.id == id } ?: return
        val updated = post.copy(shares = post.shares + 1)
        dao.save(updated)
        data.value = dao.getAll()
    }

}
