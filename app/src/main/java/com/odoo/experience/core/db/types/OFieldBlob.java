package com.odoo.experience.core.db.types;

public class OFieldBlob extends OFieldHelper<OFieldBlob> {

    public OFieldBlob(String label) {
        super(label);
    }

    @Override
    public String fieldType() {
        return "BLOB";
    }
}
