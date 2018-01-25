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
 * Created on 31/12/14 12:59 PM
 */
package com.odoo.base.addons;

import android.content.Context;

import com.odoo.addons.events.models.ExploreTracks;
import com.odoo.addons.events.models.UserEventSchedule;
import com.odoo.addons.sponsor.models.EventSponsor;
import com.odoo.base.addons.ir.IrModel;
import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.orm.OModel;
import com.odoo.news.models.OdooNews;

import java.util.ArrayList;
import java.util.List;

public class BaseModels {
    public static final String TAG = BaseModels.class.getSimpleName();

    public static List<OModel> baseModels(Context context) {
        List<OModel> models = new ArrayList<>();
        models.add(new OdooNews(context));
        models.add(new IrModel(context));
        models.add(new ResPartner(context));
        models.add(new UserEventSchedule(context));
        models.add(new EventSponsor(context));
        models.add(new ExploreTracks(context));
        return models;
    }
}
