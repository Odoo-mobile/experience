package com.odoo.experience.core.db.sql;

import com.odoo.experience.core.db.OModel;
import com.odoo.experience.core.db.types.OFieldHelper;

public class ModelSQL {

    private OModel model;

    public ModelSQL(OModel model) {
        this.model = model;
    }

    public String createStatement() {
        StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE IF NOT EXISTS ");
        sql.append(model.getTableName());
        sql.append(" (");
        for (OFieldHelper field : model.getFields()) {
            sql.append(field.getFieldName()).append(" ");
            sql.append(field.fieldType()).append(" ");
            if (field.getSize() > 0) {
                sql.append("(").append(field.getSize()).append(")");
            }
            if (field.isAutoIncrement()) {
                sql.append(" PRIMARY KEY AUTOINCREMENT ");
            }
            if (field.getDefaultValue() != null) {
                sql.append(" DEFAULT ");
                if (field.getDefaultValue() instanceof String) {
                    sql.append("'").append(field.getDefaultValue()).append("'");
                } else {
                    sql.append(field.getDefaultValue());
                }
            }
            sql.append(", ");
        }
        sql.deleteCharAt(sql.lastIndexOf(","));
        sql.append(")");
        return sql.toString();
    }
}
