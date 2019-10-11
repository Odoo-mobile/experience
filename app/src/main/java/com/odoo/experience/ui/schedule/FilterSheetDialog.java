package com.odoo.experience.ui.schedule;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.odoo.experience.R;
import com.odoo.experience.core.db.ORecord;
import com.odoo.experience.core.helper.OdooActivity;
import com.odoo.experience.core.utils.BitmapUtils;
import com.odoo.experience.core.utils.OBind;
import com.odoo.experience.database.models.EventTrackTags;
import com.odoo.experience.database.models.EventTrackTagsRel;
import com.odoo.experience.database.models.EventTracks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FilterSheetDialog extends OdooActivity implements View.OnClickListener, TextWatcher {

    private static final int DATA_TAGS = 1;
    private static final int DATA_LOCATION = 2;
    private String[] colors = {
            "#777777", "#F06050", "#F4A460", "#F7CD1F",
            "#6CC1ED", "#814968", "#EB7E7F", "#2C8397",
            "#475577", "#D6145F", "#30C381", "#9365B8"
    };

    private EventTrackTags tags;
    private EventTrackTagsRel tagsRel;
    private EventTracks tracks;
    private HashMap<String, ORecord> selectedItems = new HashMap<>();
    private List<Integer> tagIds = new ArrayList<>();
    private List<Integer> locationIds = new ArrayList<>();
    private List<ORecord> locations = new ArrayList<>();
    private List<ORecord> topics = new ArrayList<>();
    private EditText edtSearch;
    private Timer timer = new Timer();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_bottom_sheet);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setResult(RESULT_CANCELED);
        tags = new EventTrackTags(this);
        tracks = new EventTracks(this);
        tagsRel = new EventTrackTagsRel(this);
        findViewById(R.id.closeDialog).setOnClickListener(this);
        findViewById(R.id.bntResetFilters).setOnClickListener(this);

        Bundle extra = getIntent().getExtras();
        tagIds.addAll(extra.getIntegerArrayList("tag_ids"));
        locationIds.addAll(extra.getIntegerArrayList("location_ids"));
        edtSearch = findViewById(R.id.edtSearchFilters);
        edtSearch.addTextChangedListener(this);

        bindViews();
    }

    private void bindViews() {
        // tags
        topics = tags.select("color > ? and id in (select distinct tag_id from event_track_tags)", new String[]{"0"}, "name");
        // location
        locations = tracks.select(new String[]{"distinct location_id as id", "location_name as name"}, "location_name IS NOT NULL", new String[]{}, "location_name");
        // speakers
//        List<ORecord> speakers = tracks.select(new String[]{"distinct partner_id as id", "partner_name as name", "image"}, "partner_name IS NOT NULL and partner_name != ?", new String[]{"false"}, "partner_name");
//        bindRecords((ViewGroup) findViewById(R.id.flexSpeakers), speakers);
        bindRecords();
    }

    private void bindRecords() {
        // tags
        bindRecords((ViewGroup) findViewById(R.id.flexTopics), topics, DATA_TAGS);
        // locations
        bindRecords((ViewGroup) findViewById(R.id.flexLocations), locations, DATA_LOCATION);
    }

    private void bindRecords(ViewGroup parent, List<ORecord> records, int type) {
        parent.removeAllViews();
        String searchTerm = edtSearch.getText().toString().trim();
        for (ORecord record : records) {
            if (!searchTerm.isEmpty() && !record.getString("name")
                    .toLowerCase().contains(searchTerm.toLowerCase())) {
                continue;
            }
            View view = LayoutInflater.from(this)
                    .inflate(R.layout.xp_filter_tag_item_view, parent, false);
            record.put("_type", type);
            view.setTag(record);
            view.setOnClickListener(onItemClick);
            OBind.setText(view.findViewById(R.id.filterName), record.getString("name"));
            ImageView dot = view.findViewById(R.id.filterDotColor);
            if (record.contains("image")) {
                if (record.getString("image").equals("false")) {
                    dot.setImageResource(R.drawable.user);
                } else {
                    Bitmap bitmap = BitmapUtils.getBitmapImage(this, record.getString("image"));
                    dot.setImageBitmap(bitmap);
                }
            } else {
                int color = record.contains("color") ? record.getInt("color")
                        : 2;
                dot.setColorFilter(Color.parseColor(colors[color]));
            }

            switch (type) {
                case DATA_TAGS:
                    if (tagIds.indexOf(record.getInt("id")) != -1) {
                        updateSelected(record);
                        toggleSelected(view, record);
                    }
                    break;
                case DATA_LOCATION:
                    if (locationIds.indexOf(record.getInt("id")) != -1) {
                        updateSelected(record);
                        toggleSelected(view, record);
                    }
                    break;
            }

            FlexboxLayout.LayoutParams params = (FlexboxLayout.LayoutParams) view.getLayoutParams();
            params.setFlexGrow(1.0F);
            parent.addView(view);
        }
    }

    private View.OnClickListener onItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ORecord record = (ORecord) view.getTag();
            updateSelected(record);
            toggleSelected(view, record);
            switch (record.getInt("_type")) {
                case DATA_TAGS:
                    break;
                case DATA_LOCATION:
                    break;
            }
        }
    };

    private void updateSelected(ORecord record) {
        String key = "item_" + record.getInt("_type") + "_" + record.getInt("id");
        if (selectedItems.containsKey(key)) {
            selectedItems.remove(key);
        } else {
            selectedItems.put(key, record);
        }
    }

    private void toggleSelected(View view, ORecord record) {
        String key = "item_" + record.getInt("_type") + "_" + record.getInt("id");
        boolean isSelected = selectedItems.containsKey(key);
        GradientDrawable drawable = (GradientDrawable) view.getBackground();
        int color = record.contains("color") ? record.getInt("color") : 2;
        view.setBackgroundColor(Color.TRANSPARENT);
        if (isSelected) {
            drawable.setColorFilter(Color.parseColor(colors[color]), PorterDuff.Mode.SRC_IN);
            view.setBackground(drawable);
        } else {
            view.setBackgroundResource(R.drawable.xp_filter_item_view);
        }
        view.findViewById(R.id.filterDotColor).setVisibility(isSelected ? View.GONE : View.VISIBLE);
        view.findViewById(R.id.filterDeselect).setVisibility(isSelected ? View.VISIBLE : View.GONE);
        TextView filterName = view.findViewById(R.id.filterName);
        filterName.setTextColor(isSelected ? Color.WHITE : Color.BLACK);
        updateCount();
    }

    private List<ORecord> updateCount() {
        List<Integer> tagIds = new ArrayList<>();
        List<String> whereStrLocation = new ArrayList<>();
        List<String> argsVals = new ArrayList<>();
        for (String key : selectedItems.keySet()) {
            ORecord record = selectedItems.get(key);
            switch (record.getInt("_type")) {
                case DATA_TAGS:
                    tagIds.add(record.getInt("id"));
                    break;
                case DATA_LOCATION:
                    whereStrLocation.add("location_id = ?");
                    argsVals.add(record.getInt("id") + "");
                    break;
            }
        }

        boolean isLocation = !whereStrLocation.isEmpty();
        String where = (isLocation ? "(" : "") + TextUtils.join(" OR ", whereStrLocation) + (isLocation ? ")" : "");
        if (!tagIds.isEmpty()) {
            List<Integer> trackIds = tagsRel.getTrackIds(tagIds);
            if (!trackIds.isEmpty()) {
                where += whereStrLocation.isEmpty() ? "location_id != 'NULL' AND " : " AND ";
                where += "id IN (" + TextUtils.join(", ", trackIds) + ")";
            } else {
                findViewById(R.id.bntResetFilters).setVisibility(!selectedItems.isEmpty() ? View.GONE : View.VISIBLE);
                return new ArrayList<>();
            }
        }
        findViewById(R.id.bntResetFilters).setVisibility(selectedItems.isEmpty() ? View.GONE : View.VISIBLE);
        String[] args = argsVals.toArray(new String[argsVals.size()]);
        List<ORecord> count = tracks.select(new String[]{"id", "name"}, where, args, null);
        OBind.setText(findViewById(R.id.filterTitle), selectedItems.isEmpty() ? getString(R.string.label_filters)
                : getString(R.string.title_n_events, count.size()));
        OBind.setImage(findViewById(R.id.closeDialog), selectedItems.isEmpty() ? R.drawable.ic_keyboard_arrow_down_black_24dp :
                R.drawable.ic_done_black_24dp);
        return count;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bntResetFilters:
                selectedItems.clear();
                locationIds.clear();
                tagIds.clear();
                bindViews();
                updateCount();
                break;
            default:
                Intent intent = new Intent();
                ArrayList<Integer> tagIds = new ArrayList<>();
                ArrayList<Integer> locationIds = new ArrayList<>();
                if (!selectedItems.isEmpty()) {
                    for (String key : selectedItems.keySet()) {
                        ORecord record = selectedItems.get(key);
                        switch (record.getInt("_type")) {
                            case DATA_TAGS:
                                tagIds.add(record.getInt("id"));
                                break;
                            case DATA_LOCATION:
                                locationIds.add(record.getInt("id"));
                                break;
                        }
                    }
                }
                intent.putExtra("tag_ids", tagIds);
                intent.putExtra("location_ids", locationIds);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (timer != null) timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bindRecords();
                    }
                });
            }
        }, 550);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
