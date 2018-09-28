/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 * <p/>
 * Created on 14/7/16 4:04 PM
 */
package com.odoo.addons.sponsor;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.odoo.R;
import com.odoo.addons.sponsor.models.EventSponsor;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.support.list.OCursorListAdapter;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.OCursorUtils;
import com.odoo.core.utils.OResource;

import java.util.List;

public class Sponsors extends BaseFragment implements AdapterView.OnItemClickListener,
        OCursorListAdapter.OnViewBindListener, LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = Sponsors.class.getSimpleName();

    private OCursorListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.sponsor_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GridView mGrid = (GridView) findViewById(R.id.gridView);
        mGrid.setNumColumns(_bool(R.bool.isLandscape) ? 3 : 2);
        mGrid.setOnItemClickListener(this);

        mAdapter = new OCursorListAdapter(getActivity(), null, R.layout.sponsors);
        mAdapter.setOnViewBindListener(this);
        mGrid.setAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        return null;
    }

    @Override
    public Class<EventSponsor> database() {
        return EventSponsor.class;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        ODataRow row = OCursorUtils.toDatarow((Cursor) mAdapter.getItem(position));
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(OResource.color(getContext(), R.color.colorPrimary));
        builder.build().launchUrl(getActivity(), Uri.parse(row.getString("url")));
    }

    @Override
    public void onViewBind(View view, Cursor cursor, ODataRow row) {
        View sponsor_detail_layout = view.findViewById(R.id.sponsor_detail_layout);
        OControls.setText(view, R.id.sponsorName, row.getString("sponsor_name"));
        OControls.setText(view, R.id.sponsorType, row.getString("sponsor_type"));
        if (!row.getString("image_medium").equals("false"))
            OControls.setImage(view, R.id.sponsorLogo, BitmapUtils.getBitmapImage(getActivity(),
                    row.getString("image_medium")));

        if (row.getString("sponsor_type").equals("Platinum"))
            sponsor_detail_layout.setBackgroundColor(OResource.color(getContext(),
                    R.color.primary_platinum));
        else if (row.getString("sponsor_type").equals("VIP"))
            sponsor_detail_layout.setBackgroundColor(OResource.color(getContext(),
                    R.color.primary_vip));
        else if (row.getString("sponsor_type").equals("Gold"))
            sponsor_detail_layout.setBackgroundColor(OResource.color(getContext(),
                    R.color.primary_gold));
        else
            sponsor_detail_layout.setBackgroundColor(OResource.color(getContext(),
                    R.color.primary_basic));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), db().uri(), null, null, null, "sequence, id desc");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }
}
