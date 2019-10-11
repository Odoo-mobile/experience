package com.odoo.experience.ui.schedule.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.odoo.experience.AppConfig;
import com.odoo.experience.R;
import com.odoo.experience.core.db.ORecord;
import com.odoo.experience.database.models.EventTracks;
import com.odoo.experience.ui.schedule.ScheduleDetail;

public class SessionReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "odooxp_" + AppConfig.EVENT_YEAR;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        createNotificationChannel(context);
        notifySession(context, new EventTracks(context).get(data.getInt("track_id")));
    }

    private void notifySession(Context context, ORecord record) {
        String title = context.getString(R.string.title_upcoming_session_in_10_min);
        String content = record.getString("name");

        NotificationCompat.BigTextStyle largeContent = new NotificationCompat.BigTextStyle()
                .bigText(context.getString(R.string.track_name_by,
                        record.getString("name"), record.getString("partner_name")));


        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(context, ScheduleDetail.class);
        intent.putExtra(ScheduleDetail.KEY_TRACK_ID, record.getInt("id"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, record.getInt("id"), intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_odoox_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setStyle(largeContent)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(record.getInt("id"), mBuilder.build());
    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }
}
