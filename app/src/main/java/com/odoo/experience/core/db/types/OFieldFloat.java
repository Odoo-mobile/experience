package com.odoo.experience.core.db.types;

public class OFieldFloat extends OFieldHelper<OFieldFloat> {
    public OFieldFloat(String label) {
        super(label);
    }

    @Override
    public String fieldType() {
        return "FLOAT";
    }
}
