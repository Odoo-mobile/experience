package com.odoo.experience.database.models;

import android.content.ContentValues;
import android.content.Context;

import com.odoo.experience.AppConfig;
import com.odoo.experience.core.api.odoo.client.helper.data.OdooRecord;
import com.odoo.experience.core.db.OModel;
import com.odoo.experience.core.db.ORecord;
import com.odoo.experience.core.db.types.OFieldChar;
import com.odoo.experience.core.db.types.OFieldDatetime;

import java.util.List;

public class EventEvent extends OModel {

    OFieldChar name = new OFieldChar("Name");
    OFieldDatetime date_begin = new OFieldDatetime("Date Begin").setDefault(false);
    OFieldDatetime date_end = new OFieldDatetime("Date End").setDefault(false);
    OFieldChar date_tz = new OFieldChar("Timezone");
    OFieldChar website_published = new OFieldChar("Website Published").setDefault("false");

    public EventEvent(Context context) {
        super(context, "event.event");
    }

    public String[] syncFields() {
        return new String[]{"name", "date_begin", "date_end", "date_tz", "website_published"};
    }

    @Override
    public ContentValues recordToValues(OdooRecord record) {
        ContentValues values = super.recordToValues(record);
        values.put("name", record.getString("name"));
        values.put("date_begin", record.getString("date_begin"));
        values.put("date_end", record.getString("date_end"));
        values.put("date_tz", record.getString("date_tz"));
        values.put("website_published", record.getString("website_published"));
        return values;
    }

    public String getStartDate() {
        List<ORecord> records = select("id = ?", new String[]{AppConfig.EVENT_ID + ""}, null);
        if (!records.isEmpty()) {
            return records.get(0).getString("date_begin");
        }
        return null;
    }
}
