package com.example.mvchistv.proyectodetesis;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;
import java.util.Random;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

Servicio servicio=new Servicio();

    private static final String TAG =  "FirebaseMessagingService";



    @SuppressLint("LongLogTag")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: "+remoteMessage.getFrom());
        servicio.detenerRunnable();
        servicio.Consulta();
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
           Intent intent=new Intent(this,Login.class);
           intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
           startActivity(intent);

            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }


        sendNotification(remoteMessage.getData());

    }
    private void sendNotification(Map<String,String> messageBody) {

        String channelId = "my_channel_1";

        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       final int not_nu=generateRandom();
        PendingIntent pendingIntent = PendingIntent.getActivity(this,not_nu, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(messageBody.get("message"));
        bigText.setBigContentTitle(messageBody.get("title"));
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setContentTitle(messageBody.get("title"))
                .setContentText(messageBody.get("message"))
                .setAutoCancel(true)
                .setStyle(bigText)
                .setSound(defaultSoundUri)
                .setChannelId(channelId)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2, notificationBuilder.build());


    }



    public int generateRandom(){
        Random random = new Random();
        return random.nextInt(9999 - 1000) + 1000;
    }
}
