package com.odoo.experience.database.models;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.odoo.experience.core.api.odoo.client.helper.data.OdooRecord;
import com.odoo.experience.core.db.OModel;
import com.odoo.experience.core.db.types.OFieldBlob;
import com.odoo.experience.core.db.types.OFieldChar;
import com.odoo.experience.core.db.types.OFieldInteger;

import java.util.Arrays;
import java.util.List;

public class EventSponsors extends OModel {

    OFieldChar url = new OFieldChar("Sponsor URL");
    OFieldChar partner_name = new OFieldChar("Partner Name");
    OFieldInteger sponsor_type_id = new OFieldInteger("Sponsor Type Id");
    OFieldChar sponsor_type_name = new OFieldChar("Sponsor Type Name");
    OFieldInteger event_id = new OFieldInteger("Event Id");
    OFieldChar event_name = new OFieldChar("Event Name");
    OFieldInteger sequence = new OFieldInteger("Sequence").setDefault(0);
    OFieldBlob image_medium = new OFieldBlob("Image").setDefault("false");

    public EventSponsors(Context context) {
        super(context, "event.sponsor");
    }

    public String[] syncFields() {
        return new String[]{
                "id", "url", "partner_id", "sponsor_type_id", "event_id", "sequence", "image_medium",
                "write_date", "create_date"
        };
    }

    @Override
    public ContentValues recordToValues(OdooRecord record) {
        ContentValues values = super.recordToValues(record);
        values.put("url", record.getString("url"));
        values.put("sequence", record.getInt("sequence"));

        List<Object> partner_id = record.getArray("partner_id");
        values.put("partner_name", partner_id.get(1).toString());

        List<Object> sponsor_type_id = record.getArray("sponsor_type_id");
        values.put("sponsor_type_id", Double.valueOf(sponsor_type_id.get(0).toString()).intValue());
        values.put("sponsor_type_name", sponsor_type_id.get(1).toString());

//        List<Object> event_id = record.getArray("event_id");
//        values.put("event_id", Double.valueOf(event_id.get(0).toString()).intValue());
//        values.put("event_name", event_id.get(1).toString());

        values.put("image_medium", record.getString("image_medium"));
        return values;
    }
}
