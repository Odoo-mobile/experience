package com.odoo.addons.events.models;

import android.content.Context;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OVarchar;

import odoo.helper.ODomain;

public class EventTrackTag extends OModel {

    OColumn name = new OColumn("TAG", OVarchar.class);

    public EventTrackTag(Context context) {
        super(context, "event.track.tag");
    }

    @Override
    public boolean checkForCreateDate() {
        return false;
    }

    @Override
    public ODomain defaultDomain() {
        ODomain domain = new ODomain();
        domain.add("id", "in", getServerIds());
        return domain;
    }
}
