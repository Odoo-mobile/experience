/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 * <p/>
 * Created on 18/12/14 11:28 AM
 */
package com.odoo.datas;

import com.odoo.core.support.OdooUser;

import odoo.helper.OdooVersion;

public class OConstants {
    public static final String URL_ODOO = "https://www.odoo.com";
    public static final String URL_ODOO_ACCOUNTS = "https://www.odoo.com";
    public static final String URL_ODOO_MOBILE_GIT_HUB = "https://github.com/Odoo-mobile";
    public static final String URL_ODOO_APPS_ON_PLAY_STORE = "https://play.google.com/store/apps/developer?id=Odoo+SA";

    public static final int RPC_REQUEST_TIME_OUT = 30000; // 30 Seconds
    public static final int RPC_REQUEST_RETRIES = 1; // Retries when timeout

    /* Social Pages */
    public static final String PLUS_HASH_TAG_URL = "https://plus.google.com/explore/OdooExperience";
    public static final String TWITTER_HASH_TAG_URL = "https://twitter.com/hashtag/OdooExperience";
    public static final String GOOGLE_PLUS_URL = "https://plus.google.com/+Odooapps";
    public static final String TWITTER_PAGE = "http://twitter.com/odoo";

    /**
     * Database version. Required to change in increment order
     * when you change your database model in case of released apk.
     */
    public static final String DATABASE_NAME = "odoo_experience_event_2017_692.db";
    public static final int DATABASE_VERSION = 1;

    // FIXME: Replace with your default user credentials by which all events are updated.
    private static final String USER_NAME = "USERNAME";
    private static final String USER_KEY = "USER_PASSWORD";
    private static final String USER_DB = "SERVER_DATABASE";

    public static final Boolean APPLY_DB_DUMP = true;

    /* Event details */
    public static final Integer EVENT_ID = 692; // FIXME: Set event id here

    public static OdooUser getUser() {
        OdooUser user = new OdooUser();
        user.setHost(URL_ODOO_ACCOUNTS);
        user.setUsername(USER_NAME);
        user.setPassword(USER_KEY);
        user.setDatabase(USER_DB);
        user.setAvatar("false");
        user.setTimezone("Asia/Kolkata"); // FIXME: changed your timezone based on user.
        OdooVersion version = new OdooVersion();
        version.setVersionNumber(10);
        user.setOdooVersion(version);
        return user;
    }

}
