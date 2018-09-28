package com.odoo.addons.events.models;

import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OText;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.utils.ODateUtils;
import com.odoo.datas.OConstants;

import java.util.ArrayList;

import odoo.helper.ODomain;

public class EventTrack extends OModel {

    public static final String AUTHORITY = "com.odoo.experience.core.provider.content.sync.event_track";
    public static final String DAY_FILTER = "day_filter";

    OColumn name = new OColumn("Title", OVarchar.class).setSize(100);
    OColumn event_id = new OColumn("Event", EventEvent.class, OColumn.RelationType.ManyToOne);
    OColumn date = new OColumn("Track Date", ODateTime.class);
    OColumn duration = new OColumn("Duration", OFloat.class);
    OColumn description = new OColumn("Track description", OText.class);

    // Visible only website published tracks.
    OColumn website_published = new OColumn("Website Published", OBoolean.class);

    OColumn tag_ids = new OColumn("Tags", EventTrackTag.class, OColumn.RelationType.ManyToMany);

    OColumn partner_biography = new OColumn("Author", OText.class).setDefaultValue("false");

    OColumn location_id = new OColumn("Room", EventTrackLocation.class, OColumn.RelationType.ManyToOne);
    @Odoo.Functional(store = true, depends = {"location_id"}, method = "storeRoom")
    OColumn room = new OColumn("Room", OVarchar.class).setLocalColumn();

    OColumn partner_id = new OColumn("Proposed by", ResPartner.class, OColumn.RelationType.ManyToOne);
    OColumn partner_name = new OColumn("Speaker Name", OVarchar.class);

    OColumn website_url = new OColumn("Website URL", OText.class);

    public EventTrack(Context context) {
        super(context, "event.track");
    }

    @Override
    public ODomain defaultDomain() {
        ODomain domain = new ODomain();
        domain.add("event_id", "=", OConstants.EVENT_ID);
        domain.add("website_published", "=", true);
        return domain;
    }

    public String storeRoom(OValues values) {
        if (!values.getString("location_id").equals("false")) {
            ArrayList<Object> location_id = (ArrayList<Object>) values.get("location_id");
            return location_id.get(1).toString();
        }
        return "false";
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }

    public Uri dayFilter(boolean isTalks, int day) {
        return uri().buildUpon().appendPath(DAY_FILTER).appendPath(day + "")
                .appendQueryParameter("is_talks", isTalks + "").build();
    }

    @Override
    public boolean checkForCreateDate() {
        return false;
    }

    public ODataRow browseSession(int track_id) {
        String[] projections = {OColumn.ROW_ID, "date as time",
                "partner_name", "date(date) as ddate",
                "duration", "name", "description", "room"};
        return browse(projections, track_id);
    }

    public Cursor getDayData(boolean isTalks, int day) {
        String[] projections = {OColumn.ROW_ID, "date as time",
                "partner_name", "date(date) as ddate",
                "duration", "name", "description", "room"};
        String sql;
        String date;
        if (isTalks && day == 0) {
            EventEvent eventEvent = new EventEvent(getContext());
            date = eventEvent.getEvent().getString("date_begin");
            date = ODateUtils.parseDate(date, ODateUtils.DEFAULT_FORMAT, ODateUtils.DEFAULT_DATE_FORMAT);
            sql = "SELECT " + TextUtils.join(", ", projections) +
                    " FROM " + getTableName() +
                    " WHERE date(date) < ?" +
                    " ORDER BY time(date)";
        } else {
            date = getDayDate(day);
            sql = "SELECT " + TextUtils.join(", ", projections) +
                    " FROM " + getTableName() +
                    " WHERE date(date) = ?" +
                    " GROUP BY time(date)" +
                    " ORDER BY time(date)";
        }
        return executeRawQuery(sql, new String[]{date});
    }

    public String getDayDate(int day) {
        EventEvent event = new EventEvent(getContext());
        return event.getEventDate(day);
    }

    @Override
    public void onSyncFinished(SyncResult result) {
        ExploreTracks tracks = new ExploreTracks(getContext());
        if (tracks.count(null, null) != count("partner_name = ?", new String[]{"false"}))
            tracks.generateTracks();
    }

}
