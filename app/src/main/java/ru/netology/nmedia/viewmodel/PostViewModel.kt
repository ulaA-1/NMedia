package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

private val emptyPost = Post(
    id = 0L,
    author = "",
    content = "",
    likedByMe = false,
    likes = 0,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PostRepository = PostRepositoryImpl()

    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel> = _data

    val edited = MutableLiveData(emptyPost)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit> = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        thread {
            _data.postValue(_data.value?.copy(loading = true) ?: FeedModel(loading = true))
            try {
                val posts = repository.getAll()
                _data.postValue(
                    _data.value?.copy(
                        posts = posts,
                        empty = posts.isEmpty(),
                        loading = false,
                        error = false
                    ) ?: FeedModel(posts = posts, empty = posts.isEmpty())
                )
            } catch (e: IOException) {
                _data.postValue(
                    FeedModel(
                        posts = emptyList(),
                        loading = false,
                        error = true,
                        empty = true
                    )
                )
            }
        }

    }


    fun save(content: String) {
        thread {
            try {
                val newPost = Post(
                    id = 0L,
                    author = "",
                    content = content,
                    published = "",
                    likedByMe = false,
                    likes = 0
                )
                val saved = repository.save(newPost)
                val updated = _data.value?.posts.orEmpty().toMutableList()
                updated.add(0, saved)
                _data.postValue(
                    _data.value?.copy(
                        posts = updated,
                        loading = false,
                        error = false,
                        empty = updated.isEmpty()
                    )
                )
                _postCreated.postValue(Unit)
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(error = true, loading = false))
            }
        }
    }


    fun changeContentAndSave(content: String) {
        val text = content.trim()
        edited.value?.let { post ->
            if (post.content != text) {
                thread {
                    try {
                        val updatedPost = repository.save(post.copy(content = text))
                        val currentPosts = _data.value?.posts.orEmpty()
                        val newPosts =
                            currentPosts.map { if (it.id == updatedPost.id) updatedPost else it }
                        _data.postValue(_data.value?.copy(posts = newPosts))
                        _postCreated.postValue(Unit)
                    } catch (e: IOException) {
                        _data.postValue(_data.value?.copy(error = true))
                    }
                }
            }
        }
        edited.value = emptyPost
    }

    fun edit(post: Post) {
        edited.value = post
    }


    fun likeById(id: Long) {
        thread {
            try {
                val current = _data.value?.posts.orEmpty()
                val post = current.find { it.id == id } ?: return@thread

                val updated = if (post.likedByMe) {
                    repository.unlikeById(id)
                } else {
                    repository.likeById(id)
                }
                val newList = current.map { if (it.id == id) updated else it }
                _data.postValue(_data.value?.copy(posts = newList))
            } catch (_: IOException) {
            }
        }
    }


    fun removeById(id: Long) {
        thread {
            val old = _data.value?.posts.orEmpty()
            _data.postValue(_data.value?.copy(posts = old.filter { it.id != id }))
            try {
                repository.removeById(id)
            } catch (_: IOException) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        }
    }

    fun shareById(id: Long) {
        thread {
            try {
                val updated = repository.shareById(id)
                val current = _data.value?.posts.orEmpty()
                val newList = current.map { if (it.id == id) updated else it }
                _data.postValue(_data.value?.copy(posts = newList))
            } catch (_: IOException) {
            }
        }
    }
}

