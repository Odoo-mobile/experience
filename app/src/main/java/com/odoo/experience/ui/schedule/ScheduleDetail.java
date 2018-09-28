package com.odoo.experience.ui.schedule;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.odoo.experience.AppConfig;
import com.odoo.experience.R;
import com.odoo.experience.core.db.ORecord;
import com.odoo.experience.core.helper.OdooActivity;
import com.odoo.experience.core.utils.BitmapUtils;
import com.odoo.experience.core.utils.OBind;
import com.odoo.experience.core.utils.ODateUtils;
import com.odoo.experience.core.utils.StringUtils;
import com.odoo.experience.database.models.EventTrackTagsRel;
import com.odoo.experience.database.models.EventTracks;
import com.odoo.experience.database.models.ResPartner;

import java.util.HashMap;
import java.util.List;

public class ScheduleDetail extends OdooActivity implements View.OnClickListener {
    public static final String KEY_TRACK_ID = "track_id";
    private EventTracks tracks;
    private ORecord record;
    private EventTrackTagsRel tags;
    private String[] colors = {
            "#777777", "#F06050", "#F4A460", "#F7CD1F",
            "#6CC1ED", "#814968", "#EB7E7F", "#2C8397",
            "#475577", "#D6145F", "#30C381", "#9365B8"
    };

    private HashMap<Integer, Integer> speakerImages = new HashMap<>();
    private int[] bgImages = {
            R.drawable.detail_cover_1,
            R.drawable.detail_cover_2,
            R.drawable.detail_cover_3,
            R.drawable.detail_cover_4,
            R.drawable.detail_cover_5,
            R.drawable.detail_cover_6,
            R.drawable.detail_cover_7,
            R.drawable.detail_cover_8,
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xp_schedule_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        // Setting partner specific cover
        /*
            415299 - AL
            958661, 25434 - FP
            63446 - ODO
            1806450 - Patrik
            153969 - GED
            958431 - MGA
            548436 - FGI
         */
        speakerImages.put(415299, R.drawable.al); // AL
        speakerImages.put(958661, R.drawable.fp); // FP
        speakerImages.put(25434, R.drawable.fp); // FP
        speakerImages.put(63446, R.drawable.odo); // ODO
        speakerImages.put(1806450, R.drawable.patric); // Patrik
        speakerImages.put(153969, R.drawable.ged); // GED
        speakerImages.put(958431, R.drawable.mga); // MGA
        speakerImages.put(548436, R.drawable.fgi); // FGI

        tracks = new EventTracks(this);
        tags = new EventTrackTagsRel(this);
        record = tracks.get(getIntent().getExtras().getInt(KEY_TRACK_ID));
        if (record == null) {
            finish();
            return;
        }
        setTitle("");

        int location_id = record.getInt("location_id");

        int coverImage = speakerImages.containsKey(record.getInt("partner_id")) ?
                speakerImages.get(record.getInt("partner_id")) : bgImages[record.getInt("id") % 8];

        OBind.setImage(findViewById(R.id.mainBackdrop), coverImage);
        OBind.setText(findViewById(R.id.trackTitle), record.getString("name"));

        String newDate = ODateUtils.addDurationInDate(record.getString("date"),
                record.getFloat("duration"));
        String duration = ODateUtils.floatToDuration(record.getString("duration"));

        String part1 = ODateUtils.convertToDefault(record.getString("date"), ODateUtils.DEFAULT_FORMAT,
                "MMM dd, hh:mm a");
        String part2 = ODateUtils.convertToDefault(newDate, ODateUtils.DEFAULT_FORMAT, "hh:mm a");
        OBind.setText(findViewById(R.id.trackDateAndTime),
                getString(R.string.track_detail_time_date, part1, part2, duration));
        if (!record.getString("location_name").equals("false")) {
            OBind.setText(findViewById(R.id.trackLocation), record.getString("location_name"));
        } else {
            findViewById(R.id.trackLocationContainer).setVisibility(View.GONE);
        }
        OBind.setText(findViewById(R.id.trackDescription),
                StringUtils.stringToHtml(record.getString("description")));
        bindTags(tags.getRelRecords(record.getInt("id")), (ViewGroup) findViewById(R.id.trackTags));

        if (!record.getString("partner_name").equals("false")) {
            findViewById(R.id.speakerContainer).setVisibility(View.VISIBLE);
            OBind.setText(findViewById(R.id.partnerName), record.getString("partner_name"));
            if (!record.getString("image").equals("false")) {
                Bitmap bitmap = BitmapUtils.getBitmapImage(this, record.getString("image"));
                ImageView imageView = findViewById(R.id.partnerImage);
                imageView.setImageBitmap(bitmap);
            }
            ORecord partner = new ResPartner(this).get(record.getInt("partner_id"));
            if (partner != null) {
                String strDesc = StringUtils.htmlToString(partner.getString("website_description"));
                if (!strDesc.isEmpty() && !strDesc.equals("false")) {
                    OBind.setText(findViewById(R.id.partnerDescription),
                            StringUtils.stringToHtml(partner.getString("website_description")));
                } else {
                    findViewById(R.id.partnerDescription).setVisibility(View.GONE);
                }
            } else {
                findViewById(R.id.partnerDescription).setVisibility(View.GONE);
            }
        }
        makeStarred();
        findViewById(R.id.btnStarTrack).setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_share_track:
                doShare();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void doShare() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String text = "I'm attending '" + record.getString("name") + "' at ";
        text += AppConfig.TWITTER_HASH_TAG_URL + " " + AppConfig.EVENT_YEAR;
        text += "\n\n" + AppConfig.ODOO_URL + record.getString("website_url")
                + " \n\nShared with Odoo Experience App";
        intent.putExtra(Intent.EXTRA_SUBJECT, "Odoo Experience " + AppConfig.EVENT_YEAR);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(intent, "Share Session"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.xp_schedule_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void bindTags(List<ORecord> tags, ViewGroup parent) {
        parent.removeAllViews();
        if (tags.isEmpty()) {
            parent.setVisibility(View.GONE);
        } else {
            parent.setVisibility(View.VISIBLE);
            for (ORecord record : tags) {
                if (record.getInt("color") > 0) {
                    View view = LayoutInflater.from(this)
                            .inflate(R.layout.track_tag_item_view, parent, false);
                    OBind.setText(view.findViewById(R.id.trackTagName), record.getString("name"));
                    ImageView imageView = view.findViewById(R.id.trackTagColors);
                    imageView.setColorFilter(Color.parseColor(colors[record.getInt("color")]));
                    parent.addView(view);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        boolean toStar = record.getInt("selected") == 0;
        ContentValues values = new ContentValues();
        values.put("selected", toStar ? 1 : 0);
        tracks.update(values, "id = ?", record.getString("id"));
        record.put("selected", toStar ? 1 : 0);
        makeStarred();
        Toast.makeText(this,
                toStar ? R.string.track_added_to_starred :
                        R.string.track_removed_from_starred, Toast.LENGTH_SHORT).show();
    }

    private void makeStarred() {
        FloatingActionButton btn = findViewById(R.id.btnStarTrack);
        btn.setImageResource(record.getInt("selected") == 1 ? R.drawable.ic_action_starfill : R.drawable.ic_action_star);
    }
}
