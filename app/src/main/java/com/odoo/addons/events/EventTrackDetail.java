package com.odoo.addons.events;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.odoo.R;
import com.odoo.SettingsActivity;
import com.odoo.addons.events.models.EventEvent;
import com.odoo.addons.events.models.EventTrack;
import com.odoo.addons.events.models.ExploreTracks;
import com.odoo.addons.events.models.UserEventSchedule;
import com.odoo.core.account.BaseSettings;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.OAppBarUtils;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.ODateUtils;
import com.odoo.core.utils.OResource;
import com.odoo.datas.OConstants;

import java.util.Date;

public class EventTrackDetail extends OdooCompatActivity implements View.OnClickListener {

    public static final String KEY_TRACK_ID = "track_id";
    public static final String KEY_NO_OTHER_CHOICE = "no_choice_option";
    private EventEvent event;
    private UserEventSchedule userEventSchedule;
    private ODataRow record;
    private EventTrack track;
    private boolean isOnGoing = false;
    private ExploreTracks exploreTracks;
    private MenuItem trackLikeMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail);
        OAppBarUtils.setAppBar(this, true);
        event = new EventEvent(this);
        userEventSchedule = new UserEventSchedule(this);
        track = new EventTrack(this);
        exploreTracks = new ExploreTracks(this);
        Bundle extra = getIntent().getExtras();
        record = track.browse(extra.getInt(KEY_TRACK_ID));

        if (extra.containsKey(KEY_NO_OTHER_CHOICE)) {
            findViewById(R.id.fab).setVisibility(View.GONE);
        } else {
            findViewById(R.id.fab).setOnClickListener(this);
            toggleAttendingButton(record.getInt(OColumn.ROW_ID));
        }

        setTitle("");
        TextView title, subTitle, desc;
        title = (TextView) findViewById(android.R.id.title);
        subTitle = (TextView) findViewById(R.id.sub_title);
        desc = (TextView) findViewById(R.id.desc);

        title.setText(record.getString("name"));
        if (record.getString("description").equals("false")) {
            record.put("description", getString(R.string.no_desc_yet));
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            desc.setText(Html.fromHtml(record.getString("description")));
        } else {
            desc.setText(Html.fromHtml(record.getString("description"), Html.FROM_HTML_MODE_LEGACY));
        }
        String time;

        if (BaseSettings.showConferenceTime(this)) {
            time = ODateUtils.convertToTimeZone(record.getString("date"),
                    ODateUtils.DEFAULT_FORMAT, event.getEventTimeZone(), "hh:mm a");
        } else {
            time = ODateUtils.convertToDefault(record.getString("date"), ODateUtils.DEFAULT_FORMAT, "hh:mm a");
        }
        String dayTime = "Day " + event.getDayNumber(record.getString("date"));
        dayTime += " / " + time;
        int duration = (Math.round(record.getFloat("duration") * 60));
        dayTime += " (" + duration + " min)";
        if (!record.getString("room").equals("false")) {
            dayTime += " \nin " + record.getString("room");
        }
        subTitle.setText(dayTime);

        TextView partnerBiography = (TextView) findViewById(R.id.partner_biography);
//        if (record.getString("partner_biography").equals("false")) {
        partnerBiography.setVisibility(View.GONE);
//        } else {
//            partnerBiography.setVisibility(View.VISIBLE);
//            partnerBiography.setText(StringUtils.htmlToString(record.getString("partner_biography")));
//        }
        // Adding sponsors
        final LinearLayout container = (LinearLayout) findViewById(R.id.speakers_profile_container);
        container.removeAllViews();
        addSpeakers(container);
        checkOnGoing();
    }

    private void toggleAttendingButton(int track_id) {
        ImageView imageView = (ImageView) findViewById(R.id.fab);
        imageView.setOnClickListener(this);
        if (userEventSchedule.attending(track_id)) {
            imageView.setImageResource(R.drawable.ic_check_done);
            imageView.setTag(true);
        } else {
            imageView.setImageResource(R.drawable.ic_action_content_add);
            imageView.setTag(null);
        }
    }

    private void checkOnGoing() {
        int duration = Math.round((record.getFloat("duration") * 60));
        Date trackStartDate = ODateUtils.createDateObject(record.getString("date"),
                ODateUtils.DEFAULT_FORMAT, false);
        Date trackEndDate = ODateUtils.getDateMinuteBefore(trackStartDate, duration * -1);
        Date now = new Date();
        if (trackStartDate.compareTo(now) < 0 && trackEndDate.compareTo(now) > 0) {
            findViewById(R.id.on_going_session_status).setVisibility(View.VISIBLE);
            isOnGoing = true;
        } else {
            findViewById(R.id.on_going_session_status).setVisibility(View.GONE);
            isOnGoing = false;
        }
    }

    private void addSpeakers(ViewGroup parent) {
        String name = "";
        String image = "";
        String websiteDesc = "";
        if (!record.getString("partner_id").equals("false")) {
            ODataRow speaker = record.getM2ORecord("partner_id").browse();
            image = speaker.getString("image_small");
            name = speaker.getString("name");
            websiteDesc = speaker.getString("website_description");
        } else {
            name = record.getString("partner_name");
        }
        View view = LayoutInflater.from(this).inflate(R.layout.speaker_detail_view, parent, false);
        if (!image.equals("false")) {
            Bitmap avatar = BitmapUtils.getBitmapImage(this, image);
            OControls.setImage(view, R.id.speakerAvatar, avatar);
        }
        OControls.setText(view, R.id.speaker_name, name);
        if (!websiteDesc.equals("false")) {
            TextView speaker_bio = (TextView) view.findViewById(R.id.speaker_bio);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                speaker_bio.setText(Html.fromHtml(websiteDesc));
            } else {
                speaker_bio.setText(Html.fromHtml(websiteDesc, Html.FROM_HTML_MODE_LEGACY));
            }
        }
        parent.addView(view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_detail, menu);
        trackLikeMenu = menu.findItem(R.id.action_like_track);
        toggleMenuLike(exploreTracks.count("id = ? and liked = ?", new String[]{record.getInt(OColumn.ROW_ID) + ""
                , "true"}) > 0);
        return super.onCreateOptionsMenu(menu);
    }

    private void toggleMenuLike(Boolean liked) {
        trackLikeMenu.setIcon(liked ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_launch_event_track_page:
                String url = OConstants.URL_ODOO + record.getString("website_url");
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(OResource.color(this, R.color.colorPrimary));
                builder.build().launchUrl(this, Uri.parse(url));
                break;
            case R.id.action_share_track:
                doShare();
                break;
            case R.id.action_like_track:
                int track_id = record.getInt(OColumn.ROW_ID);
                boolean liked = !(exploreTracks.count("id = ? and liked = ?", new String[]{track_id + ""
                        , "true"}) > 0);
                OValues values = new OValues();
                values.put("liked", liked);
                exploreTracks.update("id = ?", new String[]{track_id + ""}, values);
                Toast.makeText(EventTrackDetail.this, liked ? R.string.toast_track_liked
                        : R.string.toast_track_unliked, Toast.LENGTH_SHORT).show();
                toggleMenuLike(liked);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                if (view.getTag() == null) {
                    userEventSchedule.attend(record.getString("date"), record.getInt(OColumn.ROW_ID));
                    Toast.makeText(this, R.string.session_added_to_schedule, Toast.LENGTH_LONG)
                            .show();
                } else {
                    userEventSchedule.removeAttend(record.getInt(OColumn.ROW_ID));
                    Toast.makeText(this, R.string.session_removed_from_schedule, Toast.LENGTH_LONG)
                            .show();
                }
                toggleAttendingButton(record.getInt(OColumn.ROW_ID));

                // Updating reminders
                SettingsActivity settingsActivity = new SettingsActivity();
                settingsActivity.settingUpdated(this);
                break;
        }
    }

    private void doShare() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String text = "I'm attending '" + record.getString("name") + "' at ";
        text += OResource.string(this, R.string.hash_tag_odoo_exp) + " " + event.getEventYear();
        text += "\n\n" + OConstants.URL_ODOO + record.getString("website_url")
                + " \n\nShared with Odoo Experience App";
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(intent, "Share Session"));
    }
}
