package com.example.taskit.Service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.taskit.Activities.MainActivity;
import com.example.taskit.R;

import static com.example.taskit.App.CHANNEL_ID_1;
import static com.example.taskit.App.RQST_CODE;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();

        assert extras != null;
        String title = extras.getString("title");
        String description = extras.getString("description");


        Intent intentMain = new Intent(context, MainActivity.class);
        intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentMain, 0);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, CHANNEL_ID_1)
                .setSmallIcon(R.drawable.ic_check_box_black_24dp)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(RQST_CODE, notification.build());
    }

}
