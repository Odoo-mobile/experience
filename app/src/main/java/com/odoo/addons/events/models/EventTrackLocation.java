package com.odoo.addons.events.models;

import android.content.Context;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OVarchar;

public class EventTrackLocation extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);

    public EventTrackLocation(Context context) {
        super(context, "event.track.location");
    }
}
