package com.odoo.experience.core.db.types;

public class OFieldDatetime extends OFieldHelper<OFieldDatetime> {

    public OFieldDatetime(String name) {
        super(name);
    }

    @Override
    public String fieldType() {
        return "DATETIME";
    }
}
