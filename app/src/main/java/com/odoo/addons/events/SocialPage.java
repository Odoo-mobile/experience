package com.odoo.addons.events;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.odoo.R;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.datas.OConstants;

import java.util.List;

public class SocialPage extends BaseFragment implements View.OnClickListener {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.social_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.socialGooglePlus).setOnClickListener(this);
        view.findViewById(R.id.socialTwitter).setOnClickListener(this);
        view.findViewById(R.id.plusURL).setOnClickListener(this);
        view.findViewById(R.id.twitterPage).setOnClickListener(this);
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        return null;
    }

    @Override
    public <T> Class<T> database() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.socialGooglePlus:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(OConstants.PLUS_HASH_TAG_URL)));
                break;
            case R.id.socialTwitter:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(OConstants.TWITTER_HASH_TAG_URL)));
                break;
            case R.id.plusURL:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(OConstants.GOOGLE_PLUS_URL)));
                break;
            case R.id.twitterPage:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(OConstants.TWITTER_PAGE)));
                break;
        }
    }
}
