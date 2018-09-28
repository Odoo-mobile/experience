package odoo.controls.appintro;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.ViewGroup;

import java.util.List;

import odoo.controls.appintro.navigator.PagerNavigatorAdapter;

public class SliderHelper extends ViewPager {

    private Context mContext;
    private SliderPagerAdapter mPagerAdapter = null;
    private PagerNavigatorAdapter mPagerNavigatorAdapter;

    public SliderHelper(Context context) {
        super(context);
        mContext = context;
        _init(context);
    }

    public SliderHelper(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init(context);
    }

    private void _init(Context context) {
        mContext = context;
        mPagerNavigatorAdapter = new PagerNavigatorAdapter(mContext);
    }

    public void init(FragmentManager fragmentManager, List<SliderItem> items) {
        mPagerAdapter = new SliderPagerAdapter(mContext, fragmentManager);
        mPagerAdapter.initPager(mContext, items);
        setAdapter(mPagerAdapter);
        addOnPageChangeListener(mPageChangeListener);
    }

    OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            mPagerNavigatorAdapter.focusOnPagerDot(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    public void initNavigator(ViewGroup parent) {
        mPagerNavigatorAdapter.navigator(mPagerAdapter.getCount(), parent);
    }
}
