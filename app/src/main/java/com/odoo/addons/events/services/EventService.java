package com.odoo.addons.events.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SyncResult;
import android.content.res.AssetManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.odoo.addons.events.models.EventEvent;
import com.odoo.addons.events.models.EventTrack;
import com.odoo.addons.events.models.EventTrackTag;
import com.odoo.addons.sponsor.models.EventSponsor;
import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.account.OdooLogin;
import com.odoo.core.orm.OModel;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.utils.OPreferenceManager;
import com.odoo.datas.OConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import odoo.Odoo;

public class EventService extends IntentService {
    public static final String TAG = EventService.class.getSimpleName();
    public static final String ACTION = EventService.class.getCanonicalName();
    public static final String KEY_STATUS = "status";
    public static final String KEY_SYNC_DONE = "sync_done";

    public EventService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(TAG, "Getting event tracks");
        if (!syncDump()) {
            Log.d(TAG, "Manual Synchronization started");
        /* event */
            sendStatus("Preparing event");
            EventEvent event = new EventEvent(getApplicationContext());
            performSync(event, true);

        /* tracks */
            sendStatus("Getting tracks");
            EventTrack tracks = new EventTrack(getApplicationContext());
            performSync(tracks, true);

        /* speakers */
            sendStatus("Mapping speakers");
            ResPartner partner = new ResPartner(getApplicationContext());
            performSync(partner, true);

        /* Tags */
            sendStatus("Setting filters and rooms");
            EventTrackTag tags = new EventTrackTag(getApplicationContext());
            performSync(tags, true);

            sendStatus("Almost Done");
            /* Sponser */

            EventSponsor sponsor = new EventSponsor(getApplicationContext());
            performSync(sponsor, true);
            Log.v(TAG, "Tracks loading finished.");
            sendDoneSingle();
        }
    }

    private void sendStatus(String status) {
        Intent intent = new Intent(ACTION);
        intent.setAction(ACTION);
        intent.putExtra(KEY_STATUS, status);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private void sendDoneSingle() {
        OPreferenceManager preferenceManager = new OPreferenceManager(getApplicationContext());
        preferenceManager.putBoolean(OdooLogin.KEY_DB_SETUP_DONE, true);
        Intent intent = new Intent(ACTION);
        intent.setAction(ACTION);
        intent.putExtra(KEY_SYNC_DONE, true);
        LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(intent);
    }

    private void performSync(OModel model, Boolean checkForCreateWrite) {
        try {
            OSyncAdapter syncAdapter = new OSyncAdapter(getApplicationContext(), model.getClass(), null, true);
            syncAdapter.setModel(model);
            syncAdapter.checkForWriteCreateDate(checkForCreateWrite);
            syncAdapter.onPerformSync(null, null, model.authority(), null, new SyncResult());
        } catch (Exception e) {
            Log.w(TAG, e.getMessage(), e);
        }
    }


    private boolean syncDump() {
        if (OConstants.APPLY_DB_DUMP) {
            AssetManager assetManager = getApplicationContext().getResources().getAssets();
            File databases = new File(getFilesDir().getParent() + File.separator + "databases");
            boolean hasDBDIR = databases.exists();
            if (!databases.exists()) {
                hasDBDIR = databases.mkdir();
            }
            String dbPath = databases.getPath() + File.separator + OConstants.DATABASE_NAME;
            if (!new File(dbPath).exists()) {
                try {
                    Thread.sleep(1000);
                    InputStream in = assetManager.open("dump" + File.separator + OConstants.DATABASE_NAME);
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
                    new OPreferenceManager(getApplicationContext())
                            .putBoolean(OdooLogin.KEY_DB_SETUP_DONE, true);
                    sendDoneSingle();
                    startService(new Intent(getApplicationContext(), EventService.class));
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
