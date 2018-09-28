package com.odoo.experience.core.api.odoo.client;

import android.util.Log;

import java.util.List;

import com.odoo.experience.core.api.odoo.client.helper.data.OdooResult;

public class OdooVersion {

    public String server_version;
    public Boolean is_enterprise = false, isSaas11 = false;

    // Version Information
    public Float version_mejor, version_micro, version_serial;
    public String version_minor, version_release_level;

    public static OdooVersion parse(OdooResult result) {
        OdooVersion version = new OdooVersion();
        version.server_version = result.getString("server_version");

        List<Object> version_info = result.getArray("server_version_info");

        // Enterprise version only supported from 9.0
        if (version_info.size() > 5) {
            version.is_enterprise = version_info.get(5).toString().equals("e");
        }
        if (version_info.get(0).toString().equals("saas~11")) {
            version.version_mejor = 11F;
            version.isSaas11 = true;
        } else {
            version.version_mejor = Double.valueOf(version_info.get(0).toString()).floatValue();
        }
        version.version_minor = version_info.get(1).toString();
        version.version_micro = Double.valueOf(version_info.get(2).toString()).floatValue();
        version.version_release_level = version_info.get(3).toString();
        version.version_serial = Double.valueOf(version_info.get(4).toString()).floatValue();

        return version;
    }

    @Override
    public String toString() {
        return "OdooVersion{" +
                "server_version='" + server_version + '\'' +
                ", is_enterprise=" + is_enterprise +
                ", server_version= [" + version_mejor + ", " + version_minor + ", " + version_micro + "," +
                " " + version_release_level + ", " + version_serial + "]" +
                '}';
    }
}
