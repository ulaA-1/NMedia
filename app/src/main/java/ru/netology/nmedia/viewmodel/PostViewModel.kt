package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.SingleLiveEvent

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
        _data.value = _data.value?.copy(loading = true) ?: FeedModel(loading = true)

        repository.getAllAsync(object : PostRepository.GetAllCallback {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(
                    _data.value?.copy(
                        posts = posts,
                        empty = posts.isEmpty(),
                        loading = false,
                        error = false
                    ) ?: FeedModel(posts = posts, empty = posts.isEmpty())
                )
            }

            override fun onError(e: Exception) {
                _data.postValue(
                    FeedModel(
                        posts = emptyList(),
                        loading = false,
                        error = true,
                        empty = true
                    )
                )
            }
        })
    }

    fun save(content: String) {
        val newPost = Post(
            id = 0L,
            author = "",
            content = content,
            published = "",
            likedByMe = false,
            likes = 0
        )

        _data.value = _data.value?.copy(loading = true)

        repository.saveAsync(newPost, object : PostRepository.PostCallback {
            override fun onSuccess(post: Post) {
                val updated = _data.value?.posts.orEmpty().toMutableList()
                updated.add(0, post)
                _data.postValue(
                    _data.value?.copy(
                        posts = updated,
                        loading = false,
                        error = false,
                        empty = updated.isEmpty()
                    )
                )
                _postCreated.postValue(Unit)
            }

            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(error = true, loading = false))
            }
        })
    }


    fun changeContentAndSave(content: String) {
        val text = content.trim()
        edited.value?.let { post ->
            if (post.content != text) {
                _data.value = _data.value?.copy(loading = true)
                repository.saveAsync(
                    post.copy(content = text),
                    object : PostRepository.PostCallback {
                        override fun onSuccess(post: Post) {
                            val currentPosts = _data.value?.posts.orEmpty()
                            val newPosts = currentPosts.map { if (it.id == post.id) post else it }
                            _data.postValue(_data.value?.copy(posts = newPosts, loading = false))
                            _postCreated.postValue(Unit)
                        }

                        override fun onError(e: Exception) {
                            _data.postValue(_data.value?.copy(error = true, loading = false))
                        }
                    })
            }
        }
        edited.value = emptyPost
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun likeById(id: Long) {

        val current = _data.value?.posts.orEmpty()
        val post = current.find { it.id == id } ?: return

        _data.value = _data.value?.copy(loading = true)

        if (post.likedByMe) {
            repository.unlikeByIdAsync(id, object : PostRepository.PostCallback {
                override fun onSuccess(updated: Post) {
                    val newList =
                        _data.value?.posts.orEmpty().map { if (it.id == id) updated else it }
                    _data.postValue(_data.value?.copy(posts = newList, loading = false))
                }

                override fun onError(e: Exception) {
                    _data.postValue(_data.value?.copy(error = true, loading = false))
                }
            })
        } else {
            repository.likeByIdAsync(id, object : PostRepository.PostCallback {
                override fun onSuccess(updated: Post) {
                    val newList =
                        _data.value?.posts.orEmpty().map { if (it.id == id) updated else it }
                    _data.postValue(_data.value?.copy(posts = newList, loading = false))
                }

                override fun onError(e: Exception) {
                    _data.postValue(_data.value?.copy(error = true, loading = false))
                }
            })
        }
    }

    fun removeById(id: Long) {

        val old = _data.value?.posts.orEmpty()
        _data.postValue(_data.value?.copy(posts = old.filter { it.id != id }))

        repository.removeByIdAsync(id, object : PostRepository.ActionCallback {
            override fun onSuccess() {

            }

            override fun onError(e: Exception) {

                _data.postValue(_data.value?.copy(posts = old))
            }
        })
    }

    fun shareById(id: Long) {
        repository.shareByIdAsync(id, object : PostRepository.PostCallback {
            override fun onSuccess(updated: Post) {
                val newList = _data.value?.posts.orEmpty().map { if (it.id == id) updated else it }
                _data.postValue(_data.value?.copy(posts = newList))
            }

            override fun onError(e: Exception) {

                _data.postValue(_data.value?.copy(error = true))
            }
        })
    }
}
