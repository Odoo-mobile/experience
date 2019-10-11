package com.odoo.experience.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.odoo.experience.AppConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DumpSyncIntent extends IntentService {
    public static final String TAG = DumpSyncIntent.class.getSimpleName();
    public static final String ACTION = DumpSyncIntent.class.getCanonicalName();
    public static final String KEY_STATUS = "status";
    public static final String KEY_SYNC_DONE = "sync_done";


    public DumpSyncIntent() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        syncDump();
    }

    private boolean syncDump() {
        if (AppConfig.APPLY_DB_DUMP) {
            Log.v(TAG, "Applying Database Dump");
            AssetManager assetManager = getApplicationContext().getResources().getAssets();
            File databases = new File(getFilesDir().getParent() + File.separator + "databases");
            boolean hasDBDIR = databases.exists();
            if (!databases.exists()) {
                hasDBDIR = databases.mkdir();
            }
            String dbPath = databases.getPath() + File.separator + AppConfig.DB_NAME;
            if (!new File(dbPath).exists()) {
                try {
                    Thread.sleep(1000);
                    InputStream in = assetManager.open("dump" + File.separator +
                            "odooxp_" + AppConfig.EVENT_ID + ".db");
                    OutputStream out = new FileOutputStream(dbPath);
                    byte[] buffer = new byte[65536 * 2];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    out.flush();
                    in.close();
                    out.close();
                    Log.v(TAG, "Database dump applied successfully.");
                    sendDoneSingle();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sendDoneSingle();
            }
        }
        return false;
    }

    private void sendDoneSingle() {
        SharedPreferences preferences = getSharedPreferences("dump_sync", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(AppConfig.KEY_DB_SETUP_DONE, true);
        editor.apply();
        Intent intent = new Intent(ACTION);
        intent.setAction(ACTION);
        intent.putExtra(KEY_SYNC_DONE, true);
        LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(intent);
    }
}
