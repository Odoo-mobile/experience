package com.odoo.experience.core.db.types;

public class OFieldChar extends OFieldHelper<OFieldChar> {

    public OFieldChar(String name) {
        super(name);
        setSize(100);
    }

    @Override
    public String fieldType() {
        return "VARCHAR";
    }
}
