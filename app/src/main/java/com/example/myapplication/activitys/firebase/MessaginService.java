package com.example.myapplication.activitys.firebase;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.R;
import com.example.myapplication.activitys.activitys.ChatActivity;
import com.example.myapplication.activitys.model.Usuario;
import com.example.myapplication.activitys.util.Constant;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.util.Random;


public class MessaginService extends FirebaseMessagingService {


    @Override
    public void onNewToken(@NotNull String token) {

        super.onNewToken(token);
        Log.d("FMC", "Token" + token);
    }


    @Override
    public void onMessageReceived(@NotNull RemoteMessage message) {

        super.onMessageReceived(message);
        Usuario user = new Usuario();
        user.id = message.getData().get(Constant.KEY_USER_ID);
        user.nombre = message.getData().get(Constant.KEY_NAME);
        user.token = message.getData().get(Constant.KEY_FCM_TOKEN);

        int notification = new Random().nextInt();
        String chanelId = "chat_message";

        Intent intent = new Intent(this, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constant.KEY_USER, user);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, chanelId);
        builder.setSmallIcon(R.drawable.ic_notifications);
        builder.setContentTitle(user.nombre);
        builder.setContentTitle(message.getData().get(Constant.KEY_MESSAGE));
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(
                message.getData().get(Constant.KEY_MESSAGE)
        ));

        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelNane = "Chat Message";
            String channelDescription = "This notification channel is used for chat massage notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(chanelId, channelNane, importance);
            channel.setDescription(channelDescription);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(notification, builder.build());
    }


}
