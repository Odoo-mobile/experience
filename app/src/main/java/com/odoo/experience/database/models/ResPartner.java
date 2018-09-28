package com.odoo.experience.database.models;

import android.content.ContentValues;
import android.content.Context;

import com.odoo.experience.core.api.odoo.client.helper.data.OdooRecord;
import com.odoo.experience.core.db.OModel;
import com.odoo.experience.core.db.types.OFieldChar;

public class ResPartner extends OModel {

    OFieldChar name = new OFieldChar("Name");
    OFieldChar website_description = new OFieldChar("Website Description");
    OFieldChar function = new OFieldChar("Function");

    public ResPartner(Context context) {
        super(context, "res.partner");
    }

    @Override
    public String[] syncFields() {
        return new String[]{"name", "website_description", "function"};
    }

    @Override
    public ContentValues recordToValues(OdooRecord record) {
        ContentValues contentValues = super.recordToValues(record);
        contentValues.put("name", record.getString("name"));
        contentValues.put("website_description", record.getString("website_description"));
        contentValues.put("function", record.getString("function"));
        return contentValues;
    }
}
