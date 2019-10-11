package com.odoo.experience.widget.recycler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;

import com.odoo.experience.R;
import com.odoo.experience.core.db.ORecord;
import com.odoo.experience.core.utils.ODateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TimeHeaderDecoration extends RecyclerView.ItemDecoration {
    private Context context;
    private List<ORecord> records;
    protected TextPaint paint;
    protected int width = 170, paddingTop = 45;
    protected HashMap<Integer, StaticLayout> timeSlots = new HashMap<>();

    public TimeHeaderDecoration(Context context, List<ORecord> records) {
        this.context = context;
        this.records = records;
        paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);
        createTimeSlots(records);
        width = Float.valueOf(context.getResources().getDimension(R.dimen.sticky_header_width)).intValue();
        paddingTop = Float.valueOf(context.getResources().getDimension(R.dimen.sticky_header_top_padding)).intValue();
    }


    public void setTextColor(int color) {
        paint.setColor(color);
    }

    public void setTextSize(int textSize) {
        paint.setTextSize(textSize);
    }

    public void createTimeSlots(List<ORecord> records) {
        List<String> timeSlotDates = new ArrayList<>();
        for (ORecord record : records) {
            String date = record.getString("date");
            String dateFormat = ODateUtils.parseDate(date, ODateUtils.DEFAULT_FORMAT, ODateUtils.DEFAULT_DATE_FORMAT);
            if (timeSlotDates.indexOf(dateFormat) < 0) {
                timeSlotDates.add(dateFormat);
                timeSlots.put(records.indexOf(record), createHeader(date));
            }
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        if (timeSlots.isEmpty() || parent.getChildCount() <= 0) return;

        int earliestFoundHeaderPos = -1;
        int prevHeaderTop = Integer.MAX_VALUE;

        for (int i = parent.getChildCount() - 1; i >= 0; i--) {
            View view = parent.getChildAt(i);
            if (view == null) {
                continue;
            }
            int viewTop = view.getTop() + (int) view.getTranslationY();
            if (view.getBottom() > 0 && viewTop < parent.getHeight()) {
                int position = parent.getChildAdapterPosition(view);
                if (timeSlots.containsKey(position)) {
                    c.save();
                    StaticLayout layout = timeSlots.get(position);
                    layout.getPaint().setAlpha((int) (view.getAlpha() * 255));
                    int top = viewTop + paddingTop;
                    if (top < paddingTop) {
                        top = paddingTop;
                    }
                    int diff = prevHeaderTop - layout.getHeight();
                    if (top > diff) {
                        top = diff;
                    }
                    c.translate(0, top);
                    layout.draw(c);
                    earliestFoundHeaderPos = position;
                    prevHeaderTop = viewTop;
                    c.restore();
                }
            }
        }
        if (earliestFoundHeaderPos < 0) {
            earliestFoundHeaderPos = parent.getChildAdapterPosition(parent.getChildAt(0)) + 1;
        }
        List<Integer> keys = new ArrayList<>(timeSlots.keySet());
        Collections.sort(keys);
        Collections.reverse(keys);
        for (int headerPos : keys) {
            if (headerPos < earliestFoundHeaderPos) {
                c.save();
                StaticLayout layout = timeSlots.get(headerPos);
                int top = prevHeaderTop - layout.getHeight();
                if (top > paddingTop) {
                    top = paddingTop;
                }
                c.translate(0, top);
                layout.draw(c);
                c.restore();
                break;
            }
        }
    }

    private String getFormattedDate(int position) {
        String date = records.get(position).getString("date");
        return ODateUtils.parseDate(date, ODateUtils.DEFAULT_FORMAT, ODateUtils.DEFAULT_DATE_FORMAT);
    }

    public StaticLayout createHeader(String dateTime) {
        String date = ODateUtils.parseDate(dateTime, ODateUtils.DEFAULT_FORMAT, "d");
        date += System.lineSeparator();
        date += ODateUtils.parseDate(dateTime, ODateUtils.DEFAULT_FORMAT, "EEE");
        return new StaticLayout(date, paint, width, Layout.Alignment.ALIGN_CENTER, 1f, 0f, false);
    }

}
