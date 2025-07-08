package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentEditPostBinding
import ru.netology.nmedia.viewmodel.PostViewModel

class EditPostFragment : Fragment() {

    private var _binding: FragmentEditPostBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostViewModel by activityViewModels()

    companion object {
        const val ARG_TEXT = "post_text"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPostBinding.inflate(inflater, container, false)
        val originalText = arguments?.getString(ARG_TEXT) ?: ""
        binding.content.setText(originalText)
        binding.content.requestFocus()
        binding.content.setSelection(originalText.length)

        binding.editButton.setOnClickListener {
            val newContent = binding.content.text.toString()
            if (newContent.isBlank()) {
                findNavController().navigateUp()
            } else {
                viewModel.changeContentAndSave(newContent)
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
