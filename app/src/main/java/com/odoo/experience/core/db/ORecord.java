package com.odoo.experience.core.db;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ORecord {
    HashMap<String, Object> _data = new HashMap<>();

    public static ORecord fromCursor(Cursor cr) {
        ORecord record = new ORecord();
        for (String col : cr.getColumnNames()) {
            record.put(col, ORecord.cursorValue(col, cr));
        }
        return record;
    }

    public static Object cursorValue(String column, Cursor cr) {
        Object value = false;
        int index = cr.getColumnIndex(column);
        switch (cr.getType(index)) {
            case Cursor.FIELD_TYPE_NULL:
                value = false;
                break;
            case Cursor.FIELD_TYPE_STRING:
                value = cr.getString(index);
                break;
            case Cursor.FIELD_TYPE_INTEGER:
                value = cr.getInt(index);
                break;
            case Cursor.FIELD_TYPE_FLOAT:
                value = cr.getFloat(index);
                break;
            case Cursor.FIELD_TYPE_BLOB:
                value = cr.getBlob(index);
                break;
        }
        return value;
    }

    public void put(String key, Object value) {
        _data.put(key, value);
    }

    public Object get(String key) {
        return _data.get(key);
    }

    public Integer getInt(String key) {
        if (_data.get(key).toString().equals("false"))
            return 0;
        else
            return Integer.parseInt(_data.get(key).toString());
    }

    public Float getFloat(String key) {
        return Float.parseFloat(_data.get(key).toString());
    }

    public String getString(String key) {
        if (_data.containsKey(key) && _data.get(key) != null)
            return _data.get(key).toString();
        else
            return "false";
    }

    public Boolean getBoolean(String key) {
        return Boolean.parseBoolean(_data.get(key).toString());
    }

    public List<Object> values() {
        List<Object> values = new ArrayList<>();
        values.addAll(_data.values());
        return values;
    }

    public List<String> keys() {
        List<String> list = new ArrayList<>();
        list.addAll(_data.keySet());
        return list;
    }

    public boolean contains(String key) {
        return _data.containsKey(key);
    }

    public int size() {
        return _data.size();
    }

    @Override
    public String toString() {
        return _data.toString();
    }

    public void addAll(HashMap<String, Object> data) {
        _data.putAll(data);
    }


    public HashMap<String, Object> getAll() {
        return _data;
    }
}
