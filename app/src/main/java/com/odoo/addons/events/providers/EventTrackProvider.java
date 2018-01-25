package com.odoo.addons.events.providers;

import android.database.Cursor;
import android.net.Uri;

import com.odoo.addons.events.models.EventTrack;
import com.odoo.core.orm.provider.BaseModelProvider;

import java.util.Locale;

public class EventTrackProvider extends BaseModelProvider {
    public static final int DAY_FILTER_CODE = 544;

    @Override
    public boolean onCreate() {
        EventTrack track = new EventTrack(getContext());
        String path = track.getModelName().toLowerCase(Locale.getDefault());
        matcher.addURI(authority(), path + "/" + EventTrack.DAY_FILTER + "/#", DAY_FILTER_CODE);
        return super.onCreate();
    }

    @Override
    public String authority() {
        return EventTrack.AUTHORITY;
    }

    @Override
    public Cursor query(Uri uri, String[] base_projection, String selection, String[]
            selectionArgs, String sortOrder) {
        int match = matcher.match(uri);
        EventTrack track = new EventTrack(getContext());
        switch (match) {
            case DAY_FILTER_CODE:
                boolean is_talks = Boolean.parseBoolean(uri.getQueryParameter("is_talks"));
                return track.getDayData(is_talks, Integer.parseInt(uri.getLastPathSegment()));
            default:
                return super.query(uri, base_projection, selection, selectionArgs, sortOrder);
        }
    }

}
