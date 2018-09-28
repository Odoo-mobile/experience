package com.odoo.experience.core.utils;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;

public class StringUtils {
    public static final String TAG = StringUtils.class.getSimpleName();

    public static String repeat(String string, int repeat) {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < repeat; i++)
            str.append(string);
        return str.toString();
    }

    public static String capitalizeString(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i] == '.'
                    || chars[i] == '\'') {
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    /**
     * Html to string.
     *
     * @param html the html
     * @return the string
     */
    public static String htmlToString(String html) {
        String htmlData = html.replaceAll("\\<.*?\\>", "").replaceAll("\n", "")
                .replaceAll("\t", " ");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
            return Html.fromHtml(htmlData, Html.FROM_HTML_OPTION_USE_CSS_COLORS).toString();
        else
            return Html.fromHtml(htmlData).toString();
    }

    /**
     * String to html.
     *
     * @param html the string
     * @return the spanned
     */
    public static String stringToHtml(String html) {
        return Html.fromHtml(html).toString().trim();
    }

}