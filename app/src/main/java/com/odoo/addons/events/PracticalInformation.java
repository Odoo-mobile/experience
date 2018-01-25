package com.odoo.addons.events;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.odoo.R;
import com.odoo.core.support.OdooCompatActivity;

public class PracticalInformation extends OdooCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_info);
        findViewById(R.id.closeView).setOnClickListener(this);
        findViewById(R.id.btnViewAccessMap).setOnClickListener(this);
        findViewById(R.id.btnAccessMap).setOnClickListener(this);
        findViewById(R.id.btnParkingInformation).setOnClickListener(this);
        findViewById(R.id.btnBusWebsite).setOnClickListener(this);
        findViewById(R.id.btnTrainSchedules).setOnClickListener(this);

        /* call and email*/
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
            case R.id.closeView:
                finish();
                break;
            case R.id.btnViewAccessMap:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.odoo.com/web/content/2850535?unique=1ed10a9cfa6cc5fcbc9605887ec30c4f3f63bdb8&download=true"));
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
}
