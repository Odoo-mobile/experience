package com.odoo.experience.services;

import android.app.IntentService;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.odoo.experience.AppConfig;
import com.odoo.experience.core.api.odoo.OdooClient;
import com.odoo.experience.core.api.odoo.client.helper.OdooErrorException;
import com.odoo.experience.core.api.odoo.client.helper.data.OdooRecord;
import com.odoo.experience.core.api.odoo.client.helper.data.OdooResult;
import com.odoo.experience.core.api.odoo.client.helper.utils.OdooParams;
import com.odoo.experience.core.api.odoo.client.listeners.IOdooResponse;
import com.odoo.experience.core.api.odoo.client.listeners.OdooErrorListener;
import com.odoo.experience.core.utils.NetworkUtils;
import com.odoo.experience.database.models.EventEvent;
import com.odoo.experience.database.models.EventSponsors;
import com.odoo.experience.database.models.EventTrackTags;
import com.odoo.experience.database.models.EventTracks;

public class OdooDataService extends IntentService implements OdooErrorListener {
    public static final String TAG = OdooDataService.class.getCanonicalName();
    public static final String KEY_SYNC_REQUEST_FOR = "sync_request_for";
    public static final String SYNC_SPONSORS = "sync_sponsors";
    public static final String SYNC_TRACKS = "sync_tracks";
    private static final String LOG_TAG = "OdooRPCData======>";
    public static final String KEY_SYNC_STATUS = "sync_status";
    public static final String SYNC_STATUS_FINISHED = "sync_finished";
    public static final String SYNC_STATUS_NO_NETWORK = "sync_no_network";

    private OdooClient client;
    private EventEvent eventEvent;
    private EventTracks eventTracks;

    public OdooDataService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!NetworkUtils.isConnected(this)) {
            broadcastSyncFinished(SYNC_STATUS_NO_NETWORK);
            return;
        }
        eventEvent = new EventEvent(this);
        eventTracks = new EventTracks(this);
        createClient();
        if (syncBaseData()) {
            syncTracks();
        }
        broadcastSyncFinished(SYNC_STATUS_FINISHED);
    }

    public void broadcastSyncFinished(String status) {
        Log.v(TAG, "broadcastSyncFinished: " + status);
        Intent intent = new Intent(TAG);
        intent.putExtra(KEY_SYNC_STATUS, status);
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(intent);
    }


    private void createClient() {
        client = new OdooClient.Builder(this)
                .setHost(AppConfig.ODOO_URL)
                .setSynchronizedRequests(true)
                .setErrorListener(this)
                .build();
    }

    @Override
    public void onError(OdooErrorException error) {
        Log.w(TAG, "ERROR " + error.getMessage());
    }

    private boolean syncBaseData() {
        final boolean[] success = {false};
        client.withRetryPolicy(60000, 1);
        String url = AppConfig.ODOO_URL + "/event/mobile/data";
        OdooParams params = new OdooParams();
        params.add("event_id", AppConfig.EVENT_ID);
        client.callController(url, params, new IOdooResponse() {
            @Override
            public void onResult(OdooResult result) {
                if (!result.isEmpty()) {
                    storeBaseData(result);
                    success[0] = true;
                }
            }

            @Override
            public boolean onError(OdooErrorException error) {
                Log.v(TAG, "RESPONSE ERROR: syncBaseData() " + error.getMessage(), error);
                return true;
            }
        });
        return success[0];
    }

    private void syncTracks() {
        client.withRetryPolicy(60000, 1);
        String url = AppConfig.ODOO_URL + "/event/mobile/tracks";
        OdooParams params = new OdooParams();
        params.add("event_id", AppConfig.EVENT_ID);
        String lastSyncDate = eventTracks.getLastSyncDate();
        if (lastSyncDate != null) {
            params.add("updated_after", lastSyncDate);
        }
        client.callController(url, params, new IOdooResponse() {
            @Override
            public void onResult(OdooResult result) {
                storeTracks(result.getRecords());
            }

            @Override
            public boolean onError(OdooErrorException error) {
                Log.v(TAG, "RESPONSE ERROR: syncTracks() " + error.getMessage(), error);
                return true;
            }
        });
    }

    private void storeBaseData(OdooResult result) {
        // event
        OdooRecord event = result.getData("event");
        eventEvent.createOrUpdate(eventEvent.recordToValues(event), event.getInt("id"));
        Log.v(LOG_TAG, "Event detail updated");
        // tags
        EventTrackTags tags = new EventTrackTags(this);
        OdooRecord[] tagList = result.getRecords("tags");
        for (OdooRecord tag : tagList) {
            tags.createOrUpdate(tags.recordToValues(tag), tag.getInt("id"));
        }
        Log.v(LOG_TAG, tagList.length + " tags synced");
        // sponsors
        EventSponsors eventSponsors = new EventSponsors(this);
        OdooRecord[] sponsors = result.getRecords("sponsors");
        for (OdooRecord sponsor : sponsors) {
            eventSponsors.createOrUpdate(eventSponsors.recordToValues(sponsor), sponsor.getInt("id"));
        }
        Log.v(LOG_TAG, sponsors.length + " sponsors synced");
        eventEvent.setSyncDate();
    }

    private void storeTracks(OdooRecord[] tracks) {
        for (OdooRecord record : tracks) {
            eventTracks.createOrUpdate(eventTracks.recordToValues(record), record.getInt("id"));
        }
        eventTracks.setSyncDate();
        Log.v(LOG_TAG, tracks.length + " tracks synced");
    }
}
