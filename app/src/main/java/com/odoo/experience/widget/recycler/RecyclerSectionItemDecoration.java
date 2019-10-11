package com.odoo.experience.widget.recycler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.odoo.experience.core.db.ORecord;


public class RecyclerSectionItemDecoration extends RecyclerView.ItemDecoration {

    private Context context;
    private final boolean sticky;
    private final SectionCallback sectionCallback;

    private View headerView;
    private TextView header;
    private int headerViewResId = -1;
    private OnHeaderSectionViewBindListener mOnHeaderSectionViewBindListener;


    public RecyclerSectionItemDecoration(Context context, boolean sticky, @NonNull SectionCallback sectionCallback) {
        this.context = context;
        this.sticky = sticky;
        this.sectionCallback = sectionCallback;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int pos = parent.getChildAdapterPosition(view);
//        if (sectionCallback.isSection(pos) && headerView != null) {
//            outRect.top = headerView.getHeight(); // context.getResources().getDimensionPixelSize(R.dimen.section_header_height);
//        }
    }

    public void setHeaderViewResId(int resId, OnHeaderSectionViewBindListener listener) {
        headerViewResId = resId;
        mOnHeaderSectionViewBindListener = listener;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        if (headerView == null) {
            headerView = inflateHeaderView(parent);
            if (headerViewResId <= 0) {
                header = headerView.findViewById(android.R.id.title);
            }
            fixLayoutSize(headerView, parent);
        }

        CharSequence previousHeader = "";
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            final int position = parent.getChildAdapterPosition(child);
            boolean isSectionView = sectionCallback.isSection(position);
            String title = "";
            if (isSectionView) {
                ORecord record = sectionCallback.getSectionHeader(position);
                title = record.contains("name") ? record.getString("name") :
                        record.contains("title") ? record.getString("title") : "Unknown";
                if (headerViewResId > 0 && mOnHeaderSectionViewBindListener != null) {
                    title = mOnHeaderSectionViewBindListener.onSectionHeaderBind(headerView, record);
                } else {
                    header.setText(title);
                }
            }
            if (!previousHeader.equals(title) || isSectionView) {
                drawHeader(c, child, headerView);
                previousHeader = title;
            }
        }
    }

    private void drawHeader(Canvas c, View child, View headerView) {
        c.save();
        if (sticky) {
            c.translate(0, Math.max(0, child.getTop() - headerView.getHeight()));
        } else {
            c.translate(0, child.getTop() - headerView.getHeight());
        }
        headerView.draw(c);
        c.restore();
    }

    private View inflateHeaderView(RecyclerView parent) {
        return LayoutInflater.from(parent.getContext())
                .inflate(headerViewResId > 0 ? headerViewResId
                        : android.R.layout.simple_list_item_1, parent, false);
    }

    private void fixLayoutSize(View view, ViewGroup parent) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(),
                View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(),
                View.MeasureSpec.UNSPECIFIED);
        int childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                parent.getPaddingLeft() + parent.getPaddingRight(),
                view.getLayoutParams().width);
        int childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                parent.getPaddingTop() + parent.getPaddingBottom(),
                view.getLayoutParams().height);
        view.measure(childWidth, childHeight);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }


    public interface SectionCallback {

        boolean isSection(int position);

        ORecord getSectionHeader(int position);
    }

    public interface OnHeaderSectionViewBindListener {
        String onSectionHeaderBind(View view, ORecord record);
    }
}
