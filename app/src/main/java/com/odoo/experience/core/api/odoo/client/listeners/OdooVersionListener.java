package com.odoo.experience.core.api.odoo.client.listeners;

import com.odoo.experience.core.api.odoo.client.OdooVersion;

public interface OdooVersionListener {
    void onVersionLoad(OdooVersion version);
}
