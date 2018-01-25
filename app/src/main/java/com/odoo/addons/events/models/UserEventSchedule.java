package com.odoo.addons.events.models;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.utils.ODateUtils;
import com.odoo.core.utils.reminder.ReminderUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserEventSchedule extends OModel {
    public static final int REQUEST_SESSION_REMINDER = 123;
    OColumn track_id = new OColumn("Track", OInteger.class);
    OColumn date = new OColumn("Date", OVarchar.class);
    OColumn time = new OColumn("Time", OVarchar.class);

    public UserEventSchedule(Context context) {
        super(context, "user.event.schedule");
    }

    public boolean attending(int track_id) {
        return count("track_id = ?", new String[]{track_id + ""}) > 0;
    }

    public ODataRow getSession(String date, String time) {
        if (count("date = ? and time = ?", new String[]{date, time}) > 0) {
            EventTrack track = (EventTrack) createInstance(EventTrack.class);
            Cursor cr = executeRawQuery("select track_id from " + getTableName()
                    + " where date = ? and time = ?", new String[]{date, time});
            if (cr.moveToFirst()) {
                return track.browseSession(cr.getInt(0));
            }
        }
        return null;
    }

    public void updateForReminders() {
        Log.v(TAG, "Updating reminder for scheduled tracks");
        ReminderUtils reminderUtils = new ReminderUtils(getContext());
        List<Integer> trackIds = new ArrayList<>();

        // Getting user's all scheduled tracks
        for (ODataRow row : select()) {
            trackIds.add(row.getInt("track_id"));
        }

        // Getting other tracks
        EventTrack trackObj = new EventTrack(getContext());
        List<ODataRow> rows = trackObj.query("SELECT " + OColumn.ROW_ID + ", name FROM " + trackObj.getTableName() +
                        " WHERE partner_name != ? and room = ? and date != ?"
                , new String[]{"false", "false", "false"});
        for (ODataRow row : rows) {
            trackIds.add(row.getInt(OColumn.ROW_ID));
        }
        for (int id : trackIds) {
            ODataRow track = trackObj.browse(id);
            Date eventTime = ODateUtils.createDateObject(track.getString("date"),
                    ODateUtils.DEFAULT_FORMAT, false);
            // Setting reminder for notify sessions
            Bundle session_data = new Bundle();
            session_data.putInt("track_id", id);
            session_data.putBoolean("session_reminder", true);
            session_data.putString(ReminderUtils.KEY_REMINDER_TYPE, "session");
            session_data.putString("track_description", track.getString("description"));

            // setting reminder
            Date date = ODateUtils.getDateMinuteBefore(eventTime, 10);
            reminderUtils.setReminder(id, date, session_data);
            Log.v(TAG, "Setting reminder for: #" + id + track.getString("name"));
        }
    }

    public void attend(String trackDate, int track_id) {
        String date = ODateUtils.parseDate(trackDate,
                ODateUtils.DEFAULT_FORMAT, ODateUtils.DEFAULT_DATE_FORMAT);
        String time = ODateUtils.parseDate(trackDate,
                ODateUtils.DEFAULT_FORMAT, ODateUtils.DEFAULT_TIME_FORMAT);
        OValues values = new OValues();
        values.put("date", date);
        values.put("time", time);
        values.put("track_id", track_id);
        insertOrUpdate("date = ? and time = ?", new String[]{date, time}, values);
    }

    public void removeAttend(int track_id) {
        delete("track_id = ?", new String[]{track_id + ""}, true);
    }
}
