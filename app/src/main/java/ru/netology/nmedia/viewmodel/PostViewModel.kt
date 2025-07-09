package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryRoomImpl
import ru.netology.nmedia.db.AppDb

private val empty = Post(
    id = 0,
    author = "",
    published = "",
    content = "",
    likedByMe = false
)

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PostRepository = PostRepositoryRoomImpl(
        AppDb.getInstance(application).postDao
    )

    val data: LiveData<List<Post>> = repository.getAll()
    val edited = MutableLiveData(empty)

    fun save() {
        edited.value?.let {
            repository.save(it)
            resetEdited()
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) return
        edited.value = edited.value?.copy(content = text)
    }

    fun resetEdited() {
        edited.value = empty
    }

    fun likeById(id: Long) {
        repository.likeById(id)
    }

    fun removeById(id: Long) {
        repository.removeById(id)
    }

    fun shareById(id: Long) {
        repository.shareById(id)
    }

    fun changeContentAndSave(text: String) {
        edited.value?.let {
            if (it.content != text) {
                repository.save(it.copy(content = text))
            }
        }
        resetEdited()
    }

}
