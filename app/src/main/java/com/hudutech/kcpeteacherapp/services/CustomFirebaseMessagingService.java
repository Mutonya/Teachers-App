package com.hudutech.kcpeteacherapp.services;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hudutech.kcpeteacherapp.R;
import com.hudutech.kcpeteacherapp.models.User;
import com.hudutech.kcpeteacherapp.ui.livechat.ChatActivity;

import java.util.Map;


public class CustomFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "CustomFirebaseMessaging";
    final String CHANNEL_ID = "kcpe_revision_nofitications";
    private Context context;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        context = getApplicationContext();
        if (remoteMessage.getData().size() > 0) {
            if (remoteMessage.getData().containsKey("fromUid") && remoteMessage.getData().get("type").equals("chat")) {
                sendNewMsgNotification(remoteMessage);
            }


        }

    }


    private void sendNewMsgNotification(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();

        final String title = data.get("title");
        final String message = data.get("body");
        final String fromUid = data.get("fromUid");

        //find the the sender and set the intent as toUser since this will be
        //our new recipient when the user opens the notification
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(fromUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {


                        Intent resultIntent = new Intent(this, ChatActivity.class);

                        resultIntent.putExtra("toUser", user);

                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                                this,
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );


                        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                // .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                                .setContentTitle(title)
                                .setContentText(message)
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(message))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setDefaults(NotificationCompat.DEFAULT_ALL)
                                .setAutoCancel(true);


                        int notificationId = (int) System.currentTimeMillis();
                        mBuilder.setSound(alarmSound);
                        mBuilder.setContentIntent(resultPendingIntent);


                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        if (manager != null) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                                        "New Message",
                                        NotificationManager.IMPORTANCE_DEFAULT);
                                channel.setDescription("Notification when there is a new message.");
                                channel.setShowBadge(true);
                                manager.createNotificationChannel(channel);

                            }

                            manager.notify(notificationId, mBuilder.build());
                        }
                    } else {
                        Log.d(TAG, "sendNewMsgNotification: Use is null ");
                    }

                }).addOnFailureListener(e -> {

            Log.e(TAG, "sendNewMsgNotification: ", e);

        });


    }


}
