package com.odoo.experience.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.odoo.experience.core.helper.OdooFragment;

import java.util.ArrayList;
import java.util.List;


public class TabFragmentAdapter extends FragmentPagerAdapter {

    private List<OdooFragment> fragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    public TabFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void addFragment(OdooFragment fragment, String title) {
        fragments.add(fragment);
        titles.add(title);
    }
}
