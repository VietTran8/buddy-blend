package vn.edu.tdtu.buddyblend.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.models.InteractNotification;
import vn.edu.tdtu.buddyblend.ui.activities.MainActivity;

public class MyFMService extends FirebaseMessagingService {

    public MyFMService() {
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        InteractNotification interactNotification = null;
        if(message.getData().containsKey("type")) {
            interactNotification = new InteractNotification(message.getData());
            Intent intent = new Intent("NEW_NOTIFICATION");
            intent.putExtra("notification_data", new Gson().toJson(interactNotification));
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = "Default";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_checked_16)
                .setContentTitle(message.getData().get("title"))
                .setContentText(message.getData().get("content"))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Title",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("NewMessage", "New token: " + token);
        super.onNewToken(token);
    }
}