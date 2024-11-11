package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0
    private var downloadUrl: String = ""
    private var fileNameToDownload: String = ""
    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        createNotificationChannel()
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        binding.layoutContent.customButton.setOnClickListener {
            if (downloadUrl.isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_message), Toast.LENGTH_SHORT).show()
            } else {
                download()
            }
        }

        binding.layoutContent.groupDownload.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_glide -> {
                    downloadUrl = GLIDE_URL
                    fileNameToDownload = getString(R.string.download_first_item)
                }

                R.id.radio_load_app -> {
                    downloadUrl = UDACITY_URL
                    fileNameToDownload = getString(R.string.download_second_item)
                }

                R.id.radio_retrofit -> {
                    downloadUrl = RETROFIT_URL
                    fileNameToDownload = getString(R.string.download_third_item)
                }

                else -> {

                }
            }
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        notificationManager = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.notification_channel_id), name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            notificationManager.createNotificationChannel(channel)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val isSuccess = id == downloadID
            sendNotification(
                context = this@MainActivity,
                fileName = fileNameToDownload,
                isSuccess = isSuccess
            )
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(downloadUrl))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val GLIDE_URL =
            "https://filesampleshub.com/download/document/pdf/sample1.pdf"
        private const val UDACITY_URL =
            "https://filesampleshub.com/download/document/pdf/sample1.pdf"
        private const val RETROFIT_URL =
            "https://filesampleshub.com/download/document/pdf/sample1.pdf"
    }
}