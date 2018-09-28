package com.odoo.experience;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.odoo.experience.core.helper.OdooActivity;
import com.odoo.experience.core.utils.OBind;
import com.odoo.experience.services.DumpSyncIntent;
import com.odoo.experience.services.OdooDataService;

public class SplashScreen extends OdooActivity implements View.OnClickListener {

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        startService(new Intent(this, OdooDataService.class));
        preferences = getApplication().getSharedPreferences("app_launch_status_v2", MODE_PRIVATE);
        if (!preferences.getBoolean("is_first_launch_v2", true)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        findViewById(R.id.btnGetStarted).setOnClickListener(this);
        if (AppConfig.APPLY_DB_DUMP) {
            OBind.setText(findViewById(R.id.btnGetStarted), getString(R.string.label_please_wait));
            findViewById(R.id.btnGetStarted).setEnabled(false);
            startService(new Intent(this, DumpSyncIntent.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(dbApplyReceiver, new IntentFilter(DumpSyncIntent.ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(dbApplyReceiver);
    }

    @Override
    public void onClick(View view) {
        preferences.edit().putBoolean("is_first_launch_v2", false).apply();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private BroadcastReceiver dbApplyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            OBind.setText(findViewById(R.id.btnGetStarted), getString(R.string.label_get_started));
            findViewById(R.id.btnGetStarted).setEnabled(true);

        }
    };
}
