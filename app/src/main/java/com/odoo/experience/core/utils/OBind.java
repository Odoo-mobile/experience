package com.odoo.experience.core.utils;

import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class OBind {

    public static void setText(View view, String content) {
        if (view != null && view instanceof TextView) {
            ((TextView) view).setText(content);
        }
    }

    public static void setText(View view, Spanned content) {
        if (view != null && view instanceof TextView) {
            ((TextView) view).setText(content);
        }
    }

    public static void setText(View view, int resId) {
        if (view != null && view instanceof TextView) {
            ((TextView) view).setText(view.getContext().getString(resId));
        }
    }

    public static void setImage(View view, int resId) {
        if (view != null && view instanceof ImageView) {
            ((ImageView) view).setImageResource(resId);
        }
    }
}
