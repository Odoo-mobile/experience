package com.odoo.experience.ui.info;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.odoo.experience.R;
import com.odoo.experience.core.helper.OdooFragment;
import com.odoo.experience.database.models.EventTracks;

public class FragmentXPInfo extends OdooFragment implements View.OnClickListener {

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

        /* call and email*/
        findViewById(R.id.btnHotelZeroBook).setOnClickListener(this);
        findViewById(R.id.btnHotelOneCall).setOnClickListener(this);
        findViewById(R.id.btnHotelTwoCall).setOnClickListener(this);
        findViewById(R.id.btnHotelThreeCall).setOnClickListener(this);

        findViewById(R.id.btnHotelOneEmail).setOnClickListener(this);
        findViewById(R.id.btnHotelTwoEmail).setOnClickListener(this);
        findViewById(R.id.btnHotelThreeEmail).setOnClickListener(this);
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
            case R.id.btnHotelZeroBook:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://reservations.cubilis.eu/martins-louvain-la-neuve/Rooms/Select?Arrival=2018-9-30&Departure=2018-10-5&Room=&Rate=&Package=&DiscountCode=Odoo2018"));
                startActivity(intent);
                break;
            case R.id.btnHotelOneCall:
            case R.id.btnHotelTwoCall:
            case R.id.btnHotelThreeCall:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("tel:" + view.getTag().toString()));
                startActivity(Intent.createChooser(intent, "Select"));
                break;
            case R.id.btnHotelOneEmail:
            case R.id.btnHotelTwoEmail:
            case R.id.btnHotelThreeEmail:
            case R.id.contactUs:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("mailto:" + view.getTag().toString()));
                startActivity(Intent.createChooser(intent, "Select"));
                break;
        }
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
