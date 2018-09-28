package com.odoo.addons.events.models;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OText;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.utils.OCursorUtils;
import com.odoo.core.utils.StringUtils;

public class ExploreTracks extends OModel {
    public static final String TAG_FILTER_PATH = "tag_filter";
    public static final String ACTION_DATA_UPDATE = "data_updated";
    public static final String AUTHORITY = "com.odoo.experience.core.provider.content.sync.event_track_explore";
    OColumn name = new OColumn("Title", OVarchar.class).setSize(100).setDefaultValue(false);
    OColumn description = new OColumn("Description", OText.class).setDefaultValue(false);
    OColumn speakers = new OColumn("Speaker", OVarchar.class).setSize(150).setDefaultValue(false);
    OColumn room = new OColumn("Room", OVarchar.class).setSize(100).setDefaultValue(false);
    OColumn location_id = new OColumn("Location ID", OInteger.class).setDefaultValue(0);
    OColumn track_time = new OColumn("Time", ODateTime.class);
    OColumn duration = new OColumn("Duration", OFloat.class).setDefaultValue(0.0F);
    OColumn is_collapsible = new OColumn("Collapsible", OBoolean.class).setDefaultValue(false);
    OColumn liked = new OColumn("Liked", OBoolean.class).setDefaultValue(false).setLocalColumn();

    public ExploreTracks(Context context) {
        super(context, "explore.tracks");
    }


    public void generateTracks() {
        Log.i(TAG, "Updating exploring tracks");
        delete(null, null, true);
        EventTrack track = new EventTrack(getContext());
        String sql = "SELECT date as track_time FROM " + track.getTableName() +
                " WHERE partner_name != ?" +
                " GROUP BY date";
        Cursor cr = track.executeRawQuery(sql, new String[]{"false"});
        if (cr.moveToFirst()) {
            do {
                ODataRow data = OCursorUtils.toDatarow(cr);
                data.put("is_collapsible", true);
                insert(data.toValues());
            } while (cr.moveToNext());
        }
        String[] projections = {OColumn.ROW_ID, "name", "description",
                "partner_name as speakers", "room", "location_id", "duration", "date as track_time"};

        // Inserting other records in explore table
        String insertSQL = "INSERT INTO " + getTableName() +
                " (id, name, description, speakers, room, location_id, duration, track_time)";
        insertSQL += " SELECT " + TextUtils.join(", ", projections);
        insertSQL += " FROM " + track.getTableName() + " WHERE (partner_name != ? or partner_biography != ?)";
        query(insertSQL, new String[]{"false", "false"});
        Log.i(TAG, "Updated exploring tracks: " + count(null, null));

        Intent dataUpdated = new Intent(ACTION_DATA_UPDATE);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(dataUpdated);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }

    public Uri tagFilterUri(int size) {
        return uri().buildUpon().appendPath(TAG_FILTER_PATH).appendPath(size + "").build();
    }

    public Cursor getFilteredTrack(String where, String[] args, int size) {
        String query = "SELECT * FROM explore_tracks WHERE " +
                "id in ( SELECT e_track._id FROM event_track as e_track " +
                "LEFT JOIN event_track_event_track_tag_rel as rel " +
                "ON rel.event_track_id = e_track._id " +
                "WHERE rel.event_track_tag_id in (" + StringUtils.repeat("?, ", size - 1) + " ?) " +
                ") AND " + where + " ORDER BY track_time";
        return executeRawQuery(query, args);
    }

}
