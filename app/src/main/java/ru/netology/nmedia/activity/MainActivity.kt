package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {

    private val empty = Post(
        id = 0,
        author = "",
        published = "",
        content = "",
        likedByMe = false
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) = viewModel.likeById(post.id)
            override fun onShare(post: Post) = viewModel.shareById(post.id)
            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
                if (viewModel.edited.value?.id == post.id) {
                    viewModel.edited.value = empty
                }
            }
            override fun onEdit(post: Post) {
                viewModel.edited.value = post
                binding.content.setText(post.content)
                AndroidUtils.showKeyboard(binding.content)
            }
        })

        binding.list.adapter = adapter

        viewModel.data.observe(this) { posts ->
            val newPost = adapter.currentList.size < posts.size
            adapter.submitList(posts) {
                if (newPost) {
                    binding.list.scrollToPosition(0)
                }
            }
        }

        viewModel.edited.observe(this) { post ->
            val isEditing = post.id != 0L
            binding.editingGroup.visibility = if (isEditing) View.VISIBLE else View.GONE
            if (!isEditing) {
                binding.content.setText("")
                AndroidUtils.hideKeyBoard(binding.content)
            }
        }

        binding.add.setOnClickListener {
            val text = binding.content.text.toString()
            if (text.isBlank()) {
                Toast.makeText(this, R.string.error_empty_content, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.changeContentAndSave(text)
            binding.content.setText("")
            binding.editingGroup.visibility = View.GONE
            binding.content.clearFocus()
            AndroidUtils.hideKeyBoard(it)
        }

        binding.cancelEdit.setOnClickListener {
            viewModel.edited.value = empty
            binding.content.setText("")
            binding.editingGroup.visibility = View.GONE
            AndroidUtils.hideKeyBoard(it)
        }
    }
}
