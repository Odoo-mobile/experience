package com.odoo.addons.events;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;

import com.odoo.R;
import com.odoo.SettingsActivity;
import com.odoo.addons.events.models.EventEvent;
import com.odoo.addons.events.models.EventTrackLocation;
import com.odoo.addons.events.models.EventTrackTag;
import com.odoo.addons.events.models.ExploreTracks;
import com.odoo.addons.events.models.UserEventSchedule;
import com.odoo.core.account.BaseSettings;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.addons.fragment.IOnSearchViewChangeListener;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.ODateUtils;
import com.odoo.core.utils.OResource;
import com.odoo.core.utils.StringUtils;
import com.odoo.core.utils.sys.IOnBackPressListener;

import java.util.ArrayList;
import java.util.List;

import odoo.controls.recycler.EasyRecyclerView;
import odoo.controls.recycler.EasyRecyclerViewAdapter;

public class EventExplore extends BaseFragment
        implements EasyRecyclerViewAdapter.OnItemViewClickListener,
        EasyRecyclerViewAdapter.OnViewBindListener,
        IOnBackPressListener, LoaderManager.LoaderCallbacks<Cursor>, IOnSearchViewChangeListener, View.OnClickListener {
    public static final String KEY_LIKES_TRACKS = "likes_tracks_only";
    private EasyRecyclerView exploreTracksView;
    private DrawerLayout drawer;
    private int colors[] = {
            Color.parseColor("#F3F3F3"),
            Color.parseColor("#EEEEEE"),
            Color.parseColor("#E1E1E1")
    };
    private int nextColor = 0;

    private String searchQuery = null;
    private EventEvent event;

    /* Filter values */
    private List<Integer> filterDays = new ArrayList<>();
    private List<Integer> filterRooms = new ArrayList<>();
    private List<Integer> filterTags = new ArrayList<>();
    private String eventTimeZone;
    private UserEventSchedule userEventSchedule;

    private TextView emptySearchResult;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        parent().setHasTabLayout(false);
        parent().setOnBackPressListener(this);
        return inflater.inflate(R.layout.activity_event_explore, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(OResource.string(getContext(), R.string.title_explore));
        event = new EventEvent(getContext());
        userEventSchedule = new UserEventSchedule(getContext());
        eventTimeZone = event.getEventTimeZone();
        init();
    }

    private void init() {
        emptySearchResult = (TextView) findViewById(R.id.empty_search_result);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        exploreTracksView = (EasyRecyclerView) findViewById(R.id.scheduleTracks);
        exploreTracksView.grid(2);
        exploreTracksView.setLayout(R.layout.explore_event_list_item);
        exploreTracksView.setGridSpanLookupListener(new EasyRecyclerView.GridSpanLookupListener() {
            @Override
            public int getSpanSize(int position, ODataRow row) {
                if (row.getBoolean("is_collapsible")) {
                    return 2;
                }
                return 1;
            }
        });

        Bundle data = getArguments();
        if (data != null && data.containsKey(KEY_LIKES_TRACKS)) {
            setTitle("Likes");
        } else {
            setTitle("Explore");
        }
        if (data != null && data.containsKey(EventScheduleDayPager.KEY_DAY)) {
            filterDays.add(data.getInt(EventScheduleDayPager.KEY_DAY));
        }
        // create filter items
        filterItems();
    }

    @Override
    public boolean onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
            return false;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.event_explore, menu);
        setHasSearchView(this, menu, R.id.menu_search_track);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_event_filter:
                toggleFilterDrawer();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleFilterDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else
            drawer.openDrawer(GravityCompat.END);
    }

    @Override
    public void onViewBind(int position, View view, ODataRow data) {
        OControls.setText(view, R.id.event_title, data.getString("name"));
        OControls.setVisible(view, R.id.event_desc);
        OControls.setGone(view, R.id.event_time);
        View toggleView = view.findViewById(R.id.toggleAttendingTrack);
        toggleView.setVisibility(View.GONE);
        if (!data.getString("description").equals("false"))
            OControls.setText(view, R.id.event_desc,
                    StringUtils.htmlToString(data.getString("description")));
        else
            OControls.setText(view, R.id.event_desc, "");
        if (data.getString("is_collapsible").equals("false")) {
            view.findViewById(R.id.event_speaker).setVisibility(View.VISIBLE);
            OControls.setText(view, R.id.event_speaker, data.getString("speakers"));
            view.findViewById(R.id.rowValue).setBackgroundColor(getNextColor());
            boolean attending = userEventSchedule.attending(data.getInt("id"));
            toggleView.setVisibility(View.VISIBLE);
            toggleView.setTag(data);
            toggleView.setOnClickListener(this);
            ImageView viewAttending = (ImageView) view.findViewById(R.id.viewAttending);
            viewAttending.setColorFilter(Color.parseColor(attending ? "#00a185" : "#cccdce"));
        } else {
            view.findViewById(R.id.event_speaker).setVisibility(View.GONE);
            String date = ODateUtils.convertToDefault(data.getString("track_time")
                    , ODateUtils.DEFAULT_FORMAT, "dd, MMM");
            String track_time;
            if (BaseSettings.showConferenceTime(getContext())) {
                track_time = ODateUtils.convertToTimeZone(data.getString("track_time"),
                        ODateUtils.DEFAULT_FORMAT, eventTimeZone, "hh:mm a");
            } else {
                track_time = ODateUtils.convertToDefault(data.getString("track_time")
                        , ODateUtils.DEFAULT_FORMAT, "hh:mm aa");
            }
            OControls.setText(view, R.id.event_title, date);
            OControls.setText(view, R.id.event_time, track_time);
            OControls.setVisible(view, R.id.event_time);
            OControls.setGone(view, R.id.event_desc);
            view.findViewById(R.id.rowValue).setBackgroundColor(Color.WHITE);
        }

    }

    private int getNextColor() {
        if (nextColor > 2) {
            nextColor = 0;
        }
        return colors[nextColor++];
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri uri = db().uri();

        List<String> args = new ArrayList<>();

        // tag filter
        if (!filterTags.isEmpty()) {
            List<String> tagIds = new ArrayList<>();
            for (int tag : filterTags) {
                tagIds.add(tag + "");
            }
            args.addAll(tagIds);
            uri = ((ExploreTracks) db()).tagFilterUri(filterTags.size());
        }

        String where = "track_time != ?";
        args.add("false");

        // Day Filters
        if (!filterDays.isEmpty()) {
            List<String> dayDates = new ArrayList<>();
            for (int day : filterDays) {
                String date = event.getEventDate(day);
                dayDates.add("date(track_time) = ?");
                args.add(date);
            }
            where += " AND (" + TextUtils.join(" OR ", dayDates) + ")";
        }

        // room filter
        if (!filterRooms.isEmpty()) {
            List<String> roomFilter = new ArrayList<>();
            for (int room : filterRooms) {
                roomFilter.add("location_id = ?");
                args.add(room + "");
            }
            where += " AND (" + TextUtils.join(" OR ", roomFilter) + ")";
        }
        // Search query
        if (searchQuery != null) {
            where += " and (name LIKE ? or description LIKE ? or " +
                    "room LIKE ? or speakers LIKE ?)";
            args.add("%" + searchQuery + "%");
            args.add("%" + searchQuery + "%");
            args.add("%" + searchQuery + "%");
            args.add("%" + searchQuery + "%");
        }


        // Filtering browse session with time
        Bundle data = getArguments();
        if (data.containsKey(EventScheduleDayPager.KEY_DAY) &&
                data.containsKey("date")) {
            where += " AND time(track_time) = ?";
            args.add(ODateUtils.parseDate(data.getString("date"),
                    ODateUtils.DEFAULT_FORMAT, ODateUtils.DEFAULT_TIME_FORMAT));
        }

        if (data.containsKey(KEY_LIKES_TRACKS) && data.getBoolean(KEY_LIKES_TRACKS)) {
            where += " AND liked = ?";
            args.add("true");
        }

        return new CursorLoader(getContext(), uri, null, where,
                args.toArray(new String[args.size()]), "track_time");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        nextColor = 0;
        if (cursor.getCount() != 0) {
            exploreTracksView.changeCursor(cursor);
            exploreTracksView.setOnViewBindListener(this);
            exploreTracksView.setOnItemViewClickListener(this);
            emptySearchResult.setVisibility(View.GONE);
            exploreTracksView.setVisibility(View.VISIBLE);
        } else {
            emptySearchResult.setVisibility(View.VISIBLE);
            exploreTracksView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        exploreTracksView.changeCursor(null);
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onItemViewClick(int position, View view, ODataRow data) {
        if (!data.getBoolean("is_collapsible")) {
            Intent trackDetail = new Intent(getContext(), EventTrackDetail.class);
            trackDetail.putExtra(EventTrackDetail.KEY_TRACK_ID, data.getInt("id"));
            startActivity(trackDetail);
        }
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        return null;
    }

    @Override
    public Class<ExploreTracks> database() {
        return ExploreTracks.class;
    }

    @Override
    public boolean onSearchViewTextChange(String newFilter) {
        searchQuery = newFilter;
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }

    @Override
    public void onSearchViewClose() {
        // Nothing to do
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(dataUpdateReceiver, new IntentFilter(ExploreTracks.ACTION_DATA_UPDATE));
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(dataUpdateReceiver);
    }

    private BroadcastReceiver dataUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("DataUpdated", "Explore data updated");
            getLoaderManager().restartLoader(0, null, EventExplore.this);
        }
    };

    private void filterItems() {
        Bundle data = getArguments();
        // Days
        final ViewGroup daysContainer = (ViewGroup) findViewById(R.id.daysContainer);
        daysContainer.removeAllViews();
        for (int i = 1; i <= event.getTotalDays(); i++) {
            String label = getString(R.string.label_day, i);
            boolean checked = data.containsKey(EventScheduleDayPager.KEY_DAY)
                    && data.getInt(EventScheduleDayPager.KEY_DAY) == i;
            final View view = getFilterView(daysContainer, label, R.id.filterDay, checked, i);
            daysContainer.post(new Runnable() {
                @Override
                public void run() {
                    daysContainer.addView(view);
                }
            });
        }

        // Tags
        EventTrackTag tags = new EventTrackTag(getContext());
        final ViewGroup tagsContainer = (ViewGroup) findViewById(R.id.tagsContainer);
        tagsContainer.removeAllViews();
        for (ODataRow tag : tags.select(new String[]{"name"}, null, null, "name")) {
            final View view = getFilterView(tagsContainer, tag.getString("name"),
                    R.id.filterTags, false, tag);
            tagsContainer.post(new Runnable() {
                @Override
                public void run() {
                    tagsContainer.addView(view);
                }
            });
        }

        // Rooms
        EventTrackLocation location = new EventTrackLocation(getContext());
        final ViewGroup locationContainer = (ViewGroup) findViewById(R.id.roomsContainer);
        tagsContainer.removeAllViews();
        for (ODataRow room : location.select(new String[]{"name"}, null, null, "name")) {
            final View view = getFilterView(tagsContainer, room.getString("name"),
                    R.id.filterRooms, false, room);
            locationContainer.post(new Runnable() {
                @Override
                public void run() {
                    locationContainer.addView(view);
                }
            });
        }
    }

    private View getFilterView(View parent, String label, int keyId, boolean checked, Object value) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.track_filter_item_view,
                (ViewGroup) parent, false);
        OControls.setText(view, R.id.filterTitle, label);
        setCheckboxEvent(view, keyId, value, checked);
        return view;
    }

    private void setCheckboxEvent(View view, int keyId, Object value, boolean checked) {
        view.setTag(keyId);
        view.setTag(keyId, value);
        view.setOnClickListener(filterOnClick);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.trackFilterValue);
        checkBox.setChecked(checked);
    }

    private View.OnClickListener filterOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.trackFilterValue);
            checkBox.setChecked(!checkBox.isChecked());
            boolean isChecked = checkBox.isChecked();
            int keyId = (int) view.getTag();
            switch (keyId) {
                case R.id.filterDay:
                    int dayValue = (int) view.getTag(keyId);
                    int dayIndex = filterDays.indexOf(dayValue);
                    if (dayIndex != -1 && !isChecked) {
                        filterDays.remove(dayIndex);
                    }
                    if (dayIndex == -1 && isChecked) {
                        filterDays.add(dayValue);
                    }
                    break;
                case R.id.filterTags:
                    ODataRow tag = (ODataRow) view.getTag(keyId);
                    int tagIndex = filterTags.indexOf(tag.getInt(OColumn.ROW_ID));
                    if (tagIndex != -1 && !isChecked) {
                        filterTags.remove(tagIndex);
                    }
                    if (tagIndex == -1 && isChecked) {
                        filterTags.add(tag.getInt(OColumn.ROW_ID));
                    }
                    break;
                case R.id.filterRooms:
                    ODataRow room = (ODataRow) view.getTag(keyId);
                    int roomIndex = filterRooms.indexOf(room.getInt(OColumn.ROW_ID));
                    if (roomIndex != -1 && !isChecked) {
                        filterRooms.remove(roomIndex);
                    }
                    if (roomIndex == -1 && isChecked) {
                        filterRooms.add(room.getInt(OColumn.ROW_ID));
                    }
                    break;
            }
            getLoaderManager().restartLoader(0, null, EventExplore.this);
        }
    };

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.toggleAttendingTrack) {
            ODataRow track = (ODataRow) view.getTag();
            int track_id = track.getInt("id");
            if (userEventSchedule.attending(track_id)) {
                Log.v(TAG, "Removing track attend:" + track_id);
                userEventSchedule.removeAttend(track_id);
                Toast.makeText(getContext(), R.string.session_removed_from_schedule, Toast.LENGTH_LONG)
                        .show();
            } else {
                Log.v(TAG, "Attending track :" + track_id);
                userEventSchedule.attend(track.getString("track_time"), track_id);
                Toast.makeText(getContext(), R.string.session_added_to_schedule, Toast.LENGTH_LONG)
                        .show();
            }
            // Updating reminders
            SettingsActivity settingsActivity = new SettingsActivity();
            settingsActivity.settingUpdated(getContext());
            getLoaderManager().restartLoader(0, null, this);
        }
    }
}
