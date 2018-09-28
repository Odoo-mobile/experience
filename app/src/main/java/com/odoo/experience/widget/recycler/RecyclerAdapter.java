package com.odoo.experience.widget.recycler;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.odoo.experience.core.db.ORecord;
import com.odoo.experience.ui.schedule.FragStarred;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RVHolder> {

    private Context context;
    private List<ORecord> recordItems;
    private OnItemViewBindListener mOnItemViewBindListener;
    private OnItemViewClickListener mOnItemViewClickListener;
    private int layoutResId = -1;

    public RecyclerAdapter(Context context, List<ORecord> recordItems) {
        this.context = context;
        this.recordItems = recordItems;
    }

    @NonNull
    @Override
    public RVHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(layoutResId > 0 ? layoutResId : android.R.layout.simple_list_item_1,
                        viewGroup, false);
        return new RVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RVHolder viewHolder, final int i) {
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemViewClickListener != null) {
                    mOnItemViewClickListener.onItemViewClick(viewHolder, i, recordItems.get(i));
                }
            }
        });
        if (mOnItemViewBindListener != null) {
            mOnItemViewBindListener.onItemViewBind(recordItems.get(i), i, viewHolder);
        }
    }

    @Override
    public int getItemCount() {
        return recordItems.size();
    }

    public void setLayoutResId(int resId, OnItemViewBindListener listener) {
        this.layoutResId = resId;
        this.mOnItemViewBindListener = listener;

    }

    public void setOnItemViewClickListener(OnItemViewClickListener listener) {
        mOnItemViewClickListener = listener;
    }

    public interface OnItemViewClickListener {
        void onItemViewClick(RVHolder holder, int position, ORecord record);
    }

    public interface OnItemViewBindListener {
        void onItemViewBind(ORecord record, int position, RVHolder holder);
    }
}
