package com.odoo.experience.core.api.odoo.client.listeners;

import com.odoo.experience.core.api.odoo.OdooUser;
import com.odoo.experience.core.api.odoo.client.AuthError;

public interface AuthenticateListener {
    void onLoginSuccess(OdooUser user);

    void onLoginFail(AuthError error);
}
