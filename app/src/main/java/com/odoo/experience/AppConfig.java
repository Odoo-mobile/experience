package com.odoo.experience;

public class AppConfig {

    /*
        Toggle when your have ready dump (replace in assets)
        TODO: Dump file name must be "odooxp_<event_id>.db
    */
    public static final boolean APPLY_DB_DUMP = false;
    public static final String KEY_DB_SETUP_DONE = "db_setup_status";
    public static String D_SESSION_ID = null;
    // Database
    public static final String DB_NAME = "OdooXP2018-2.db";
    public static final int DB_VERSION = 1;

    // FIXME: Replace with latest event
    public static final int EVENT_ID = 1206;
    public static final int EVENT_YEAR = 2018;
    public static final String[] EVENT_DATES = {"2018-10-03", "2018-10-04", "2018-10-05"};

    public static final String ODOO_URL = "https://www.odoo.com";

    /* Social Pages */
    public static final String TWITTER_HASH_TAG_URL = "https://twitter.com/hashtag/OdooExperience";

    /* NOTE: Need event read rights */
    public static final String USER_NAME = "<REPLACE_WITH_YOUR_USER>";
    public static final String USER_KEY = "<REPLACE_WITH_USER_PASSWORD>";
    public static final String USER_DB = "<REPLACE_WITH_DATABASE_NAME>";
}
