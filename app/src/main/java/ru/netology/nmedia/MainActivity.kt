package ru.netology.nmedia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var post: Post? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        post = Post(
            id = 1,
            author = getString(R.string.Netology),
            published = getString(R.string.Data),
            content = getString(R.string.NetologyText),
            likedByMe = false,
            likes = 999,
            shares = 5,
            views = 12500
        )

        updateUI()

        binding.likeIcon.setOnClickListener {
            post?.likedByMe = !(post?.likedByMe ?: false)
            post?.likes = (post?.likes ?: 0) + if (post?.likedByMe == true) 1 else -1
            updateUI()
        }

        binding.shareIcon.setOnClickListener {
            post?.shares = (post?.shares ?: 0) + 1
            updateUI()
        }
    }

    private fun updateUI() {
        post?.let {
            with(binding) {
                title.text = it.author
                date.text = it.published
                contentText.text = it.content

                likeIcon.setImageResource(
                    if (it.likedByMe) R.drawable.ic_like_red else R.drawable.ic_like
                )

                likeCount.text = formatCount(it.likes)
                shareCount.text = formatCount(it.shares)
                viewCount.text = formatCount(it.views)
            }
        }
    }

    private fun formatCount(count: Int): String {
        return when {
            count < 1_000 -> count.toString()
            count in 1_000..9_999 -> String.format(Locale.US, "%.1fK", count / 1_000.0)
            count in 10_000..999_999 -> "${count / 1_000}K"
            else -> String.format(Locale.US, "%.1fM", count / 1_000_000.0)
        }
    }
}
