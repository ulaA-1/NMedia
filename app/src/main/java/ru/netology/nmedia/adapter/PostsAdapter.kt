package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post

class PostsAdapter(
    private val onLike: (Post) -> Unit,
    private val onShare: (Post) -> Unit
) : ListAdapter<Post, PostsAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onLike, onShare)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PostViewHolder(
        private val binding: CardPostBinding,
        private val onLike: (Post) -> Unit,
        private val onShare: (Post) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) = with(binding) {
            title.text = post.author
            date.text = post.published
            contentText.text = post.content

            likeCount.text = formatCount(post.likes)
            shareCount.text = formatCount(post.shares)
            viewCount.text = formatCount(post.views)

            likeIcon.setImageResource(
                if (post.likedByMe) ru.netology.nmedia.R.drawable.ic_like_red
                else ru.netology.nmedia.R.drawable.ic_like
            )

            likeIcon.setOnClickListener { onLike(post) }
            shareIcon.setOnClickListener { onShare(post) }
        }

        private fun formatCount(count: Int): String = when {
            count < 1_000 -> count.toString()
            count in 1_000..9_999 -> "${count / 1_000}.${(count % 1_000) / 100}K"
            count in 10_000..999_999 -> "${count / 1_000}K"
            else -> "${count / 1_000_000}.${(count % 1_000_000) / 100_000}M"
        }
    }
}
