/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 * <p/>
 * Created on 9/1/15 11:32 AM
 */
package com.odoo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.odoo.addons.events.models.UserEventSchedule;
import com.odoo.core.account.About;
import com.odoo.core.account.BaseSettings;
import com.odoo.core.utils.OAppBarUtils;

public class SettingsActivity extends AppCompatActivity {
    public static final String TAG = SettingsActivity.class.getSimpleName();
    public static final String ACTION_ABOUT = "com.odoo.ACTION_ABOUT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_setting_activity);
        OAppBarUtils.setAppBar(this, true);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setHomeButtonEnabled(true);
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setTitle(R.string.title_application_settings);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        if (intent.getAction() != null
                && intent.getAction().equals(ACTION_ABOUT)) {
            Intent about = new Intent(this, About.class);
            super.startActivity(about);
            return;
        }
        super.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void settingUpdated(Context context) {
        UserEventSchedule scheduleSession = new UserEventSchedule(context);
        if (BaseSettings.notifyForSession(context)) {
            scheduleSession.updateForReminders();
        }
    }
}
