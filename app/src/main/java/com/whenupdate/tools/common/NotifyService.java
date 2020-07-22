package com.whenupdate.tools.common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.whenupdate.tools.R;
import com.whenupdate.tools.mvp.MainActivity;

public class NotifyService extends Service {
    private NotificationManager notificationManager;
    private String channel_WhenUPDATE_id;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel_WhenUPDATE_id = "WhenUPDATE_Main";
            String channel_WhenUPDATE_name = "WhenUPDATE_Notify";
            String channel_WhenUPDATE_desc = "Notify is created in NotifyService for WhenUPDATE";
            NotificationChannel channel_WhenUPDATE =
                    new NotificationChannel(channel_WhenUPDATE_id, channel_WhenUPDATE_name,
                            NotificationManager.IMPORTANCE_HIGH);
            channel_WhenUPDATE.setDescription(channel_WhenUPDATE_desc);
            notificationManager.deleteNotificationChannel(channel_WhenUPDATE_id);
            notificationManager.createNotificationChannel(channel_WhenUPDATE);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendNotify();
        return START_NOT_STICKY;
    }

    private void sendNotify() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Builder mNotify = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_sentiment_smale_toast)
                .setContentTitle(getApplication().getString(R.string.title_notify))
                .setContentText(getApplication().getString(R.string.update_exist))
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pIntent)
                .setVibrate(new long[]{1000, 1000});

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotify.setChannelId(channel_WhenUPDATE_id);
            startForeground(1, mNotify.build());
            stopForeground(false);
        } else {
            notificationManager.notify(1, mNotify.build());
        }
    }
}
