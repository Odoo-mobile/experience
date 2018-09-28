package odoo.controls.appintro.navigator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;

import com.odoo.R;
import com.odoo.core.utils.OResource;

public class PagerNavigatorAdapter {

    private Context mContext = null;
    private LinearLayout mParent;

    public PagerNavigatorAdapter(Context context) {
        super();
        mContext = context;
    }

    public void navigator(int totalCount, View container) {
        mParent = (LinearLayout) container;
        pageNavigationDots(totalCount);
    }

    private void pageNavigationDots(int totalPage) {
        while (totalPage > 0) {
            View v = new View(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    getHeightWidth(false), getHeightWidth(false));
            v.setLayoutParams(params);
            mParent.addView(v);
            totalPage--;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                focusOnPagerDot(0);
            }
        }, 500);
    }

    @SuppressLint("NewApi")
    public void focusOnPagerDot(int position) {
        for (int i = 0; i < mParent.getChildCount(); i++) {
            GradientDrawable shapeBg = (GradientDrawable)
                    ContextCompat.getDrawable(mContext, R.drawable.intro_slider_dot_bg);
            View child = mParent.getChildAt(i);
            LinearLayout.LayoutParams params;
            if (i == position) {
                params = new LinearLayout.LayoutParams(getHeightWidth(true),
                        getHeightWidth(true));
                shapeBg.setColor(OResource.color(mContext, R.color.android_pure_white));
            } else {
                params = new LinearLayout.LayoutParams(getHeightWidth(false),
                        getHeightWidth(false));
                shapeBg.setColor(OResource.color(mContext, R.color.android_pure_white));
            }
            params.setMargins(getLeftRightMargin(), getTopBottomMargin(),
                    getLeftRightMargin(), getTopBottomMargin());
            child.setLayoutParams(params);
            child.setBackground(shapeBg);
            child.invalidate();
        }
    }

    private int getHeightWidth(boolean focused) {
        if (focused)
            return (int) mContext.getResources().getDimension(
                    R.dimen.dot_focused_height_width);
        else
            return (int) mContext.getResources().getDimension(
                    R.dimen.dot_normal_height_width);
    }

    private int getTopBottomMargin() {
        return (int) mContext.getResources().getDimension(
                R.dimen.dot_top_bottom_margin);
    }

    private int getLeftRightMargin() {
        return (int) mContext.getResources().getDimension(
                R.dimen.dot_left_right_margin);
    }
}
