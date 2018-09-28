package com.odoo.experience.core.api.odoo.client.helper;

import com.odoo.experience.core.api.odoo.client.helper.data.OdooError;
import com.odoo.experience.core.api.odoo.client.helper.data.OdooResult;

public class OdooResponse {
    public int id;
    public float jsonrpc;
    public OdooResult result;
    public OdooError error;
}
