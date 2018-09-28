package com.odoo.experience.core.utils;

import android.os.Environment;

import java.io.File;

public class OStorageUtils {
    public static final String TAG = OStorageUtils.class.getSimpleName();

    public static String getDirectoryPath(String file_type) {
        File externalStorage = Environment.getExternalStorageDirectory();
        String path = externalStorage.getAbsolutePath() + "/Odoo";
        File baseDir = new File(path);
        if (!baseDir.isDirectory()) {
            baseDir.mkdir();
        }
        if (file_type == null) {
            file_type = "file";
        }
        if (file_type.contains("image")) {
            path += "/Images";
        } else if (file_type.contains("audio")) {
            path += "/Audio";
        } else {
            path += "/Files";
        }
        File fileDir = new File(path);
        if (!fileDir.isDirectory()) {
            fileDir.mkdir();
        }
        return path;
    }
}