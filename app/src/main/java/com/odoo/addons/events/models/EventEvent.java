package com.odoo.addons.events.models;

import android.content.Context;
import android.database.Cursor;

import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.utils.OCursorUtils;
import com.odoo.core.utils.ODateUtils;
import com.odoo.datas.OConstants;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import odoo.helper.ODomain;

public class EventEvent extends OModel {

    public static ODataRow event = null;
    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn date_begin = new OColumn("Start Date", ODateTime.class);
    OColumn date_end = new OColumn("End date", ODateTime.class);
    OColumn date_tz = new OColumn("Timezone", OVarchar.class);

    public EventEvent(Context context) {
        super(context, "event.event");
    }

    @Override
    public ODomain defaultDomain() {
        ODomain domain = new ODomain();
        domain.add("id", "=", OConstants.EVENT_ID);
        return domain;
    }

    @Override
    public boolean checkForCreateDate() {
        return false;
    }

    public String getEventName() {
        return getEvent().getString("name");
    }

    /**
     * Find the current day number based on the current date, start date and end date of the event
     *
     * @return day number of the event
     * (or -1 if current date not between start and end date of the event)
     */
    public int getDayNumber() {
        ODataRow event = getEvent();
        Date startDate = ODateUtils.createDateObject(event.getString("date_begin"),
                ODateUtils.DEFAULT_FORMAT, false);
        Date endDate = ODateUtils.createDateObject(event.getString("date_end"),
                ODateUtils.DEFAULT_FORMAT, false);
        Date now = new Date();
        if (now.compareTo(startDate) >= 0 && now.compareTo(endDate) <= 0)
            return ODateUtils.daysBetween(startDate, now) + 1; // If on start day, it will produce 0. so adding +1 to each value for getting the day number
        return -1;
    }

    public int getDayNumber(String date) {
        ODataRow event = getEvent();
        Date startDate = ODateUtils.createDateObject(event.getString("date_begin"),
                ODateUtils.DEFAULT_FORMAT, false);
        Date endDate = ODateUtils.createDateObject(event.getString("date_end"),
                ODateUtils.DEFAULT_FORMAT, false);

        Date now = ODateUtils.createDateObject(date, ODateUtils.DEFAULT_FORMAT, false);
        if (now.compareTo(startDate) >= 0 && now.compareTo(endDate) <= 0)
            return ODateUtils.daysBetween(startDate, now) + 1; // If on start day, it will produce 0. so adding +1 to each value for getting the day number
        return -1;
    }

    public String getEventDisplayDates() {
        ODataRow event = getEvent();
        PrettyTime time = new PrettyTime(new Date(), Locale.ENGLISH);
        Date startDate = ODateUtils.createDateObject(event.getString("date_begin"),
                ODateUtils.DEFAULT_FORMAT, false);
        return time.format(startDate);
    }

    public String getDaysDisplayName() {
        ODataRow event = getEvent();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
        String month = dateFormat.format(ODateUtils.createDateObject(event.getString("date_begin"), ODateUtils.DEFAULT_FORMAT, false));
        String startDay = ODateUtils.parseDate(event.getString("date_begin"), ODateUtils.DEFAULT_FORMAT, "dd");
        String endDay = ODateUtils.parseDate(event.getString("date_end"), ODateUtils.DEFAULT_FORMAT, "dd");
        return String.format(Locale.ENGLISH, "%s %s-%s", month, startDay, endDay);
    }

    public int getTotalDays() {
        ODataRow event = getEvent();
        Date startDate = ODateUtils.createDateObject(event.getString("date_begin"),
                ODateUtils.DEFAULT_FORMAT, false);
        Date endDate = ODateUtils.createDateObject(event.getString("date_end"),
                ODateUtils.DEFAULT_FORMAT, false);
        Calendar date = Calendar.getInstance();
        date.setTime(startDate);
        int daysBetween = 0;
        while (date.getTime().before(endDate)) {
            date.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }

    public boolean hasTalksTrainingDay() {
        // Adding one more day for training talks before event begins
        // Labeled as Talks/Training
        ODataRow event = getEvent();
        EventTrack track = new EventTrack(getContext());
        return track.count("date < ?", new String[]{event.getString("date_begin")}) > 0;
    }

    public String getEventDate(int day) {
        ODataRow event = getEvent();
        Date startDate = ODateUtils.createDateObject(event.getString("date_begin"),
                ODateUtils.DEFAULT_FORMAT, false);
        int dayNumber = day - 1; // If first day, we need start day
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + dayNumber);
        return ODateUtils.getDate(cal.getTime(), ODateUtils.DEFAULT_DATE_FORMAT);
    }

    public int getEventYear() {
        ODataRow event = getEvent();
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTime(ODateUtils.createDateObject(event.getString("date_begin"),
                ODateUtils.DEFAULT_FORMAT, false));
        return cal.get(Calendar.YEAR);
    }

    public ODataRow getEvent() {
        Cursor cr = executeRawQuery("select * from " + getTableName() + " WHERE id = ?",
                new String[]{OConstants.EVENT_ID + ""});
        if (cr.moveToFirst()) {
            return OCursorUtils.toDatarow(cr);
        }
        return new ODataRow();
    }

    public String getEventTimeZone() {
        return getEvent().getString("date_tz");
    }
}