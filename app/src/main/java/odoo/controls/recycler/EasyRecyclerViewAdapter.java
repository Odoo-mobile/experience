package odoo.controls.recycler;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.odoo.core.orm.ODataRow;
import com.odoo.core.utils.OCursorUtils;

public class EasyRecyclerViewAdapter extends
        EasyRecyclerView.Adapter<EasyRecyclerViewAdapter.EasyRecyclerViewHolder> {

    private Cursor cursor;
    private int mLayout = -1;
    private OnViewBindListener mOnViewBindListener;
    private OnItemViewClickListener mOnItemViewClickListener;

    public EasyRecyclerViewAdapter(int layout_id, Cursor data) {
        mLayout = layout_id;
        cursor = data;
    }

    @Override
    public EasyRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(mLayout, viewGroup, false);
        return new EasyRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EasyRecyclerViewHolder holder, int i) {
        cursor.moveToPosition(i);
        if (mOnViewBindListener != null) {
            mOnViewBindListener.onViewBind(i, holder.mView, OCursorUtils.toDatarow(cursor));
            holder.bind(i, cursor, mOnItemViewClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : -1;
    }

    public void changeCursor(Cursor data) {
        cursor = null;
        cursor = data;
        cursor.moveToFirst();
        notifyDataSetChanged();
    }

    public ODataRow getRow(int position) {
        Cursor cr = cursor;
        if (cr.moveToPosition(position)) {
            return OCursorUtils.toDatarow(cr);
        }
        return null;
    }


    public class EasyRecyclerViewHolder extends EasyRecyclerView.ViewHolder {
        protected View mView;
        protected OnItemViewClickListener listener;
        protected int position = -1;
        protected Cursor data;

        public EasyRecyclerViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void bind(int position, Cursor data, OnItemViewClickListener listener) {
            this.listener = listener;
            this.position = position;
            this.data = data;
            mView.setTag(this);
            mView.setOnClickListener(clickListener);
        }

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    data.moveToPosition(position);
                    listener.onItemViewClick(position, mView, OCursorUtils.toDatarow(data));
                }
            }
        };
    }

    public EasyRecyclerViewAdapter setOnViewBindListener(OnViewBindListener listener) {
        mOnViewBindListener = listener;
        return this;
    }

    public EasyRecyclerViewAdapter setOnItemViewClickListener(OnItemViewClickListener listener) {
        mOnItemViewClickListener = listener;
        return this;
    }

    public interface OnViewBindListener {
        void onViewBind(int position, View view, ODataRow data);
    }

    public interface OnItemViewClickListener {
        void onItemViewClick(int position, View view, ODataRow data);
    }
}