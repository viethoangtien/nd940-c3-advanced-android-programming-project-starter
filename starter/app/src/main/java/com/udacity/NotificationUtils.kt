/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.Manifest
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

// Notification ID.
private const val NOTIFICATION_ID = 0
const val ACTION_REQUEST_CODE = 1998

fun sendNotification(context: Context, fileName: String, isSuccess: Boolean) {
    val contentIntent = Intent(context, DetailActivity::class.java).apply {
        putExtra(DetailActivity.EXTRA_FILE_NAME, fileName)
        putExtra(DetailActivity.EXTRA_STATUS, isSuccess)
    }
    val contentPendingIntent = PendingIntent.getActivity(
        context,
        1,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val actionIntent = Intent(context, NotificationBroadcastReceiver::class.java).apply {
        putExtra(NotificationBroadcastReceiver.EXTRA_FILE_NAME, fileName)
        putExtra(NotificationBroadcastReceiver.EXTRA_STATUS, isSuccess)
        action = NotificationBroadcastReceiver.ACTION_STATUS
    }
    val actionPendingIntent: PendingIntent =
        PendingIntent.getBroadcast(
            context,
            ACTION_REQUEST_CODE,
            actionIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

    val title = context.getString(R.string.notification_title)
    val description = context.getString(R.string.notification_description)

    val builder = NotificationCompat.Builder(
        context,
        context.getString(R.string.notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(title)
        .setContentText(description)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .addAction(
            R.drawable.ic_launcher_foreground, context.getString(R.string.check_the_status),
            actionPendingIntent
        )


    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return@with
        }
        // notificationId is a unique int for each notification that you must define.
        notify(NOTIFICATION_ID, builder.build())
    }
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}
