package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) = viewModel.likeById(post.id)

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val chooser =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(chooser)
            }

            override fun onRemove(post: Post) = viewModel.removeById(post.id)

            override fun onEdit(post: Post) {
                viewModel.edit(post)
                findNavController().navigate(
                    R.id.action_feedFragment_to_editPostFragment,
                    bundleOf(EditPostFragment.ARG_TEXT to post.content)
                )
            }

            override fun onVideo(post: Post) {
                post.video?.let {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                    val chooser =
                        Intent.createChooser(intent, getString(R.string.play_video))
                    startActivity(chooser)
                }
            }

            override fun onPostClicked(post: Post) {
                val bundle = Bundle().apply { putLong("postId", post.id) }
                findNavController().navigate(R.id.action_feedFragment_to_postFragment, bundle)
            }
        })

        binding.list.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { state ->
            val posts = state.posts
            val isNewPostAdded = adapter.currentList.size < posts.size

            adapter.submitList(posts) {
                if (isNewPostAdded) binding.list.scrollToPosition(0)
            }

            binding.progress.isVisible = state.loading
            binding.errorGroup.isVisible = state.error
            binding.emptyText.isVisible = state.empty
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            binding.list.scrollToPosition(0)
        }


        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

        binding.saveButton.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        return binding.root
    }
}
