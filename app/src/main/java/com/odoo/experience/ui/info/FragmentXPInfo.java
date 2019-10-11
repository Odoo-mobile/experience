package com.odoo.experience.ui.info;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.odoo.experience.R;
import com.odoo.experience.core.helper.OdooFragment;

public class FragmentXPInfo extends OdooFragment implements View.OnClickListener {

    public static final int GET_THERE_VIEW_ID = 1;
    public static final int CONTACT_VIEW_ID = 2;
    public static final int TAXIS_VIEW_ID = 3;
    public static final int WIFI_VIEW_ID = 4;

    @Override
    public int getViewResourceId() {
        setHasOptionsMenu(true);
        return R.layout.screen_xp_info_travel;
    }

    @Override
    public void onViewReady(View view) {
        resetTabLayout();
        findViewById(R.id.btnViewAccessMap).setOnClickListener(this);
        findViewById(R.id.btnAccessMap).setOnClickListener(this);
        findViewById(R.id.btnParkingInformation).setOnClickListener(this);
        findViewById(R.id.btnBusWebsite).setOnClickListener(this);
        findViewById(R.id.btnTrainSchedules).setOnClickListener(this);

        //Taxi Web
        findViewById(R.id.btnTaxiLLNWeb).setOnClickListener(this);
        findViewById(R.id.btnTaxiBlancsWeb).setOnClickListener(this);
        findViewById(R.id.btnTaxiBroeckxWeb).setOnClickListener(this);
        findViewById(R.id.btnTaxiClerboisWeb).setOnClickListener(this);
        findViewById(R.id.btnTaxiClaudeWeb).setOnClickListener(this);
        findViewById(R.id.btnTaxiDyleWeb).setOnClickListener(this);
        findViewById(R.id.btnTaxiSocialWeb).setOnClickListener(this);
        findViewById(R.id.btnTaxiBusWeb).setOnClickListener(this);

        // Taxi Call
        findViewById(R.id.btnTaxiLLNCall).setOnClickListener(this);
        findViewById(R.id.btnTaxiBlancsCall).setOnClickListener(this);
        findViewById(R.id.btnTaxiCDCall).setOnClickListener(this);
        findViewById(R.id.btnTaxiTheoCall).setOnClickListener(this);
        findViewById(R.id.btnTaxiBroeckxCall).setOnClickListener(this);
        findViewById(R.id.btnTaxiAZCall).setOnClickListener(this);
        findViewById(R.id.btnTaxiClerboisCall).setOnClickListener(this);
        findViewById(R.id.btnTaxiClaudeCall).setOnClickListener(this);
        findViewById(R.id.btnTaxiDyleCall).setOnClickListener(this);
        findViewById(R.id.btnTaxiSocialCall).setOnClickListener(this);

        // Mail
        findViewById(R.id.btnTaxiTheoMail).setOnClickListener(this);
        findViewById(R.id.btnTaxiBroeckxMail).setOnClickListener(this);
        findViewById(R.id.btnTaxiAZMail).setOnClickListener(this);


        // Expand/Collapse
        findViewById(R.id.viewGetThere).setOnClickListener(this);
        findViewById(R.id.viewContact).setOnClickListener(this);
        findViewById(R.id.viewTaxis).setOnClickListener(this);
        findViewById(R.id.viewWifi).setOnClickListener(this);

        findViewById(R.id.contactUs).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnViewAccessMap:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.google.com/maps/d/u/0/viewer?ll=50.668504,4.610996&spn=0.00476,0.00912&t=m&gl=be&msa=0&z=16&source=embed&ie=UTF8&mid=11uU159VhDtOsdjKq6anyx08xAG0"));
                startActivity(intent);
                break;
            case R.id.btnTrainSchedules:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.belgianrail.be/en/Splash.aspx?ReturnUrl=http%3a%2f%2fwww.belgianrail.be%2fen%2fDefault.aspx"));
                startActivity(intent);
                break;
            case R.id.btnParkingInformation:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.parkme.com/fr/lot/137927/parking-grand-place-louvain-la-neuve-louvain-la-neuve-belgium"));
                startActivity(intent);
                break;
            case R.id.btnAccessMap:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.google.com/maps/d/u/0/viewer?ll=50.668504,4.610996&spn=0.00476,0.00912&t=m&gl=be&msa=0&z=16&source=embed&ie=UTF8&mid=11uU159VhDtOsdjKq6anyx08xAG0"));
                startActivity(intent);
                break;
            case R.id.btnBusWebsite:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.infotec.be/Medeplacer/Rechercheditin%C3%A9raire.aspx"));
                startActivity(intent);
                break;
            case R.id.contactUs:
            case R.id.btnTaxiLLNCall:
            case R.id.btnTaxiBlancsCall:
            case R.id.btnTaxiCDCall:
            case R.id.btnTaxiTheoCall:
            case R.id.btnTaxiBroeckxCall:
            case R.id.btnTaxiAZCall:
            case R.id.btnTaxiClerboisCall:
            case R.id.btnTaxiClaudeCall:
            case R.id.btnTaxiDyleCall:
            case R.id.btnTaxiSocialCall:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("tel:" + view.getTag().toString()));
                startActivity(Intent.createChooser(intent, "Select"));
                break;
            case R.id.btnTaxiLLNWeb:
            case R.id.btnTaxiBlancsWeb:
            case R.id.btnTaxiBroeckxWeb:
            case R.id.btnTaxiClerboisWeb:
            case R.id.btnTaxiClaudeWeb:
            case R.id.btnTaxiDyleWeb:
            case R.id.btnTaxiSocialWeb:
            case R.id.btnTaxiBusWeb:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(view.getTag().toString()));
                startActivity(intent);
                break;
            case R.id.btnTaxiTheoMail:
            case R.id.btnTaxiBroeckxMail:
            case R.id.btnTaxiAZMail:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("mailto:" + view.getTag().toString()));
                startActivity(Intent.createChooser(intent, "Select"));
                break;
            case R.id.viewGetThere:
                showHideContent(GET_THERE_VIEW_ID);
                break;
            case R.id.viewContact:
                showHideContent(CONTACT_VIEW_ID);
                break;
            case R.id.viewTaxis:
                showHideContent(TAXIS_VIEW_ID);
                break;
            case R.id.viewWifi:
                showHideContent(WIFI_VIEW_ID);
                break;
        }
    }

    private void showHideContent(int viewId) {
        View viewGetThereContent, viewTaxisDetail, viewWifiDetail;
        ImageView imageExpandGetThere, imageExpandContact, imageExpandTaxis, imageExpandWifi;

        TextView textContact = findViewById(R.id.contactUs);
        viewGetThereContent = findViewById(R.id.viewGetThereContent);
        viewTaxisDetail = findViewById(R.id.viewTaxisDetail);
        viewWifiDetail = findViewById(R.id.viewWifiDetail);

        imageExpandGetThere = findViewById(R.id.imageExpandGetThere);
        imageExpandContact = findViewById(R.id.imageExpandContact);
        imageExpandTaxis = findViewById(R.id.imageExpandTaxes);
        imageExpandWifi = findViewById(R.id.imageExpandWifi);

        switch (viewId) {
            case GET_THERE_VIEW_ID:
                if (viewGetThereContent.isShown()) {
                    viewGetThereContent.setVisibility(View.GONE);
                    imageExpandGetThere.setImageResource(R.drawable.ic_arrow_right_black_24dp);
                    slide_up(getContext(), viewGetThereContent);
                } else {
                    viewGetThereContent.setVisibility(View.VISIBLE);
                    imageExpandGetThere.setImageResource(R.drawable.ic_arrow_down_black_24dp);
                    slide_down(getContext(), viewGetThereContent);
                }
                break;
            case CONTACT_VIEW_ID:
                if (textContact.isShown()) {
                    textContact.setVisibility(View.GONE);
                    imageExpandContact.setImageResource(R.drawable.ic_arrow_right_black_24dp);
                    slide_up(getContext(), textContact);
                } else {
                    textContact.setVisibility(View.VISIBLE);
                    imageExpandContact.setImageResource(R.drawable.ic_arrow_down_black_24dp);
                    slide_down(getContext(), textContact);
                }
                break;
            case TAXIS_VIEW_ID:
                if (viewTaxisDetail.isShown()) {
                    viewTaxisDetail.setVisibility(View.GONE);
                    imageExpandTaxis.setImageResource(R.drawable.ic_arrow_right_black_24dp);
                    slide_up(getContext(), viewTaxisDetail);
                } else {
                    viewTaxisDetail.setVisibility(View.VISIBLE);
                    imageExpandTaxis.setImageResource(R.drawable.ic_arrow_down_black_24dp);
                    slide_down(getContext(), viewTaxisDetail);
                }
                break;
            case WIFI_VIEW_ID:
                if (viewWifiDetail.isShown()) {
                    viewWifiDetail.setVisibility(View.GONE);
                    imageExpandWifi.setImageResource(R.drawable.ic_arrow_right_black_24dp);
                    slide_up(getContext(), viewWifiDetail);
                } else {
                    viewWifiDetail.setVisibility(View.VISIBLE);
                    imageExpandWifi.setImageResource(R.drawable.ic_arrow_down_black_24dp);
                    slide_down(getContext(), viewWifiDetail);
                }
                break;
        }

    }

    public static void slide_down(Context context, View v) {
        Animation animSlideDown = AnimationUtils.loadAnimation(context, R.anim.anim_down);
        v.startAnimation(animSlideDown);
    }

    public static void slide_up(Context context, View v) {
        Animation animSlideUp = AnimationUtils.loadAnimation(context, R.anim.anim_up);
        v.startAnimation(animSlideUp);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_xp_info, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about_app:
                showAboutApp();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAboutApp() {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.screen_xp_info_about, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        builder.setPositiveButton(R.string.label_ok, null);
        builder.setNeutralButton(R.string.label_rate_us, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(myAppLinkToMarket);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getContext(), " unable to find market app", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.show();
    }
}
