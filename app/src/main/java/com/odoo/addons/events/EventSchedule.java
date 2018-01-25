package com.odoo.addons.events;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.odoo.R;
import com.odoo.addons.events.models.EventEvent;
import com.odoo.addons.events.models.EventTrack;
import com.odoo.addons.events.models.UserEventSchedule;
import com.odoo.core.account.BaseSettings;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.ODateUtils;
import com.odoo.core.utils.StringUtils;

import java.util.Date;
import java.util.List;

import odoo.controls.recycler.EasyRecyclerView;
import odoo.controls.recycler.EasyRecyclerViewAdapter;

public class EventSchedule extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        EasyRecyclerViewAdapter.OnViewBindListener, EasyRecyclerViewAdapter.OnItemViewClickListener, View.OnClickListener {

    private EventTrack track;
    private EventEvent eventEvent;
    private UserEventSchedule userEventSchedule;
    private EasyRecyclerView scheduleTracksView;
    private String eventTimeZone;
    private ODataRow event;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        destroyTabLayout = false;
        setTitle("Schedule");
        return inflater.inflate(R.layout.event_schedule_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        track = new EventTrack(getContext());
        eventEvent = new EventEvent(getContext());
        event = eventEvent.getEvent();
        eventTimeZone = eventEvent.getEventTimeZone();
        userEventSchedule = new UserEventSchedule(getContext());
        scheduleTracksView = (EasyRecyclerView) findViewById(R.id.scheduleTracks);
        scheduleTracksView.linear();
        scheduleTracksView.setLayout(R.layout.event_schedule_track_item_view);
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        return null;
    }

    @Override
    public Class<UserEventSchedule> database() {
        return UserEventSchedule.class;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        int day = getArguments().getInt(EventScheduleDayPager.KEY_DAY);
        boolean isTalks = getArguments().getBoolean(EventScheduleDayPager.KEY_HAS_TALKS);
        return new CursorLoader(getContext(), track.dayFilter(isTalks, day), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        scheduleTracksView.changeCursor(data);
        scheduleTracksView.setOnViewBindListener(this);
        scheduleTracksView.setOnItemViewClickListener(this);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        scheduleTracksView.changeCursor(null);
    }

    private boolean isTalks(String date) {
        Date eventDate = ODateUtils.createDateObject(event.getString("date_begin"),
                ODateUtils.DEFAULT_FORMAT, false);
        Date trackDate = ODateUtils.createDateObject(date,
                ODateUtils.DEFAULT_FORMAT, false);
        return eventDate.compareTo(trackDate) > 0;
    }

    @Override
    public void onViewBind(int position, View view, ODataRow data) {
        String title = data.getString("name");
        String track_time;
        if (BaseSettings.showConferenceTime(getContext())) {
            track_time = ODateUtils.convertToTimeZone(data.getString("time"),
                    ODateUtils.DEFAULT_FORMAT, eventTimeZone, "hh:mm a");
        } else {
            track_time = ODateUtils.convertToDefault(data.getString("time"),
                    ODateUtils.DEFAULT_FORMAT, "hh:mm a");
        }
        String duration = "";
        if (!data.getString("duration").equals("false")) {
            duration = (Math.round(data.getFloat("duration") * 60)) + " min ";
        }
        if (!data.getString("description").equals("false"))
            duration += " / " + StringUtils.htmlToString(data.getString("description"));

        view.findViewById(R.id.changeSelectedTrack).setVisibility(View.GONE);
        view.findViewById(R.id.trackBreak).setVisibility(View.GONE);
        view.findViewById(R.id.trackDetail).setVisibility(View.VISIBLE);
        view.findViewById(R.id.scheduleTracksView).setVisibility(View.GONE);
        view.findViewById(R.id.no_track_selected).setVisibility(View.GONE);
        if (data.getString("room").equals("false") || isTalks(data.getString("time"))) {
            if (data.getString("partner_name").equals("false")) {
                view.findViewById(R.id.trackBreak).setVisibility(View.VISIBLE);
                view.findViewById(R.id.trackDetail).setVisibility(View.GONE);
                OControls.setText(view, R.id.breakTitle, data.getString("name"));
                OControls.setText(view, R.id.breakTime, track_time);
                OControls.setText(view, R.id.breakDuration, duration);
            } else {
                view.findViewById(R.id.selectTrack).setVisibility(View.GONE);
                view.findViewById(R.id.scheduleTracksView).setVisibility(View.VISIBLE);
                OControls.setText(view, R.id.trackTitle, title);
                OControls.setText(view, R.id.trackDuration, duration);
                OControls.setText(view, R.id.speakerName, data.getString("partner_name"));
            }
        } else {
            ODataRow session;
            session = userEventSchedule.getSession(data.getString("ddate"),
                    ODateUtils.parseDate(data.getString("time"),
                            ODateUtils.DEFAULT_FORMAT, ODateUtils.DEFAULT_TIME_FORMAT));
            if (session != null) {
                title = session.getString("name");
                if (!session.getString("duration").equals("false")) {
                    duration = (Math.round(session.getFloat("duration") * 60)) + " min ";
                }
                if (!session.getString("description").equals("false"))
                    duration += " / " + StringUtils.htmlToString(session.getString("description"));
                view.findViewById(R.id.selectTrack).setVisibility(View.GONE);
                view.findViewById(R.id.scheduleTracksView).setVisibility(View.VISIBLE);
                view.findViewById(R.id.trackTitle).setTag(session);
                OControls.setText(view, R.id.speakerName, session.getString("partner_name"));
                OControls.setText(view, R.id.trackTitle, title);
                OControls.setText(view, R.id.trackDuration, duration);
                View editSession = view.findViewById(R.id.changeSelectedTrack);
                editSession.setVisibility(View.VISIBLE);
                editSession.setTag(data);
                editSession.setOnClickListener(this);
            } else {
                view.findViewById(R.id.trackTitle).setTag(null);
                view.findViewById(R.id.selectTrack).setVisibility(View.VISIBLE);
                view.findViewById(R.id.no_track_selected).setVisibility(View.VISIBLE);
            }
        }
        OControls.setText(view, R.id.trackTime, track_time);
    }

    @Override
    public void onItemViewClick(int position, View view, ODataRow data) {
        if (data.getString("room").equals("false") || isTalks(data.getString("time"))) {
            if (!data.getString("partner_name").equals("false")) {
                Intent trackDetail = new Intent(getContext(), EventTrackDetail.class);
                trackDetail.putExtra(EventTrackDetail.KEY_TRACK_ID, data.getInt(OColumn.ROW_ID));
                trackDetail.putExtra(EventTrackDetail.KEY_NO_OTHER_CHOICE, true);
                startActivity(trackDetail);
            }
            // Else Break time enjoy ☺️
        } else {
            ODataRow session = (ODataRow) view.findViewById(R.id.trackTitle).getTag();
            if (session != null) {
                Intent trackDetail = new Intent(getContext(), EventTrackDetail.class);
                trackDetail.putExtra(EventTrackDetail.KEY_TRACK_ID, session.getInt(OColumn.ROW_ID));
                startActivity(trackDetail);
            } else {
                exploreEvent(data.getString("time"));
            }
        }
    }

    private void exploreEvent(String date) {
        EventExplore explore = new EventExplore();
        Bundle exploreData = new Bundle();
        exploreData.putInt(EventScheduleDayPager.KEY_DAY,
                getArguments().getInt(EventScheduleDayPager.KEY_DAY));
        exploreData.putString("date", date);
        explore.setArguments(exploreData);
        startFragment(explore, true);
    }

    @Override
    public void onResume() {
        destroyTabLayout = false;
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onClick(View view) {
        ODataRow data = (ODataRow) view.getTag();
        if (data != null) {
            exploreEvent(data.getString("time"));
        }
    }
}