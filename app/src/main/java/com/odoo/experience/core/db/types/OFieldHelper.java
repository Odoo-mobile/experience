package com.odoo.experience.core.db.types;

public abstract class OFieldHelper<T> {

    private String fieldName, fieldLabel;
    private int size = -1;
    private Object defaultValue;
    private boolean autoIncrement = false, primaryKey = false;

    public OFieldHelper(String label) {
        fieldLabel = label;
    }

    public abstract String fieldType();

    public T setFieldName(String name) {
        fieldName = name;
        return (T) this;
    }

    public T setSize(int size) {
        this.size = size;
        return (T) this;
    }

    public T setDefault(Object defValue) {
        defaultValue = defValue;
        return (T) this;
    }

    public T setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
        return (T) this;
    }

    public T setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
        return (T) this;
    }

    public String getFieldName() {
        return fieldName;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public int getSize() {
        return size;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}
