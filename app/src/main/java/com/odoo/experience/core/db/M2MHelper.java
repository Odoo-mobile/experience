package com.odoo.experience.core.db;

import java.util.List;

public interface M2MHelper {
    void addRel(int baseId, int relId);

    List<ORecord> getRelRecords(int baseId);
}
