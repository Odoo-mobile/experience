package com.odoo.experience.core.api.odoo.client.helper.data;

import com.google.gson.internal.LinkedTreeMap;

public class OdooError extends OdooResult {


    public OdooError getErrorData() {
        if (isValidValue("data")) {
            LinkedTreeMap map = (LinkedTreeMap) get("data");
            OdooError error = new OdooError();
            error.putAll(map);
            return error;
        }
        return null;
    }

}
