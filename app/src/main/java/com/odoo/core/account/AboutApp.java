package com.odoo.core.account;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.odoo.R;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.utils.IntentUtils;
import com.odoo.core.utils.OAppBarUtils;
import com.odoo.core.utils.OResource;
import com.odoo.datas.OConstants;

public class AboutApp extends OdooCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_about_application);
        OAppBarUtils.setAppBar(this, true);
        setTitle(R.string.title_about);

        PackageManager packageManager = getPackageManager();
        TextView version = (TextView) findViewById(R.id.applicationVersion);
        try {
            version.setText(getString(R.string.app_version_name,
                    packageManager.getPackageInfo(getPackageName(), 0).versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        findViewById(R.id.writeToUs).setOnClickListener(this);
        findViewById(R.id.launchWebsite).setOnClickListener(this);
        findViewById(R.id.playStore).setOnClickListener(this);
        findViewById(R.id.githubRepository).setOnClickListener(this);
        findViewById(R.id.openTwitter).setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(OResource.color(this, R.color.colorPrimary));
        switch (view.getId()) {
            case R.id.writeToUs:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
                        "android@odoo.co.in", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_from_app));
                startActivity(Intent.createChooser(emailIntent, getString(R.string.send_feedback)));
                break;
            case R.id.launchWebsite:
                builder.build().launchUrl(this, Uri.parse("http://odoomobile.com"));
                break;
            case R.id.playStore:
                IntentUtils.openURLInBrowser(this, OConstants.URL_ODOO_APPS_ON_PLAY_STORE);
                break;
            case R.id.githubRepository:
                builder.build().launchUrl(this, Uri.parse(OConstants.URL_ODOO_MOBILE_GIT_HUB));
                break;
            case R.id.openTwitter:
                builder.build().launchUrl(this, Uri.parse("https://twitter.com/odoomobile"));
                break;
        }
    }
}
