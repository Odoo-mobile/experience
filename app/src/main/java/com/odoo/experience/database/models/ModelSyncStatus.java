package com.odoo.experience.database.models;

import android.content.ContentValues;
import android.content.Context;

import com.odoo.experience.core.db.OModel;
import com.odoo.experience.core.db.ORecord;
import com.odoo.experience.core.db.types.OFieldChar;
import com.odoo.experience.core.db.types.OFieldDatetime;

import java.util.List;

public class ModelSyncStatus extends OModel {

    OFieldChar model = new OFieldChar("Model Name");
    OFieldDatetime last_sync_datetime = new OFieldDatetime("Sync Date Time");

    public ModelSyncStatus(Context context) {
        super(context, "model.sync.status");
    }

    public String getSyncDate(String model) {
        List<ORecord> records = select("model  = ? ", new String[]{model}, null);
        if (!records.isEmpty()) {
            return records.get(0).getString("last_sync_datetime");
        }
        return null;
    }

    public void setSyncDate(String model, String syncDate) {
        ContentValues values = new ContentValues();
        values.put("model", model);
        values.put("last_sync_datetime", syncDate);
        if (count("model = ?", model) > 0) {
            update(values, "model = ? ", model);
        } else {
            create(values);
        }
    }


    @Override
    public String[] syncFields() {
        return new String[0];
    }
}
