package com.odoo.addons.events.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.odoo.addons.events.models.EventEvent;
import com.odoo.core.support.addons.fragment.BaseFragment;

public class DayPagerAdapter extends FragmentStatePagerAdapter {
    public static final String TAG = DayPagerAdapter.class.getSimpleName();
    private Context mContext;
    private OnFragmentCallListener onFragmentCallListener;
    private Class<?> fragmentClass = null;
    public static int totalDays = -1;
    public static boolean hasTalks = false;

    public DayPagerAdapter(Context context, FragmentManager fm,
                           Class<? extends BaseFragment> fragmentClass, OnFragmentCallListener callback) {
        super(fm);
        mContext = context;
        onFragmentCallListener = callback;
        this.fragmentClass = fragmentClass;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (onFragmentCallListener != null) {
            String title = onFragmentCallListener.pageTitle(hasTalks, position);
            if (title != null) {
                return title;
            }
        }
        return super.getPageTitle(position);
    }

    @Override
    public Fragment getItem(int position) {
        if (onFragmentCallListener != null) {
            Bundle data = onFragmentCallListener.fragmentBundle(hasTalks, position);
            Fragment fragment = null;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
                if (data != null)
                    fragment.setArguments(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return fragment;
        }
        return null;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        super.restoreState(null, loader);
    }

    @Override
    public int getCount() {
        if (onFragmentCallListener != null) {
            return totalDays(mContext);
        }
        return 0;
    }

    public interface OnFragmentCallListener {
        Bundle fragmentBundle(boolean hasTalks, int position);

        String pageTitle(boolean hasTalks, int position);
    }

    public static int totalDays(Context context) {
        if (totalDays < 0) {
            EventEvent event = new EventEvent(context);
            totalDays = event.getTotalDays();
            hasTalks = event.hasTalksTrainingDay();
            if (hasTalks) {
                totalDays = totalDays + 1;
            }
        }
        return totalDays;
    }
}