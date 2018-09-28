package com.odoo.experience.widget.recycler;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class RVHolder extends RecyclerView.ViewHolder {

    private View view;

    public RVHolder(@NonNull View itemView) {
        super(itemView);
        view = itemView;
    }

    public void setTitle(String title) {
        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(title);
    }
}
