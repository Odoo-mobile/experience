package com.odoo.addons.events.providers;

import android.database.Cursor;
import android.net.Uri;

import com.odoo.addons.events.models.ExploreTracks;
import com.odoo.core.orm.provider.BaseModelProvider;

import java.util.Locale;

public class EventTrackExploreProvider extends BaseModelProvider {
    public static final int TAG_FILTER_CODE = 543;

    @Override
    public boolean onCreate() {
        ExploreTracks track = new ExploreTracks(getContext());
        String path = track.getModelName().toLowerCase(Locale.getDefault());
        matcher.addURI(authority(), path + "/" + ExploreTracks.TAG_FILTER_PATH + "/#", TAG_FILTER_CODE);
        return super.onCreate();
    }

    @Override
    public String authority() {
        return ExploreTracks.AUTHORITY;
    }

    @Override
    public Cursor query(Uri uri, String[] base_projection, String selection, String[]
            selectionArgs, String sortOrder) {
        int match = matcher.match(uri);
        ExploreTracks track = new ExploreTracks(getContext());
        switch (match) {
            case TAG_FILTER_CODE:
                return track.getFilteredTrack(selection, selectionArgs, Integer.parseInt(uri.getLastPathSegment()));
            default:
                return super.query(uri, base_projection, selection, selectionArgs, sortOrder);
        }
    }
}
