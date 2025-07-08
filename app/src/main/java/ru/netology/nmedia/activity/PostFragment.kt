package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels

class PostFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    private lateinit var holder: PostsAdapter.PostViewHolder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)

        val postId = arguments?.getLong("postId") ?: 0L

        holder = PostsAdapter.PostViewHolder(binding.post, object : OnInteractionListener {
            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

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


            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
                findNavController().navigateUp()
            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)
                findNavController().navigate(
                    R.id.action_postFragment_to_editPostFragment,
                    bundleOf(EditPostFragment.ARG_TEXT to post.content)
                )
            }


            override fun onVideo(post: Post) {
            }


            override fun onPostClicked(post: Post) {
            }
        })

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val post = posts.find { it.id == postId }
            if (post != null) {
                holder.bind(post)
            } else {
                findNavController().navigateUp()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
