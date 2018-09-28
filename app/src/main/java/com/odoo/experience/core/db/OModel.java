package com.odoo.experience.core.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.CallSuper;
import android.util.Log;

import com.odoo.experience.AppConfig;
import com.odoo.experience.core.api.odoo.client.helper.data.OdooRecord;
import com.odoo.experience.core.db.sql.ModelSQL;
import com.odoo.experience.core.db.types.OFieldDatetime;
import com.odoo.experience.core.db.types.OFieldHelper;
import com.odoo.experience.core.db.types.OFieldInteger;
import com.odoo.experience.core.utils.ODateUtils;
import com.odoo.experience.core.utils.OStorageUtils;
import com.odoo.experience.database.ModelRegistry;
import com.odoo.experience.database.models.ModelSyncStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class OModel extends SQLiteOpenHelper {

    private Context context;
    private String modelName;

    // Default Fields
    OFieldInteger _id = new OFieldInteger("_ID").setAutoIncrement(true).setPrimaryKey(true);
    OFieldInteger id = new OFieldInteger("id").setDefault(0);
    OFieldDatetime create_date = new OFieldDatetime("Create Date").setDefault("false");
    OFieldDatetime write_date = new OFieldDatetime("Write Date").setDefault("false");

    public OModel(Context context, String modelName) {
        super(context, AppConfig.DB_NAME, null, AppConfig.DB_VERSION);
        this.context = context;
        this.modelName = modelName;
    }

    public String getModelName() {
        return modelName;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        ModelRegistry registry = new ModelRegistry(context);
        for (Class<? extends OModel> cls : registry.getModelClasses()) {
            try {
                Constructor constructor = cls.getConstructor(Context.class);
                OModel model = (OModel) constructor.newInstance(context);
                ModelSQL sql = new ModelSQL(model);
                sqLiteDatabase.execSQL(sql.createStatement());
                Log.v("OModel", "Table Created : " + model.getTableName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public Context getContext() {
        return context;
    }

    public List<OFieldHelper> getFields() {
        List<OFieldHelper> fields = new ArrayList<>();
        List<Field> selfFields = Arrays.asList(getClass().getDeclaredFields());
        List<Field> parentFields = Arrays.asList(getClass().getSuperclass().getDeclaredFields());
        List<Field> allFields = new ArrayList<>();
        allFields.addAll(selfFields);
        allFields.addAll(parentFields);

        for (Field field : allFields) {
            field.setAccessible(true);
            try {
                if (field.getType().getSuperclass().getCanonicalName()
                        .equals(OFieldHelper.class.getCanonicalName())) {
                    OFieldHelper fieldObj = (OFieldHelper) field.get(this);
                    fieldObj.setFieldName(field.getName());
                    fields.add(fieldObj);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return fields;
    }

    public String getTableName() {
        return modelName.replaceAll("\\.", "_");
    }


    // CRUD
    public Cursor select() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(getTableName(), null, null, null, null, null, null);
    }

    public ORecord get(int serverId) {
        List<ORecord> records = select("id = ?", new String[]{serverId + ""}, null);
        if (!records.isEmpty()) {
            return records.get(0);
        }
        return null;
    }

    public List<ORecord> select(String[] columns, String where, String[] args, String orderBy) {
        List<ORecord> records = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cr = db.query(getTableName(), columns, where,
                args, null, null, orderBy);
        if (cr != null && cr.moveToFirst()) {
            do {
                records.add(ORecord.fromCursor(cr));
            } while (cr.moveToNext());
            cr.close();
        }
        db.close();
        return records;
    }

    public List<ORecord> select(String where, String[] args, String orderBy) {
        List<ORecord> records = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cr = db.query(getTableName(), null, where,
                args, null, null, orderBy);
        if (cr != null && cr.moveToFirst()) {
            do {
                records.add(ORecord.fromCursor(cr));
            } while (cr.moveToNext());
            cr.close();
        }
        db.close();
        return records;
    }

    public void createOrUpdate(ContentValues values, int serverId) {
        if (count("id = ? ", serverId + "") > 0) {
            update(values, "id = ?", serverId + "");
        } else {
            create(values);
        }
    }


    public int create(ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        Long newId = db.insert(getTableName(), null, values);
        db.close();
        return newId.intValue();
    }

    public int update(ContentValues values, String where, String... args) {
        SQLiteDatabase db = getWritableDatabase();
        int count = db.update(getTableName(), values, where, args);
        db.close();
        return count;
    }

    public int count(String where, String... args) {
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cr = db.query(getTableName(), new String[]{"count(*) as total"}, where, args, null, null, null);
        if (cr.moveToFirst()) {
            count = cr.getInt(0);
        }
        cr.close();
        db.close();
        return count;
    }

    public List<Integer> getServerIds() {
        List<Integer> ids = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cr = db.query(getTableName(), new String[]{"id"}, "id != ? ",
                new String[]{"0"}, null, null, null);
        if (cr != null && cr.moveToFirst()) {
            do {
                ids.add(cr.getInt(0));
            } while (cr.moveToNext());
            cr.close();
        }
        db.close();
        return ids;
    }

    public List<Integer> getM20Ids(String fieldName, String where, String... args) {
        List<Integer> ids = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cr = db.query(getTableName(), new String[]{fieldName}, where, args
                , null, null, null);
        if (cr != null && cr.moveToFirst()) {
            do {
                ids.add(cr.getInt(0));
            } while (cr.moveToNext());
            cr.close();
        }
        db.close();
        return ids;
    }

    public String getLastSyncDate() {
        ModelSyncStatus syncStatus = new ModelSyncStatus(context);
        return syncStatus.getSyncDate(getModelName());
    }

    public void setSyncDate() {
        ModelSyncStatus syncStatus = new ModelSyncStatus(context);
        syncStatus.setSyncDate(getModelName(), ODateUtils.getUTCDate());
    }

    @CallSuper
    public ContentValues recordToValues(OdooRecord record) {
        ContentValues values = new ContentValues();
        if (record.containsKey("id")) {
            values.put("id", record.getInt("id"));
        }
        if (record.containsKey("create_date")) {
            values.put("create_date", record.getString("create_date"));
        }
        if (record.containsKey("write_date")) {
            values.put("write_date", record.getString("write_date"));
        }

        return values;
    }

    public String databaseLocalPath() {
        Context app = getContext().getApplicationContext();
        return Environment.getDataDirectory().getPath() +
                "/data/" + app.getPackageName() + "/databases/" + getDatabaseName();
    }

    public void exportDB() {

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        FileChannel source;
        FileChannel destination;
        String currentDBPath = databaseLocalPath();
        String backupDBPath = OStorageUtils.getDirectoryPath("file")
                + "/" + getDatabaseName();
        File currentDB = new File(currentDBPath);
        File backupDB = new File(backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            String subject = "Database Export: " + getDatabaseName();
            Uri uri = Uri.fromFile(backupDB);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.setType("message/rfc822");
            getContext().startActivity(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public abstract String[] syncFields();
}
