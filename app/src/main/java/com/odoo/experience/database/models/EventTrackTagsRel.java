package com.odoo.experience.database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.odoo.experience.core.db.M2MHelper;
import com.odoo.experience.core.db.OModel;
import com.odoo.experience.core.db.ORecord;
import com.odoo.experience.core.db.types.OFieldInteger;

import java.util.ArrayList;
import java.util.List;

public class EventTrackTagsRel extends OModel implements M2MHelper {

    OFieldInteger track_id = new OFieldInteger("Track");
    OFieldInteger tag_id = new OFieldInteger("Tag Id");

    public EventTrackTagsRel(Context context) {
        super(context, "event.track.tags");
    }

    @Override
    public void addRel(int track_id, int tag_id) {
        ContentValues values = new ContentValues();
        values.put("track_id", track_id);
        values.put("tag_id", tag_id);
        if (count("track_id = ? and tag_id = ?", track_id + "", tag_id + "") > 0) {
            update(values, "track_id = ? and tag_id = ?", track_id + "", tag_id + "");
        } else {
            create(values);
        }
    }

    @Override
    public List<ORecord> getRelRecords(int track_id) {
        List<ORecord> records = new ArrayList<>();
        EventTrackTags tags = new EventTrackTags(getContext());

        for (ORecord record : select(new String[]{"distinct tag_id as tag_id"},
                "track_id = ?", new String[]{track_id + ""}, null)) {
            ORecord tag = tags.get(record.getInt("tag_id"));
            if (tag != null) records.add(tag);
        }
        return records;
    }

    @Override
    public String[] syncFields() {
        return new String[0];
    }

    public List<Integer> getTrackIds(List<Integer> tagIds) {
        List<Integer> records = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cr = db.query(getTableName(), new String[]{"track_id"}, "tag_id IN (" + TextUtils.join(", ", tagIds) + ")",
                new String[]{}, null, null, null);
        if (cr != null && cr.moveToFirst()) {
            do {
                records.add(cr.getInt(0));
            } while (cr.moveToNext());
            cr.close();
        }
        db.close();
        return records;
    }
}
