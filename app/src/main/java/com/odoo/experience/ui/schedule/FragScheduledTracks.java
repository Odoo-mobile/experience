package com.odoo.experience.ui.schedule;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.odoo.experience.R;
import com.odoo.experience.core.db.ORecord;
import com.odoo.experience.core.helper.OdooFragment;
import com.odoo.experience.core.utils.NetworkUtils;
import com.odoo.experience.core.utils.OBind;
import com.odoo.experience.core.utils.ODateUtils;
import com.odoo.experience.database.models.EventTrackTagsRel;
import com.odoo.experience.database.models.EventTracks;
import com.odoo.experience.services.OdooDataService;
import com.odoo.experience.ui.schedule.utils.SessionReminder;
import com.odoo.experience.widget.recycler.RVHolder;
import com.odoo.experience.widget.recycler.RecyclerAdapter;
import com.odoo.experience.widget.recycler.TimeHeaderDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FragScheduledTracks extends OdooFragment implements SwipeRefreshLayout.OnRefreshListener,
        RecyclerAdapter.OnItemViewBindListener, RecyclerAdapter.OnItemViewClickListener, FragmentXPSchedule.OnFilterDataChangedListener {
    public static final String KEY_DATE = "date_value";
    private SwipeRefreshLayout swipeRefreshLayout;
    private EventTracks tracks;
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private EventTrackTagsRel tags;
    private HashMap<Integer, List<ORecord>> tagsList = new HashMap<>();
    private String[] colors = {
            "#777777", "#F06050", "#F4A460", "#F7CD1F",
            "#6CC1ED", "#814968", "#EB7E7F", "#2C8397",
            "#475577", "#D6145F", "#30C381", "#9365B8"
    };

    private FragmentXPSchedule xpSchedule;
    private List<Integer> filterTagIds = new ArrayList<>();
    private List<Integer> filterLocationIds = new ArrayList<>();
    private ViewSwitcher viewSwitcher;
    private SessionReminder sessionReminder;


    public void setFragmentXP(FragmentXPSchedule schedule) {
        xpSchedule = schedule;
    }

    @Override
    public int getViewResourceId() {
        return R.layout.screen_sp_schedule_tracks;
    }

    @Override
    public void onViewReady(View view) {
        sessionReminder = new SessionReminder(getContext());
        viewSwitcher = findViewById(R.id.viewSwitcher);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(this);

        tracks = new EventTracks(getContext());
        tags = new EventTrackTagsRel(getContext());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private List<ORecord> getRecords() {
        String where = "DATE(date) = ?";
        List<String> argVals = new ArrayList<>();
        argVals.add(getArguments().getString(KEY_DATE));
        if (!filterLocationIds.isEmpty()) {
            List<String> locationWhr = new ArrayList<>();
            for (Integer id : filterLocationIds) {
                locationWhr.add("location_id = ?");
                argVals.add(id + "");
            }
            where += " AND (" + TextUtils.join(" OR ", locationWhr) + ")";
        }
        if (!filterTagIds.isEmpty()) {
            where += " AND id IN (select track_id from " + tags.getTableName()
                    + " where tag_id IN (" + TextUtils.join(", ", filterTagIds) + "))";
        }
        String[] args = argVals.toArray(new String[argVals.size()]);
        return tracks.select(where, args, "datetime(date)");
    }

    @Override
    public void onRefresh() {
        if (NetworkUtils.isConnected(getContext())) {
            Intent intent = new Intent(getContext(), OdooDataService.class);
            intent.putExtra(OdooDataService.KEY_SYNC_REQUEST_FOR, OdooDataService.SYNC_TRACKS);
            getActivity().startService(intent);
        }
    }

    private void showRecords() {
        List<ORecord> records = getRecords();
        if (recyclerView.getItemDecorationAt(0) != null) {
            recyclerView.removeItemDecoration(recyclerView.getItemDecorationAt(0));
        }
        adapter = new RecyclerAdapter(getContext(), records);
        adapter.setLayoutResId(R.layout.track_item_row_view, this);
        adapter.setOnItemViewClickListener(this);
        recyclerView.setAdapter(adapter);

        if (records.isEmpty()) {
            if (viewSwitcher.getNextView().getId() == R.id.emptyView) {
                viewSwitcher.showNext();
            }
        } else {
            if (viewSwitcher.getNextView().getId() == R.id.swipeRefreshLayout) {
                viewSwitcher.showNext();
            }
        }
        TimeHeaderDecoration decoration = new TimeHeaderDecoration(getContext(), records) {

            @Override
            public void createTimeSlots(List<ORecord> records) {
                List<String> timeSlotDates = new ArrayList<>();
                for (ORecord record : records) {
                    String date = record.getString("date");
                    if (timeSlotDates.indexOf(date) < 0) {
                        timeSlotDates.add(date);
                        timeSlots.put(records.indexOf(record), createTimeHeader(date));
                    }
                }
            }

            public StaticLayout createTimeHeader(String dateTime) {
                Typeface font = ResourcesCompat.getFont(getContext(), R.font.montserrat);

                String date = ODateUtils.convertToDefault(dateTime, ODateUtils.DEFAULT_FORMAT, "hh:mm a");
                SpannableStringBuilder dateObj = new SpannableStringBuilder(date.split(" ")[0]);
                dateObj.setSpan(new AbsoluteSizeSpan(Float.valueOf(getResources().getDimension(R.dimen.font_xx)).intValue()), 0, dateObj.length(), 33);
                dateObj.setSpan(new StyleSpan(Typeface.BOLD), 0, dateObj.length(), 33);

                // am/pm
                SpannableStringBuilder sb = new SpannableStringBuilder(date.split(" ")[1]);
                sb.setSpan(new StyleSpan(Typeface.NORMAL), 0, sb.length(), 33);
                sb.setSpan(new AbsoluteSizeSpan(Float.valueOf(getResources().getDimension(R.dimen.font_x)).intValue()), 0, sb.length(), 33);
                dateObj.append(System.lineSeparator());
                dateObj.append(sb);
                paint.setTypeface(font);
                return new StaticLayout(dateObj, paint, width, Layout.Alignment.ALIGN_CENTER, 1f, 0f, false);
            }
        };
        decoration.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        decoration.setTextSize(Float.valueOf(getResources().getDimension(R.dimen.dimen_50)).intValue());
        recyclerView.addItemDecoration(decoration);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(syncStatus, new IntentFilter(OdooDataService.TAG));
        showRecords();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && getContentView() != null) {
            // load data here
            showRecords();
        }
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
                    //TODO: Update data
                    break;
                case OdooDataService.SYNC_STATUS_NO_NETWORK:
                    break;
            }

        }
    };

    @Override
    public void onItemViewBind(final ORecord record, int position, final RVHolder holder) {
        OBind.setText(holder.itemView.findViewById(R.id.trackTitle), record.getString("name"));
        String duration = ODateUtils.floatToDuration(record.getString("duration"));
        String durationLocation = record.getString("location_name").equals("false") ?
                duration.contains(":") ? getString(R.string.str_track_duration_hour_only, duration)
                        : getString(R.string.str_track_duration_only, duration) :
                getString(R.string.str_track_duration_location, duration, record.getString("location_name"));
        OBind.setText(holder.itemView.findViewById(R.id.trackDurationLocation), durationLocation);
        bindTags(getTags(record.getInt("id")), (ViewGroup) holder.itemView.findViewById(R.id.trackTags));

        final ImageView starred = holder.itemView.findViewById(R.id.trackStarred);
        starred.setVisibility(record.getString("partner_name").equals("false")
                ? View.GONE : View.VISIBLE);
        starred.setImageResource(tracks.isStarred(record.getInt("id")) ?
                R.drawable.ic_action_starfill : R.drawable.ic_action_star);
        starred.setColorFilter(tracks.isStarred(record.getInt("id")) ?
                ContextCompat.getColor(getContext(), R.color.colorAccent) : Color.parseColor("#414141"));
        starred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleStarred(starred, record);
            }
        });
        TextView title = holder.itemView.findViewById(R.id.trackTitle);
        TextView locationDuration = holder.itemView.findViewById(R.id.trackDurationLocation);
        if (!record.getString("partner_name").equals("false")) {
            title.setTextColor(Color.BLACK);
            holder.itemView.setClickable(true);
            locationDuration.setTextColor(Color.parseColor("#aa000000"));
        } else {
            title.setTextColor(Color.parseColor("#88414141"));
            locationDuration.setTextColor(Color.parseColor("#88414141"));
            holder.itemView.setClickable(false);
        }
    }

    private void toggleStarred(ImageView starred, ORecord record) {
        boolean toStar = !tracks.isStarred(record.getInt("id"));
        starred.setColorFilter(toStar ?
                ContextCompat.getColor(getContext(), R.color.colorAccent) : Color.parseColor("#414141"));
        ContentValues values = new ContentValues();
        values.put("selected", toStar ? 1 : 0);
        int count = tracks.update(values, "id = ?", record.getInt("id") + "");
        if (count > 0) {
            Bundle data = new Bundle();
            data.putInt("track_id", record.getInt("id"));
            data.putString("track_description", record.getString("description"));
            if (toStar) {
                sessionReminder.setReminder(record.getInt("id"), record.getString("date"), data);
            } else {
                sessionReminder.cancelReminder(record.getInt("id"), record.getString("date"), data);
            }
            starred.setImageResource(toStar ?
                    R.drawable.ic_action_starfill : R.drawable.ic_action_star);
            Toast.makeText(getContext(),
                    toStar ? R.string.track_added_to_starred :
                            R.string.track_removed_from_starred, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(),
                    R.string.unable_to_star, Toast.LENGTH_SHORT).show();
        }
    }

    private void bindTags(List<ORecord> tags, ViewGroup parent) {
        parent.removeAllViews();
        if (tags.isEmpty()) {
            parent.setVisibility(View.GONE);
        } else {
            parent.setVisibility(View.VISIBLE);
            for (ORecord record : tags) {
                if (record.getInt("color") > 0) {
                    View view = LayoutInflater.from(getContext())
                            .inflate(R.layout.track_tag_item_view, parent, false);
                    OBind.setText(view.findViewById(R.id.trackTagName), record.getString("name"));
                    ImageView imageView = view.findViewById(R.id.trackTagColors);
                    imageView.setColorFilter(Color.parseColor(colors[record.getInt("color")]));
                    parent.addView(view);
                }
            }
        }
    }

    private List<ORecord> getTags(int track_id) {
        if (tagsList.containsKey(track_id)) {
            return tagsList.get(track_id);
        }
        List<ORecord> tagItems = tags.getRelRecords(track_id);
        tagsList.put(track_id, tagItems);
        return tagItems;
    }

    @Override
    public void onItemViewClick(RVHolder holder, int position, ORecord record) {
        if (!record.getString("partner_name").equals("false")) {
            Intent intent = new Intent(getContext(), ScheduleDetail.class);
            intent.putExtra(ScheduleDetail.KEY_TRACK_ID, record.getInt("id"));
            startActivity(intent);
        }
    }

    @Override
    public void onFilterDataChanged() {
        filterLocationIds = xpSchedule.getFilterLocationIds();
        filterTagIds = xpSchedule.getFilterTagIds();
        if (tags != null)
            showRecords();
    }
}
