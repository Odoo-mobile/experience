package com.odoo.experience;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.odoo.experience.core.helper.OdooActivity;
import com.odoo.experience.core.helper.OdooFragment;
import com.odoo.experience.ui.FragmentXPSponsors;
import com.odoo.experience.ui.info.FragmentXPInfo;
import com.odoo.experience.ui.schedule.FragmentXPSchedule;


public class MainActivity extends OdooActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private int recentScreen = R.id.menu_schedule;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.bottomNavigation);
        navigationView.setSelectedItemId(R.id.menu_schedule);
        loadFragment(R.id.menu_schedule);
        navigationView.setOnNavigationItemSelectedListener(this);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark)); // Navigation bar the soft bottom of some phones like nexus and some Samsung note series
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            default:
                if (recentScreen != menuItem.getItemId()) {
                    loadFragment(menuItem.getItemId());
                    recentScreen = menuItem.getItemId();
                }
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (recentScreen != R.id.menu_schedule) {
            loadFragment(R.id.menu_schedule);
            recentScreen = R.id.menu_schedule;
            navigationView.setSelectedItemId(R.id.menu_schedule);
        } else {
            super.onBackPressed();
        }
    }

    private void loadFragment(int menuId) {
        OdooFragment fragment = null;
        AppBarLayout appBarLayout = findViewById(R.id.appbarLayout);
        appBarLayout.setExpanded(true, true);
        switch (menuId) {
            case R.id.menu_info:
                setTitle(getString(R.string.label_info));
                fragment = new FragmentXPInfo();
                break;
            case R.id.menu_schedule:
                setTitle(getString(R.string.label_schedule));
                fragment = new FragmentXPSchedule();
                break;
            case R.id.menu_sponsors:
                setTitle(getString(R.string.label_sponsors));
                fragment = new FragmentXPSponsors();
                break;
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in,
                            android.R.anim.fade_out)
                    .replace(R.id.viewContainer, fragment, null)
                    .commit();
        } else {
            Log.e("MainActivity", "No Fragment Defined for menuId " + menuId);
        }
    }
}
