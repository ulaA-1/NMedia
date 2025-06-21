package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityEditPostBinding

class EditPostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val originalText = intent.getStringExtra(Intent.EXTRA_TEXT)
        binding.content.setText(originalText)
        binding.content.requestFocus()
        binding.content.setSelection(0)

        binding.editButton.setOnClickListener {
            val newContent = binding.content.text.toString()
            if (newContent.isBlank()) {
                setResult(Activity.RESULT_CANCELED)
            } else {
                setResult(Activity.RESULT_OK, Intent().apply {
                    putExtra(Intent.EXTRA_TEXT, newContent)
                })
            }
            finish()
        }
    }
}
