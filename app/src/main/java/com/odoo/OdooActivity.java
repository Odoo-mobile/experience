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
 * Created on 18/12/14 5:25 PM
 */
package com.odoo;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.odoo.addons.events.models.EventEvent;
import com.odoo.core.account.AppIntro;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.support.addons.fragment.IBaseFragment;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.utils.OAppBarUtils;
import com.odoo.core.utils.ODateUtils;
import com.odoo.core.utils.OFragmentUtils;
import com.odoo.core.utils.OPreferenceManager;
import com.odoo.core.utils.drawer.DrawerUtils;
import com.odoo.core.utils.sys.IOnActivityResultListener;
import com.odoo.core.utils.sys.IOnBackPressListener;

import java.util.List;
import java.util.Locale;

public class OdooActivity extends OdooCompatActivity {

    public static final String TAG = OdooActivity.class.getSimpleName();
    public static final Integer DRAWER_ITEM_LAUNCH_DELAY = 300;
    public static final String KEY_CURRENT_DRAWER_ITEM = "key_drawer_item_index";
    public static final String KEY_APP_TITLE = "key_app_title";
    public static final String KEY_HAS_ACTIONBAR_SPINNER = "key_has_actionbar_spinner";
    public static final String KEY_FRESH_LOGIN = "key_fresh_login";

    private DrawerLayout mDrawerLayout = null;
    private ActionBarDrawerToggle mDrawerToggle = null;
    private IOnBackPressListener backPressListener = null;
    private IOnActivityResultListener mIOnActivityResultListener = null;
    //Drawer Containers
    private LinearLayout mDrawerItemContainer = null;
    private Bundle mSavedInstanceState = null;
    private Integer mDrawerSelectedIndex = -1;
    private Boolean mHasActionBarSpinner = false;

    // TabLayout
    private Boolean hasTabLayout = false;
    private TabLayout tabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "OdooActivity->onCreate");
        mSavedInstanceState = savedInstanceState;
        startApp(savedInstanceState);
    }

    private void startApp(Bundle savedInstanceState) {
        OPreferenceManager preferenceManager = new OPreferenceManager(this);
        if (!preferenceManager.getBoolean(KEY_FRESH_LOGIN, false)) {
            preferenceManager.putBoolean(KEY_FRESH_LOGIN, true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(OdooActivity.this, AppIntro.class));
                }
            }, 500);
        }
        setContentView(R.layout.odoo_activity);
        OAppBarUtils.setAppBar(this, true);
        setupDrawer();
    }

    // Creating drawer
    private void setupDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                setTitle(getResources().getString(R.string.app_name));
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                setTitle(R.string.app_name);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerToggle.syncState();

        setupDrawerBox();
    }

    private void setupDrawerBox() {
        mDrawerItemContainer = (LinearLayout) findViewById(R.id.drawerItemList);
        mDrawerItemContainer.removeAllViews();
        List<ODrawerItem> items = DrawerUtils.getDrawerItems(this);
        for (ODrawerItem item : items) {
            View view = LayoutInflater.from(this).
                    inflate((item.isGroupTitle()) ? R.layout.base_drawer_group_layout :
                            R.layout.base_drawer_menu_item, mDrawerItemContainer, false);
            view.setTag(item);
            if (!item.isGroupTitle()) {
                view.setOnClickListener(drawerItemClick);
            }
            mDrawerItemContainer.addView(DrawerUtils.fillDrawerItemValue(view, item));
        }

        EventEvent event = new EventEvent(this);
        if (!event.isEmptyTable()) {
            TextView eventName = (TextView) findViewById(R.id.eventName);
            TextView eventLaunchTime = (TextView) findViewById(R.id.eventLaunchTime);
            TextView eventDate = (TextView) findViewById(R.id.eventDate);
            eventName.setText(String.format("%s %s", getString(R.string.app_name), "2017"));

            int dayOfEvent = event.getDayNumber();
            if (dayOfEvent > 0) {
                // Day started
                String day = String.format(Locale.getDefault(), "Day %d", dayOfEvent);
                eventLaunchTime.setText(day);
                eventDate.setText(ODateUtils.parseDate(ODateUtils.getDate(), ODateUtils.DEFAULT_FORMAT, "MMMM, dd"));
            } else {
                eventLaunchTime.setText(event.getEventDisplayDates());
                eventDate.setText(event.getDaysDisplayName());
            }
        }
    }

    private View.OnClickListener drawerItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = mDrawerItemContainer.indexOfChild(v);
            if (mDrawerSelectedIndex != index) {
                ODrawerItem item = (ODrawerItem) v.getTag();
                if (item.getInstance() instanceof Fragment) {
                    focusOnDrawerItem(index);
                    setTitle(item.getTitle());
                }

                loadDrawerItemInstance(item.getInstance(), item.getExtra());
            } else {
                closeDrawer();
            }
        }
    };

    public void closeDrawer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        }, DRAWER_ITEM_LAUNCH_DELAY);

    }

    /**
     * Loads fragment or start intent
     *
     * @param instance, instance of fragment or intent
     */
    private void loadDrawerItemInstance(Object instance, Bundle extra) {
        if (instance != null) {
            if (instance instanceof Intent) {
                Log.i(TAG, "Loading intent: " + instance.getClass().getCanonicalName());
                startActivity((Intent) instance);
            }
            if (instance instanceof Class<?>) {
                Class<?> cls = (Class<?>) instance;
                Intent intent = null;
                if (cls.getSuperclass().isAssignableFrom(Activity.class)) {
                    intent = new Intent(this, cls);
                }
                if (cls.getSuperclass().isAssignableFrom(AppCompatActivity.class)) {
                    intent = new Intent(this, cls);
                }
                if (cls.getSuperclass().isAssignableFrom(OdooCompatActivity.class)) {
                    intent = new Intent(this, cls);
                }
                if (intent != null) {
                    if (extra != null)
                        intent.putExtras(extra);
                    loadDrawerItemInstance(intent, null);
                    return;
                }
            }
            if (instance instanceof Fragment) {
                Log.i(TAG, "Loading fragment: " + instance.getClass().getCanonicalName());
                OFragmentUtils.get(this, mSavedInstanceState).startFragment((Fragment) instance, false, extra);
            }
        }
        closeDrawer();
    }

    public void loadFragment(Fragment fragment, Boolean addToBackState, Bundle extra) {
        OFragmentUtils.get(this, null).startFragment(fragment, addToBackState, extra);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (backPressListener != null) {
            if (backPressListener.onBackPressed()) {
                super.onBackPressed();
            }
        } else
            super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mIOnActivityResultListener != null) {
            mIOnActivityResultListener.onOdooActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Set system back button press listener
     *
     * @param listener
     */
    public void setOnBackPressListener(IOnBackPressListener listener) {
        backPressListener = listener;
    }

    public void setOnActivityResultListener(IOnActivityResultListener listener) {
        mIOnActivityResultListener = listener;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mSavedInstanceState = savedInstanceState;
        if (savedInstanceState == null) {
            // Loading Default Fragment (if any)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    IBaseFragment fragment = DrawerUtils.getDefaultDrawerFragment();
                    if (fragment != null) {
                        ODrawerItem item = DrawerUtils.getStartableObject(OdooActivity.this, fragment);
                        setTitle(item.getTitle());
                        loadDrawerItemInstance(item.getInstance(), item.getExtra());
                        int selected_item = DrawerUtils.findItemIndex(item, mDrawerItemContainer);
                        if (selected_item > -1) {
                            focusOnDrawerItem(selected_item);
                        }
                    }
                }
            }, DRAWER_ITEM_LAUNCH_DELAY);
        } else {
            mHasActionBarSpinner = savedInstanceState.getBoolean(KEY_HAS_ACTIONBAR_SPINNER);
            mDrawerSelectedIndex = savedInstanceState.getInt(KEY_CURRENT_DRAWER_ITEM);
            setTitle(savedInstanceState.getString(KEY_APP_TITLE));
            focusOnDrawerItem(mDrawerSelectedIndex);
        }
    }


    private void focusOnDrawerItem(int index) {
        mDrawerSelectedIndex = index;
        for (int i = 0; i < mDrawerItemContainer.getChildCount(); i++) {
            DrawerUtils.focusOnView(this, mDrawerItemContainer.getChildAt(i), i == index);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_CURRENT_DRAWER_ITEM, mDrawerSelectedIndex);
        outState.putString(KEY_APP_TITLE, getTitle().toString());
        outState.putBoolean(KEY_HAS_ACTIONBAR_SPINNER, mHasActionBarSpinner);
        super.onSaveInstanceState(outState);
    }

    /**
     * Actionbar Spinner handler
     */

    public void setHasActionBarSpinner(Boolean hasActionBarSpinner) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            Spinner spinner = (Spinner) findViewById(R.id.spinner_nav);
            if (hasActionBarSpinner) {
                if (spinner != null)
                    spinner.setVisibility(View.VISIBLE);
                actionBar.setDisplayShowTitleEnabled(false);
            } else {
                if (spinner != null)
                    spinner.setVisibility(View.GONE);
                actionBar.setDisplayShowTitleEnabled(true);
            }
            mHasActionBarSpinner = hasActionBarSpinner;
        }
    }

    public void setHasTabLayout(Boolean hasTabLayout) {
        tabLayout = null;
        if (hasTabLayout) {
            tabLayout = (TabLayout) findViewById(R.id.tabLayout);
            tabLayout.setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.tabLayout).setVisibility(View.GONE);
        }
        this.hasTabLayout = hasTabLayout;
    }

    public TabLayout getTabLayout() {
        tabLayout = null;
        if (hasTabLayout) {
            tabLayout = (TabLayout) findViewById(R.id.tabLayout);
            tabLayout.setVisibility(View.VISIBLE);
            return tabLayout;
        } else {
            findViewById(R.id.tabLayout).setVisibility(View.GONE);
        }
        return null;
    }

    public Spinner getActionBarSpinner() {
        Spinner spinner = null;
        if (mHasActionBarSpinner) {
            spinner = (Spinner) findViewById(R.id.spinner_nav);
            spinner.setAdapter(null);
        }
        return spinner;
    }

    public void refreshDrawer() {
        setupDrawerBox();
    }

}
