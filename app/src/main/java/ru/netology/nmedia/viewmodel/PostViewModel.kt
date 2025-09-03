package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PostRepositoryImpl(AppDb.getInstance(application).postDao())

    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel> get() = _data

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState> get() = _dataState

    private val _scrollToTop = SingleLiveEvent<Unit>()
    val scrollToTop: LiveData<Unit> = _scrollToTop

    private val emptyPost = Post(0, "", "", "", "", false, 0)
    private val edited = MutableLiveData(emptyPost)

    val newerCount: LiveData<Int> = repository.getNewerCount().asLiveData()

    init {
        loadPosts()
        observePosts()
        startNewerChecker()
    }

    private fun observePosts() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.data.collect { posts ->
                _data.postValue(FeedModel(posts))
            }
        }
    }

    private fun startNewerChecker() {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    repository.checkNewer()
                } catch (_: Exception) {
                }
                delay(10_000)
            }
        }
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun onShowNewPostsClicked() = viewModelScope.launch {
        repository.markAllVisible()
        _scrollToTop.postValue(Unit)
    }

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            val post = _data.value?.posts?.find { it.id == id } ?: return@launch
            val liked = !post.likedByMe

            if (liked) repository.likeById(id) else repository.dislikeById(id)

            val updatedPosts = _data.value?.posts.orEmpty().map { p ->
                if (p.id == id) p.copy(
                    likedByMe = liked,
                    likes = if (liked) p.likes + 1 else p.likes - 1
                ) else p
            }

            _data.postValue(_data.value?.copy(posts = updatedPosts))

        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }


    fun removeById(id: Long) = viewModelScope.launch {
        try {
            repository.removeById(id)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContentAndSave(content: String) {
        val text = content.trim()
        edited.value?.let { post ->
            if (post.content != text) {
                viewModelScope.launch {
                    try {
                        _dataState.value = FeedModelState(loading = true)
                        repository.save(post.copy(content = text))
                        loadPosts()
                        _dataState.value = FeedModelState()
                    } catch (e: Exception) {
                        _dataState.value = FeedModelState(error = true)
                    }
                }
            }
        }
        edited.value = emptyPost
    }
}
