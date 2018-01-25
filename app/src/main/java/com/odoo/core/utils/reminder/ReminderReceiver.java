/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 * <p/>
 * Created on 9/1/15 6:15 PM
 */
package com.odoo.core.utils.reminder;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.odoo.R;
import com.odoo.addons.events.EventTrackDetail;
import com.odoo.addons.events.models.EventTrack;
import com.odoo.core.account.BaseSettings;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.utils.OResource;
import com.odoo.core.utils.StringUtils;

public class ReminderReceiver extends BroadcastReceiver {
    public static final String TAG = ReminderReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String type = intent.getStringExtra(ReminderUtils.KEY_REMINDER_TYPE);
        showNotification(context, type, intent.getExtras());
    }

    private void showNotification(Context context, String type, Bundle data) {
        EventTrack track = new EventTrack(context);
        int track_id = data.getInt("track_id");
        ODataRow trackInfo = track.browse(track_id);

        if (type.equals("session") && BaseSettings.notifyForSession(context)) {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setSmallIcon(R.drawable.ic_odoo_o_white);
            builder.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            builder.setColor(Color.parseColor("#a24689"));

            String body = trackInfo.getString("name") + " : " +
                    StringUtils.htmlToString(data.getString("track_description"));

            /* Result Intent */
            Intent intent = new Intent(context, EventTrackDetail.class);
            Bundle extra = new Bundle();
            extra.putInt(EventTrackDetail.KEY_TRACK_ID, track_id);
            intent.putExtras(extra);

            builder.setContentTitle(OResource.string(context, R.string.upcoming_session_message));
            builder.setContentText(body);
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));
            builder.setSubText("By " + trackInfo.getString("speaker_name"));

            // Creating result pending intent
            PendingIntent resultPendingIntent = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(intent)
                    .getPendingIntent(track_id, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setFullScreenIntent(resultPendingIntent, true);
            builder.setContentIntent(resultPendingIntent);

            builder.setAutoCancel(true);
            builder.setSmallIcon(R.drawable.ic_odoo_o_white);

            NotificationManager notifyManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            notifyManager.notify("session_reminder", track_id, builder.build());

            Log.v(TAG, "Notification for reminder : #" + track_id + trackInfo.getString("name"));
        }
    }

}
