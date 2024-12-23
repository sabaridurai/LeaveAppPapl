package com.example.leaveapplication;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

public class pushnotificationsender {
    public static void sendNotification(String receiverDeviceToken, String messageTitle, String messageBody) {

        // Create the FCM message payload
        RemoteMessage message = new RemoteMessage.Builder(receiverDeviceToken)
                .setMessageId(Integer.toString(getRandomMessageId()))
                .addData("title", messageTitle)
                .addData("body", messageBody)
                .build();

        // Send the FCM message
        FirebaseMessaging.getInstance().send(message);
    }

    private static int getRandomMessageId() {
        return (int) (Math.random() * 100000);
    }
}