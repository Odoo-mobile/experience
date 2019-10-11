package com.odoo.experience.database.models;

import android.content.ContentValues;
import android.content.Context;

import com.odoo.experience.core.api.odoo.client.helper.data.OdooRecord;
import com.odoo.experience.core.db.OModel;
import com.odoo.experience.core.db.types.OFieldChar;
import com.odoo.experience.core.db.types.OFieldInteger;

public class EventTrackTags extends OModel {

    OFieldChar name = new OFieldChar("Name");
    OFieldInteger color = new OFieldInteger("Color").setDefault(0);

    public EventTrackTags(Context context) {
        super(context, "event.track.tag");
    }

    @Override
    public String[] syncFields() {
        return new String[]{"id", "name", "color"};
    }

    @Override
    public ContentValues recordToValues(OdooRecord record) {
        ContentValues contentValues = super.recordToValues(record);
        contentValues.put("name", record.getString("name"));
        contentValues.put("color", record.getInt("color", 0));
        return contentValues;
    }
}
