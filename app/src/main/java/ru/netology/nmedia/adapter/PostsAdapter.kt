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

            likeButton.apply {
                text = formatCount(post.likes)
                isChecked = post.likedByMe
                setOnClickListener { listener.onLike(post) }
            }

            shareButton.apply {
                text = formatCount(post.shares)
                setOnClickListener { listener.onShare(post) }
            }

            viewButton.text = formatCount(post.views)


            menuButton.setOnClickListener { view ->
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