package com.odoo.addons.events;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.odoo.R;
import com.odoo.addons.events.models.EventTrack;
import com.odoo.addons.events.models.ExploreTracks;
import com.odoo.addons.sponsor.Sponsors;
import com.odoo.addons.sponsor.models.EventSponsor;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.utils.OResource;

import java.util.ArrayList;
import java.util.List;

public class ExperienceSessions extends BaseFragment {

    public static final String TAG = ExperienceSessions.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.session_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.e(TAG, "Total Tracks: " + db().count(null, null));
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        setContext(context);
        List<ODrawerItem> menu = new ArrayList<>();
        menu.add(new ODrawerItem(TAG)
                .setTitle(_s(R.string.label_my_schedule))
                .setInstance(new EventScheduleDayPager())
                .setIcon(R.drawable.ic_icon_alarm));
        menu.add(new ODrawerItem(TAG)
                .setTitle("Explore")
                .setInstance(new EventExplore())
                .setIcon(R.drawable.ic_icon_explore));
        menu.add(new ODrawerItem(TAG)
                .setTitle("Sponsors")
                .setInstance(new Sponsors())
                .setCounter(new EventSponsor(context).count(null, null))
                .setIcon(R.drawable.ic_icon_people));
        Bundle likes = new Bundle();
        likes.putBoolean(EventExplore.KEY_LIKES_TRACKS, true);
        menu.add(new ODrawerItem(TAG)
                .setTitle("Likes")
                .setInstance(new EventExplore())
                .setCounter(new ExploreTracks(context).count("liked = ?", new String[]{"true"}))
                .setIcon(R.drawable.ic_favorite)
                .setExtra(likes));

        /* Practical information */
        menu.add(new ODrawerItem(TAG).setGroupTitle());
        menu.add(new ODrawerItem(TAG)
                .setTitle("Practical Information")
                .setInstance(PracticalInformation.class)
                .setIcon(R.drawable.ic_location));

        /* social */
        menu.add(new ODrawerItem(TAG).setGroupTitle());
        menu.add(new ODrawerItem(TAG)
                .setTitle("Social")
                .setInstance(new SocialPage())
                .setIcon(R.drawable.ic_icon_whatshot));

        // Sharing app
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, OResource.string(context, R.string.odoo_experience));
        intent.putExtra(Intent.EXTRA_TEXT, "Let me recommended you this application for Odoo Experience\n\n" +
                "https://play.google.com/store/apps/details?id=com.odoo.experience\n\n");
        menu.add(new ODrawerItem(TAG)
                .setTitle("Share app")
                .setInstance(intent)
                .setIcon(R.drawable.ic_icon_share));

        return menu;
    }

    @Override
    public Class<EventTrack> database() {
        return EventTrack.class;
    }
}
