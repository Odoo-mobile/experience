package com.odoo.experience.ui.schedule.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.AlarmManagerCompat;
import android.util.Log;

import com.odoo.experience.core.utils.ODateUtils;

import java.util.Calendar;
import java.util.Date;

public class SessionReminder {

    private Context mContext;
    private AlarmManager alarmManager;

    public SessionReminder(Context context) {
        mContext = context;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void setReminder(int requestId, String dateInUTC, Bundle extra) {
        _setCancelReminder(requestId, dateInUTC, extra, false);
    }

    public void cancelReminder(int requestId, String dateInUTC, Bundle extra) {
        _setCancelReminder(requestId, dateInUTC, extra, true);
    }

    private void _setCancelReminder(int requestId, String dateInUTC, Bundle extra, boolean toCancel) {
        Intent resultIntent = new Intent(mContext, SessionReminderReceiver.class);
        resultIntent.putExtras(extra);
        PendingIntent intent = PendingIntent.getBroadcast(mContext, requestId, resultIntent, toCancel ?
                PendingIntent.FLAG_CANCEL_CURRENT : 0);
        Date date = ODateUtils.createDateObject(dateInUTC, ODateUtils.DEFAULT_FORMAT, false);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, -10);
        if (toCancel) {
            alarmManager.cancel(intent);
        } else {
            AlarmManagerCompat.setExact(alarmManager, AlarmManager.RTC_WAKEUP, calendar.getTime().getTime(), intent);
        }
    }
}
