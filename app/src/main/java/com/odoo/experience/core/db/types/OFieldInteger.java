package com.odoo.experience.core.db.types;

public class OFieldInteger extends OFieldHelper<OFieldInteger> {

    public OFieldInteger(String name) {
        super(name);
    }

    @Override
    public String fieldType() {
        return "INTEGER";
    }
}
