package com.odoo.core.account;

import android.animation.LayoutTransition;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.odoo.OdooActivity;
import com.odoo.R;
import com.odoo.addons.events.services.EventService;
import com.odoo.core.support.kenburns.KenBurnsView;
import com.odoo.core.support.kenburns.LoopViewPager;
import com.odoo.core.utils.OPreferenceManager;

import java.util.Arrays;
import java.util.List;

public class OdooLogin extends AppCompatActivity implements View.OnClickListener {

    public static final String KEY_DB_SETUP_DONE = "database_setup_done_2017";
    private OPreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_login);
        preferenceManager = new OPreferenceManager(this);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(receiver, new IntentFilter(EventService.ACTION));
        if (!preferenceManager.getBoolean(OdooLogin.KEY_DB_SETUP_DONE, false))
            startService(new Intent(this, EventService.class));
        init();
    }

    private void init() {
        if (preferenceManager.getBoolean(KEY_DB_SETUP_DONE, false)) {
            startOdooActivity();
            return;
        }
        LinearLayout loginContainer = (LinearLayout) findViewById(R.id.loginContainer);
        loginContainer.setLayoutTransition(new LayoutTransition());
        findViewById(R.id.btnGetStarted).setOnClickListener(this);
        initializeKenBurnsView();
    }

    private void startOdooActivity() {
        startActivity(new Intent(this, OdooActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGetStarted:
                startOdooActivity();
                break;
        }
    }

    private void initializeKenBurnsView() {
        final KenBurnsView kenBurnsView = (KenBurnsView) findViewById(R.id.ken_burns_view);
        List<Integer> images = Arrays.asList(
                R.drawable.img1,
                R.drawable.img2,
                R.drawable.img3,
                R.drawable.img4,
                R.drawable.img5
        );
        kenBurnsView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        kenBurnsView.setSwapMs(7000);
        kenBurnsView.setFadeInOutMs(1000);
        kenBurnsView.loadResourceIDs(images);
        LoopViewPager.LoopViewPagerListener listener = new LoopViewPager.LoopViewPagerListener() {
            @Override
            public View OnInstantiateItem(int page) {
                return null;
            }

            @Override
            public void onPageScrollChanged(int page) {

            }

            @Override
            public void onPageScroll(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                kenBurnsView.forceSelected(position);
            }
        };
        kenBurnsView.setPager(new LoopViewPager(this, images.size(), listener));
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle data = intent.getExtras();
            if (data.containsKey(EventService.KEY_SYNC_DONE)) {
                // Done sync, show let's explore button.
                findViewById(R.id.progressLayout).setVisibility(View.GONE);
                findViewById(R.id.btnGetStarted).setVisibility(View.VISIBLE);
                preferenceManager.putBoolean(KEY_DB_SETUP_DONE, true);
            } else {
                TextView status = (TextView) findViewById(R.id.progressStatus);
                status.setText(data.getString(EventService.KEY_STATUS));
            }
        }
    };
}