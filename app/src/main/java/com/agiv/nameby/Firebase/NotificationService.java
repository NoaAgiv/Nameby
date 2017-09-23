package com.agiv.nameby.Firebase;

import android.content.Intent;

import android.support.v4.content.LocalBroadcastManager;

import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


/**
 * Created by Noa Agiv on 9/3/2017.
 */

public class NotificationService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    public static final String MATCH_NOTIFICATION = "match_notification";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
//        Log.d(TAG, "notification obj: " + remoteMessage.getData());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        final String body = remoteMessage.getNotification().getBody();


        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(getBaseContext());

        Intent intent = new Intent(MATCH_NOTIFICATION);
//        intent.putExtra("message", "a");
//        intent.putExtra("key2", "b");
        broadcaster.sendBroadcast(intent); //now goes to MainActivity's BroadcastReciever's onReceive()

//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.post(new Runnable() {
//            public void run() {
//                Toast.makeText(getApplicationContext(), body, Toast.LENGTH_LONG).show();
//            }
//        });

    }
}
