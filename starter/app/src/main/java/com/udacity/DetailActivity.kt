package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        getArgs()
        initViews()
    }

    private fun getArgs() {
        val fileName = intent.getStringExtra(EXTRA_FILE_NAME)
        val isSuccess = intent.getBooleanExtra(EXTRA_STATUS, false)
        binding.layoutContentDetail.apply {
            textFileName.text = fileName
            textStatus.setTextColor(
                ContextCompat.getColor(
                    this@DetailActivity,
                    if (isSuccess) R.color.colorPrimaryDark else R.color.color_fail
                )
            )
            textStatus.text =
                if (isSuccess) getString(R.string.success) else getString(R.string.fail)
        }

    }

    private fun initViews() {
        binding.layoutContentDetail.apply {
            buttonOk.setOnClickListener {
                finish()
            }
        }
    }

    companion object {
        const val EXTRA_FILE_NAME = "EXTRA_FILE_NAME"
        const val EXTRA_STATUS = "EXTRA_STATUS"
    }
}
