package com.udacity

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when {
            intent.action == ACTION_STATUS -> {
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager.cancelAll()
                Intent(context, DetailActivity::class.java).apply {
                    putExtra(
                        DetailActivity.EXTRA_FILE_NAME,
                        intent.getStringExtra(EXTRA_FILE_NAME)
                    )
                    putExtra(
                        DetailActivity.EXTRA_STATUS,
                        intent.getBooleanExtra(EXTRA_STATUS, false)
                    )
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(this)
                }
            }

            else -> {

            }
        }
    }

    companion object {
        const val ACTION_STATUS = "ACTION_STATUS"
        const val EXTRA_FILE_NAME = "EXTRA_FILE_NAME_BROADCAST"
        const val EXTRA_STATUS = "EXTRA_STATUS_BROADCAST"
    }
}