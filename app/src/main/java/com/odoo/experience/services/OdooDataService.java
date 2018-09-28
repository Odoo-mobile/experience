package com.odoo.experience.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.odoo.experience.AppConfig;
import com.odoo.experience.core.api.odoo.OdooClient;
import com.odoo.experience.core.api.odoo.OdooUser;
import com.odoo.experience.core.api.odoo.client.AuthError;
import com.odoo.experience.core.api.odoo.client.helper.OdooErrorException;
import com.odoo.experience.core.api.odoo.client.helper.data.OdooRecord;
import com.odoo.experience.core.api.odoo.client.helper.data.OdooResult;
import com.odoo.experience.core.api.odoo.client.helper.utils.ODomain;
import com.odoo.experience.core.api.odoo.client.helper.utils.OdooFields;
import com.odoo.experience.core.api.odoo.client.listeners.AuthenticateListener;
import com.odoo.experience.core.api.odoo.client.listeners.IOdooResponse;
import com.odoo.experience.core.api.odoo.client.listeners.OdooErrorListener;
import com.odoo.experience.core.db.OModel;
import com.odoo.experience.core.utils.NetworkUtils;
import com.odoo.experience.database.models.EventEvent;
import com.odoo.experience.database.models.EventSponsors;
import com.odoo.experience.database.models.EventTrackTags;
import com.odoo.experience.database.models.EventTracks;
import com.odoo.experience.database.models.ResPartner;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class OdooDataService extends IntentService implements OdooErrorListener {
    public static final String TAG = OdooDataService.class.getCanonicalName();
    public static final String KEY_SYNC_REQUEST_FOR = "sync_request_for";
    public static final String SYNC_EVENT = "sync_event";
    public static final String SYNC_SPONSORS = "sync_sponsors";
    public static final String SYNC_TRACKS = "sync_tracks";
    public static final String SYNC_PARTNERS = "sync_partners";
    private static final String LOG_TAG = "OdooRPCData======>";
    public static final String KEY_SYNC_STATUS = "sync_status";
    public static final String SYNC_STATUS_FINISHED = "sync_finished";
    public static final String SYNC_STATUS_NO_NETWORK = "sync_no_network";

    private OdooClient client;
    private SharedPreferences preferences;

    public OdooDataService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!NetworkUtils.isConnected(this)) {
            broadcastSyncFinished(SYNC_STATUS_NO_NETWORK);
            return;
        }
        Bundle bundle = intent.getExtras();
        String syncRequestFor = "all";
        if (bundle != null && bundle.containsKey(KEY_SYNC_REQUEST_FOR)) {
            syncRequestFor = bundle.getString(KEY_SYNC_REQUEST_FOR);
            if (syncRequestFor == null) syncRequestFor = "all";
        }
        preferences = getSharedPreferences("odoo_data_service", MODE_PRIVATE);
        AppConfig.D_SESSION_ID = preferences.getString("odoo_session", null);
        createClient();
        boolean isValidSession = isValidSession();
        if (client.isConnected() && !isValidSession) {
            client.authenticate(AppConfig.USER_NAME, AppConfig.USER_KEY, AppConfig.USER_DB, new AuthenticateListener() {
                @Override
                public void onLoginSuccess(OdooUser user) {
                    preferences.edit().putString("odoo_session", user.sessionId).apply();
                    AppConfig.D_SESSION_ID = user.sessionId;
                }

                @Override
                public void onLoginFail(AuthError error) {

                }
            });
        }
        if (!isValidSession()) {
            Log.e(TAG, "Session Not created. Returning....");
            return;
        }
        // If re-authenticated
        createClient();

        // events
        EventEvent eventEvent = new EventEvent(this);
        if (syncRequestFor.equals("all") || syncRequestFor.equals(SYNC_EVENT)) {
            ODomain eventDomain = new ODomain();
            eventDomain.add("id", "=", AppConfig.EVENT_ID);
            syncData(eventEvent, eventDomain, null);
        }

        // sponsors
        if (syncRequestFor.equals("all") || syncRequestFor.equals(SYNC_SPONSORS)) {
            ODomain sponsorDomain = new ODomain();
            sponsorDomain.add("event_id", "=", AppConfig.EVENT_ID);
            syncData(new EventSponsors(this), sponsorDomain, null);
        }

        // tracks and tags
        if (syncRequestFor.equals("all") || syncRequestFor.equals(SYNC_TRACKS)) {
            // tags
            syncData(new EventTrackTags(this), new ODomain(), null);

            // tracks
            ODomain trackDomain = new ODomain();
            trackDomain.add("event_id", "=", AppConfig.EVENT_ID);
            String eventStartDate = AppConfig.EVENT_DATES[0];
            if (eventStartDate != null) {
                trackDomain.add("|");
                trackDomain.add("date", ">=", eventStartDate);
                trackDomain.add("date", "=", false);
            }
            syncData(new EventTracks(this), trackDomain, "date");
        }

        // speakers
        if (syncRequestFor.equals("all") || syncRequestFor.equals(SYNC_PARTNERS)) {
            ODomain partnerDomain = new ODomain();
            List<Integer> partnerIds = new EventTracks(this).getM20Ids("partner_id",
                    "partner_id != ?", "0");
            partnerDomain.add("id", "in", partnerIds);
            syncData(new ResPartner(this), partnerDomain, "name");
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
        String sessionId = preferences.getString("odoo_session", AppConfig.D_SESSION_ID);
        if (sessionId != null) {
            OdooUser user = new OdooUser();
            user.host = "https://www.odoo.com";
            user.sessionId = sessionId;
            client = new OdooClient.Builder(this)
                    .setHost("https://www.odoo.com")
                    .setErrorListener(this)
                    .setSynchronizedRequests(true)
                    .build(user);
        } else {
            client = new OdooClient.Builder(this)
                    .setHost("https://www.odoo.com")
                    .setSynchronizedRequests(true)
                    .setErrorListener(this)
                    .build();
        }
    }

    private boolean isValidSession() {
        final boolean valid[] = {false};
        client.setSession(AppConfig.D_SESSION_ID);
        client.getSessionInformation(new IOdooResponse() {
            @Override
            public void onResult(OdooResult result) {
                valid[0] = !(result.get("uid") instanceof Boolean);
            }

            @Override
            public boolean onError(OdooErrorException error) {
                Log.e("NoSession", "No session available.");
                return true;
            }
        });
        return valid[0];
    }

    @Override
    public void onError(OdooErrorException error) {
        Log.w(TAG, "ERROR " + error.getMessage());
    }

    private void syncData(final OModel model, ODomain domain, final String sort) {
        String lastSyncDate = model.getLastSyncDate();
        if (lastSyncDate != null) {
            domain.add("write_date", ">", lastSyncDate);
        }
        OdooFields fields = new OdooFields(model.syncFields());
        client.withRetryPolicy(60000, 1);
        client.searchRead(model.getModelName(), domain, fields, 0, 0, sort, new IOdooResponse() {
            @Override
            public void onResult(OdooResult result) {
                for (OdooRecord record : result.getRecords()) {
                    model.createOrUpdate(model.recordToValues(record), record.getInt("id"));
                }
                model.setSyncDate();
                String str = String.format(Locale.getDefault(),
                        "%20s: %5d records synced", model.getModelName(), result.getRecords().length);
                Log.i(LOG_TAG, str);
            }

            @Override
            public boolean onError(OdooErrorException error) {
                Log.e(">>>", error.debugMessage);
                return false;
            }
        });
    }
}
