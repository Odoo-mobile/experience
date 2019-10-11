package com.odoo.experience.core.helper;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.odoo.experience.R;

public abstract class OdooFragment extends Fragment {

    private View contentView;

    public abstract int getViewResourceId();

    public abstract void onViewReady(View view);

    protected void resetTabLayout() {
        TabLayout tabView = getActivity().findViewById(R.id.tabs);
        tabView.setVisibility(View.GONE);
        tabView.removeAllTabs();
    }

    protected TabLayout getTabLayout() {
        TabLayout tabView = getActivity().findViewById(R.id.tabs);
        tabView.setVisibility(View.VISIBLE);
        tabView.removeAllTabs();
        return tabView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getViewResourceId() > 0) {
            return inflater.inflate(getViewResourceId(), container, false);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected View getContentView() {
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contentView = view;
        onViewReady(contentView);
    }

    protected <T extends View> T findViewById(int resId) {
        return contentView.findViewById(resId);
    }

}