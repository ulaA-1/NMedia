package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: PostViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.data.observe(this) { post ->
            with(binding) {
                title.text = post.author
                date.text = post.published
                contentText.text = post.content

                likeIcon.setImageResource(
                    if (post.likedByMe) R.drawable.ic_like_red else R.drawable.ic_like
                )
                likeCount.text = formatCount(post.likes)
                shareCount.text = formatCount(post.shares)
                viewCount.text = formatCount(post.views)
            }
        }

        binding.likeIcon.setOnClickListener {
            viewModel.like()
        }

        binding.shareIcon.setOnClickListener {
            viewModel.share()
        }
    }

    private fun formatCount(count: Int): String = when {
        count < 1_000 -> count.toString()
        count in 1_000..9_999 -> {
            val whole = count / 1_000
            val decimal = (count % 1_000) / 100
            "$whole.$decimal" + "K"
        }
        count in 10_000..999_999 -> "${count / 1_000}K"
        else -> {
            val millions = count / 1_000_000
            val decimal = (count % 1_000_000) / 100_000
            "$millions.$decimal" + "M"
        }
    }
}