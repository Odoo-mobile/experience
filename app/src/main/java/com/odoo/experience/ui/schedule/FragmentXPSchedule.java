package com.odoo.experience.ui.schedule;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.odoo.experience.AppConfig;
import com.odoo.experience.R;
import com.odoo.experience.core.db.ORecord;
import com.odoo.experience.core.helper.OdooFragment;
import com.odoo.experience.core.utils.OBind;
import com.odoo.experience.core.utils.ODateUtils;
import com.odoo.experience.database.models.EventTrackTags;
import com.odoo.experience.database.models.EventTracks;
import com.odoo.experience.ui.TabFragmentAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class FragmentXPSchedule extends OdooFragment implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private TabLayout tabLayout;
    private static final int REQUEST_FILTER_DATA = 5734;
    private ArrayList<Integer> tagIds = new ArrayList<>();
    private ArrayList<Integer> locationIds = new ArrayList<>();
    private List<OnFilterDataChangedListener> filterDataChangedListeners = new ArrayList<>();
    private String[] colors = {
            "#777777", "#F06050", "#F4A460", "#F7CD1F",
            "#6CC1ED", "#814968", "#EB7E7F", "#2C8397",
            "#475577", "#D6145F", "#30C381", "#9365B8"
    };

    @Override
    public int getViewResourceId() {
        setHasOptionsMenu(true);
        return R.layout.screen_schedule;
    }

    @Override
    public void onViewReady(View view) {
        tabLayout = getTabLayout();
        ViewPager viewPager = findViewById(R.id.scheduleContainer);
        TabFragmentAdapter fragmentAdapter = new TabFragmentAdapter(getChildFragmentManager());
        for (String date : AppConfig.EVENT_DATES) {
            String tabItemTitle = ODateUtils.parseDate(date, ODateUtils.DEFAULT_DATE_FORMAT, "MMM dd");
            TabLayout.Tab tabItem = tabLayout.newTab();
            tabItem.setText(tabItemTitle);
            tabItem.setTag(date);

            Bundle args = new Bundle();
            args.putString(FragScheduledTracks.KEY_DATE, date);
            FragScheduledTracks tracks = new FragScheduledTracks();
            tracks.setFragmentXP(this);
            tracks.setArguments(args);
            filterDataChangedListeners.add(tracks);
            fragmentAdapter.addFragment(tracks, tabItemTitle);
            tabLayout.addTab(tabItem);
        }
        TabLayout.Tab tabItem = tabLayout.newTab();
        tabItem.setText(getString(R.string.label_starred));
        tabItem.setTag("starred");
        tabLayout.addTab(tabItem);
        fragmentAdapter.addFragment(new FragStarred(), getString(R.string.label_starred));

        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.addOnPageChangeListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter_track:
                Intent intent = new Intent(getContext(), FilterSheetDialog.class);
                intent.putExtra("tag_ids", tagIds);
                intent.putExtra("location_ids", locationIds);
                startActivityForResult(intent, REQUEST_FILTER_DATA);
                getActivity().overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.no_change);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_FILTER_DATA) {
            tagIds = data.getIntegerArrayListExtra("tag_ids");
            locationIds = data.getIntegerArrayListExtra("location_ids");
            for (OnFilterDataChangedListener listener : filterDataChangedListeners) {
                listener.onFilterDataChanged();
            }
            getActivity().findViewById(R.id.filterContainer).setOnClickListener(this);
            getActivity().findViewById(R.id.resetFilters).setOnClickListener(this);
            if (!tagIds.isEmpty() || !locationIds.isEmpty()) {
                showFilterItems();
                getActivity().findViewById(R.id.filterContainer).setVisibility(View.VISIBLE);
            } else {
                getActivity().findViewById(R.id.filterContainer).setVisibility(View.GONE);
            }
        }
    }

    private void showFilterItems() {
        EventTrackTags tagObj = new EventTrackTags(getContext());
        List<ORecord> items = new ArrayList<>();
        if (!tagIds.isEmpty()) {
            items.addAll(tagObj.select("id IN (" + TextUtils.join(", ", tagIds) + ")", null, null));
        }

        if (!locationIds.isEmpty()) {
            EventTracks tracks = new EventTracks(getContext());
            items.addAll(tracks.select(new String[]{"distinct location_id as id", "location_name as name"}, "location_id IN (" + TextUtils.join(", ", locationIds) + ")",
                    new String[]{}, "location_name"));
        }

        ViewGroup parent = getActivity().findViewById(R.id.filterItems);
        bindTags(items, parent);
    }

    private void bindTags(List<ORecord> records, ViewGroup parent) {
        parent.removeAllViews();
        parent.setVisibility(View.VISIBLE);
        for (ORecord record : records) {
            if (!record.contains("color")) record.put("color", 2);
            if (record.getInt("color") > 0) {
                View view = LayoutInflater.from(getContext())
                        .inflate(R.layout.xp_filter_tag_item_view, parent, false);
                OBind.setText(view.findViewById(R.id.filterName), record.getString("name"));
                TextView name = view.findViewById(R.id.filterName);
                name.setTextColor(Color.WHITE);
                name.setSingleLine(true);
                view.findViewById(R.id.filterDotColor).setVisibility(View.GONE);
                int color = record.getInt("color");
                GradientDrawable drawable = (GradientDrawable) view.getBackground();
                drawable.setColorFilter(Color.parseColor(colors[color]), PorterDuff.Mode.SRC_IN);
                view.setBackground(drawable);
                parent.addView(view);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.xp_schedule_tracks, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public List<Integer> getFilterLocationIds() {
        return locationIds;
    }

    public List<Integer> getFilterTagIds() {
        return tagIds;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.filterContainer:
                Intent intent = new Intent(getContext(), FilterSheetDialog.class);
                intent.putExtra("tag_ids", tagIds);
                intent.putExtra("location_ids", locationIds);
                startActivityForResult(intent, REQUEST_FILTER_DATA);
                break;
            case R.id.resetFilters:
                if (!tagIds.isEmpty() || !locationIds.isEmpty()) {
                    tagIds.clear();
                    locationIds.clear();
                    for (OnFilterDataChangedListener listener : filterDataChangedListeners) {
                        listener.onFilterDataChanged();
                    }
                    getActivity().findViewById(R.id.filterContainer).setVisibility(View.GONE);
                }
                break;
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (getActivity() != null) {
            getActivity().findViewById(R.id.filterContainer)
                    .setVisibility(position > 2 ? View.GONE :
                            !tagIds.isEmpty() || !locationIds.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        tagIds.clear();
        locationIds.clear();
        getActivity().findViewById(R.id.filterContainer).setVisibility(View.GONE);
        super.onDestroy();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public interface OnFilterDataChangedListener {
        void onFilterDataChanged();
    }

}
