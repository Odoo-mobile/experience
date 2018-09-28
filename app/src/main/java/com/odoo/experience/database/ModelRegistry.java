package com.odoo.experience.database;

import android.content.Context;

import com.odoo.experience.core.db.ModelRegistryHelper;
import com.odoo.experience.database.models.EventEvent;
import com.odoo.experience.database.models.EventSponsors;
import com.odoo.experience.database.models.EventTrackTags;
import com.odoo.experience.database.models.EventTrackTagsRel;
import com.odoo.experience.database.models.EventTracks;
import com.odoo.experience.database.models.ResPartner;

public class ModelRegistry extends ModelRegistryHelper {

    public ModelRegistry(Context context) {
        super(context);

        register(EventEvent.class);
        register(EventSponsors.class);
        register(EventTracks.class);
        register(EventTrackTagsRel.class);
        register(EventTrackTags.class);
        register(ResPartner.class);
    }
}
