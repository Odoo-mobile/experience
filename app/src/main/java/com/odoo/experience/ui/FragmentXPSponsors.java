package com.odoo.experience.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.odoo.experience.R;
import com.odoo.experience.core.db.ORecord;
import com.odoo.experience.core.helper.OdooFragment;
import com.odoo.experience.core.utils.BitmapUtils;
import com.odoo.experience.core.utils.OBind;
import com.odoo.experience.database.models.EventSponsors;
import com.odoo.experience.services.OdooDataService;

import java.util.List;

public class FragmentXPSponsors extends OdooFragment implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private EventSponsors sponsors;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayAdapter<ORecord> adapter;

    @Override
    public int getViewResourceId() {
        return R.layout.screen_xp_sponsors;
    }

    @Override
    public void onViewReady(View view) {
        setHasOptionsMenu(true);
        resetTabLayout();
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(this);
        GridView gridView = view.findViewById(R.id.sponsorGrid);
        sponsors = new EventSponsors(getContext());
        List<ORecord> records = sponsors.select(null, null, "sequence, partner_name");
        adapter = new ArrayAdapter<ORecord>(getContext(), R.layout.screen_xp_sponsors_view, records) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext())
                            .inflate(R.layout.screen_xp_sponsors_view, parent, false);
                }
                ORecord record = getItem(position);
                ImageView logo = convertView.findViewById(R.id.sponsorLogo);
                if (record.getString("image_medium").equals("false")) {
                    logo.setImageResource(R.drawable.odoo_no_logo);
                } else {
                    logo.setImageBitmap(BitmapUtils.getBitmapImage(getContext(), record.getString("image_medium")));
                }
                OBind.setText(convertView.findViewById(R.id.sponsorType), record.getString("sponsor_type_name"));
                GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.badge_secondary_rounded);
                int badgeColor = Color.DKGRAY;
                switch (record.getString("sponsor_type_name")) {
                    case "VIP":
                        badgeColor = ContextCompat.getColor(getContext(), R.color.colorPrimary);
                        break;
                    case "Demo Booth":
                        badgeColor = Color.parseColor("#00A09D");
                }
                drawable.setColor(badgeColor);
                convertView.findViewById(R.id.sponsorType).setBackground(drawable);
                return convertView;
            }
        };
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ORecord record = (ORecord) adapterView.getItemAtPosition(i);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(record.getString("url")));
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        Intent syncData = new Intent(getContext(), OdooDataService.class);
        syncData.putExtra(OdooDataService.KEY_SYNC_REQUEST_FOR, OdooDataService.SYNC_SPONSORS);
        getActivity().startService(syncData);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(syncStatus, new IntentFilter(OdooDataService.TAG));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext())
                .unregisterReceiver(syncStatus);
    }

    private BroadcastReceiver syncStatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle data = intent.getExtras();
            swipeRefreshLayout.setRefreshing(false);
            switch (data.getString(OdooDataService.KEY_SYNC_STATUS)) {
                case OdooDataService.SYNC_STATUS_FINISHED:
                    List<ORecord> records = sponsors.select(null, null, "sequence, partner_name");
                    adapter.clear();
                    adapter.addAll(records);
                    adapter.notifyDataSetChanged();
                    break;
                case OdooDataService.SYNC_STATUS_NO_NETWORK:
                    break;
            }
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_xsponsors, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
