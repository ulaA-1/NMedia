package ru.netology.nmedia.activity


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel


class NewPostFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(inflater, container, false)
        binding.content.requestFocus()

        arguments?.textArg?.let(binding.content::setText)

        binding.saveButton.setOnClickListener {
            val content = binding.content.text.toString()
            if (content.isNotBlank()) {
                viewModel.changeContentAndSave(content)
                findNavController().navigateUp()
            }
        }
        
        return binding.root
    }

    companion object {
        var Bundle.textArg by StringArg
    }
}
