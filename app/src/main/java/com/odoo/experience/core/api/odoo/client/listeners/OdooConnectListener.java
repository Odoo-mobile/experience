package com.odoo.experience.core.api.odoo.client.listeners;

import com.odoo.experience.core.api.odoo.client.OdooVersion;

public interface OdooConnectListener {
    void onConnected(OdooVersion version);
}
