package com.odoo.addons.events;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.odoo.R;
import com.odoo.addons.events.models.EventTrack;
import com.odoo.addons.events.services.EventService;
import com.odoo.addons.events.utils.DayPagerAdapter;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.drawer.ODrawerItem;

import java.util.List;

public class EventScheduleDayPager extends BaseFragment
        implements DayPagerAdapter.OnFragmentCallListener {

    public static final String KEY_DAY = "event_day_number";
    public static final String KEY_HAS_TALKS = "has_talks_trainings";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        parent().setHasTabLayout(true);
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.day_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updatePager();
    }

    private void updatePager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        DayPagerAdapter adapter = new DayPagerAdapter(getContext(), getChildFragmentManager(),
                EventSchedule.class, this);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = parent().getTabLayout();
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        return null;
    }

    @Override
    public Class<EventTrack> database() {
        return EventTrack.class;
    }

    @Override
    public Bundle fragmentBundle(boolean hasTalks, int position) {
        Bundle data = new Bundle();
        data.putBoolean(EventScheduleDayPager.KEY_HAS_TALKS, true);
        data.putInt(EventScheduleDayPager.KEY_DAY, position + (hasTalks ? 0 : 1));
        return data;
    }

    @Override
    public String pageTitle(boolean hasTalks, int position) {
        if (hasTalks && position == 0) {
            return getString(R.string.label_talks_training);
        }
        return String.format(_s(R.string.str_day), position + (!hasTalks ? 1 : 0));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.day_pager, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh_data) {
            parent().startService(new Intent(parent(), EventService.class));
            Toast.makeText(getContext(), R.string.refreshing_data, Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
