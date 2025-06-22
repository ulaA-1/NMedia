package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: PostViewModel by viewModels()

    private lateinit var editPostLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        editPostLauncher = registerForActivityResult(EditPostResultContract) { editedContent ->
            if (editedContent != null) {
                viewModel.changeContentAndSave(editedContent)
            } else {
                viewModel.clearEdited()
            }
        }

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) = viewModel.likeById(post.id)

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val chooser = Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(chooser)
                viewModel.shareById(post.id)
            }

            override fun onRemove(post: Post) = viewModel.removeById(post.id)

            override fun onEdit(post: Post) {
                viewModel.edit(post)
                editPostLauncher.launch(post.content)
            }

            override fun onVideo(post: Post) {
                post.video?.let {
                    val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(it))
                    val chooser = Intent.createChooser(intent, getString(R.string.play_video))
                    startActivity(chooser)
                }
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

        val newPostLauncher = registerForActivityResult(NewPostResultContract) { content ->
            content ?: return@registerForActivityResult
            viewModel.changeContentAndSave(content)
        }

        binding.saveButton.setOnClickListener {
            newPostLauncher.launch(Unit)
        }
    }
}

