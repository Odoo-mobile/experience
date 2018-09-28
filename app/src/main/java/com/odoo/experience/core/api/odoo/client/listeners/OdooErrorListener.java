package com.odoo.experience.core.api.odoo.client.listeners;

import com.odoo.experience.core.api.odoo.client.helper.OdooErrorException;

public interface OdooErrorListener {
    void onError(OdooErrorException error);
}
