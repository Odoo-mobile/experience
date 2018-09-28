package com.odoo.experience.core.api.odoo.client.listeners;

import com.odoo.experience.core.api.odoo.client.builder.data.OdooRecords;

public interface IOdooRecords {

    void onRecords(OdooRecords records);
}
