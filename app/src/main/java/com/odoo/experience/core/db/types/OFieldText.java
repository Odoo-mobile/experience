package com.odoo.experience.core.db.types;

public class OFieldText extends OFieldHelper<OFieldText> {

    public OFieldText(String label) {
        super(label);
    }

    @Override
    public String fieldType() {
        return "TEXT";
    }
}
