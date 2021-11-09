package com.example.taskit;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final String CHANNEL_ID_1 = "channel1";
    public static int RQST_CODE = 1;
    public static final String PREFS_NAME = "REQUEST_LOG_FILE";

    @Override
    public void onCreate() {
        super.onCreate();

     createNotificationChannel();


    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence title = getString(R.string.channel_name);
            String description = getString(R.string.your_tasks);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_1, title, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }
}
