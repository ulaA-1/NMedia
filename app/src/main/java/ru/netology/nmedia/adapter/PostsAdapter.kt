package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post

interface OnInteractionListener {
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onRemove(post: Post)
    fun onEdit(post: Post)
}

class PostsAdapter(
    private val listener: OnInteractionListener
) : ListAdapter<Post, PostsAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PostViewHolder(
        private val binding: CardPostBinding,
        private val listener: OnInteractionListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) = with(binding) {
            title.text = post.author
            date.text = post.published
            contentText.text = post.content

            likeCount.text = formatCount(post.likes)
            shareCount.text = formatCount(post.shares)
            viewCount.text = formatCount(post.views)

            likeIcon.setImageResource(
                if (post.likedByMe) R.drawable.ic_like_red
                else R.drawable.ic_like
            )

            likeIcon.setOnClickListener { listener.onLike(post) }
            shareIcon.setOnClickListener { listener.onShare(post) }

            menuButton.setOnClickListener { view: View ->
                PopupMenu(view.context, view).apply {
                    inflate(R.menu.post_actions)
                    setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.remove -> {
                                listener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                listener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                    show()
                }
            }
        }

        private fun formatCount(count: Int): String = when {
            count < 1_000 -> count.toString()
            count in 1_000..9_999 -> "${count / 1_000}.${(count % 1_000) / 100}K"
            count in 10_000..999_999 -> "${count / 1_000}K"
            else -> "${count / 1_000_000}.${(count % 1_000_000) / 100_000}M"
        }
    }
}
