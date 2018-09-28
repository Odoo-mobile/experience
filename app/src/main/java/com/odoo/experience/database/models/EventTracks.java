package com.odoo.experience.database.models;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.odoo.experience.core.api.odoo.client.helper.data.OdooRecord;
import com.odoo.experience.core.db.OModel;
import com.odoo.experience.core.db.types.OFieldBlob;
import com.odoo.experience.core.db.types.OFieldChar;
import com.odoo.experience.core.db.types.OFieldDatetime;
import com.odoo.experience.core.db.types.OFieldFloat;
import com.odoo.experience.core.db.types.OFieldInteger;
import com.odoo.experience.core.db.types.OFieldText;

import java.util.ArrayList;
import java.util.List;

public class EventTracks extends OModel {

    OFieldChar name = new OFieldChar("Name");
    OFieldInteger user_id = new OFieldInteger("User Id");
    OFieldChar user_name = new OFieldChar("User Name");
    OFieldInteger partner_id = new OFieldInteger("partner id").setDefault(0);
    OFieldChar partner_name = new OFieldChar("Partner");
    OFieldChar partner_email = new OFieldChar("Partner Email");

    OFieldInteger location_id = new OFieldInteger("Room Id");
    OFieldChar location_name = new OFieldChar("Room Name");
    OFieldFloat duration = new OFieldFloat("Duration").setDefault(0.0F);

    OFieldDatetime date = new OFieldDatetime("Date");
    OFieldText description = new OFieldText("Description");

    OFieldInteger stage_id = new OFieldInteger("Stage Id");
    OFieldChar stage_name = new OFieldChar("Stage");
    OFieldBlob image = new OFieldBlob("Image").setDefault("false");
    OFieldText website_url = new OFieldText("Website URL");
    OFieldInteger selected = new OFieldInteger("Selected (Starred) track").setDefault(0);

    EventTrackTagsRel tagsRel;

    public EventTracks(Context context) {
        super(context, "event.track");
        tagsRel = new EventTrackTagsRel(getContext());
    }

    public String[] syncFields() {
        return new String[]{
                "id", "user_id", "partner_id", "name", "partner_name", "partner_email", "location_id", "tag_ids",
                "duration", "date", "write_date", "create_date", "stage_id", "description", "image", "website_url"
        };
    }

    @Override
    public ContentValues recordToValues(OdooRecord record) {
        ContentValues values = super.recordToValues(record);
        values.put("name", record.getString("name"));
        if (!record.getString("user_id").equals("false")) {
            List<Object> user_id = record.getArray("user_id");
            values.put("user_id", Double.valueOf(user_id.get(0).toString()).intValue());
            values.put("user_name", user_id.get(1).toString());
        }
        if (!record.getString("partner_id").equals("false")) {
            List<Object> partner_id = record.getArray("partner_id");
            values.put("partner_id", Double.valueOf(partner_id.get(0).toString()).intValue());
        }

        values.put("partner_name", record.getString("partner_name"));
        values.put("partner_email", record.getString("partner_email"));
        values.put("website_url", record.getString("website_url"));
        if (!record.getString("location_id").equals("false")) {
            List<Object> location_id = record.getArray("location_id");
            values.put("location_id", Double.valueOf(location_id.get(0).toString()).intValue());
            values.put("location_name", location_id.get(1).toString());
        }
        values.put("duration", record.getFloat("duration"));
        values.put("date", record.getString("date"));
        values.put("description", record.getString("description"));
        values.put("image", record.getString("image"));
        if (!record.getString("stage_id").equals("false")) {
            List<Object> stage_id = record.getArray("stage_id");
            values.put("stage_id", Double.valueOf(stage_id.get(0).toString()).intValue());
            values.put("stage_name", stage_id.get(1).toString());
        }

        List<Double> tag_ids = record.getArray("tag_ids");
        for (Double tagId : tag_ids) {
            ContentValues tagRelVal = new ContentValues();
            tagRelVal.put("track_id", record.getInt("id"));
            tagRelVal.put("tag_id", tagId.intValue());
            tagsRel.create(tagRelVal);
        }
        return values;
    }

    public boolean isStarred(int id) {
        return count("id = ? and selected = ?", id + "", "1") > 0;
    }
}
