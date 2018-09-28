package com.odoo.experience.core.db;

import android.content.Context;

import com.odoo.experience.database.models.ModelSyncStatus;

import java.util.ArrayList;
import java.util.List;

public class ModelRegistryHelper {

    private Context context;
    private List<Class<? extends OModel>> modelClasses = new ArrayList<>();

    public ModelRegistryHelper(Context context) {
        this.context = context;
        register(ModelSyncStatus.class);
    }

    protected final void register(Class<? extends OModel> className){
        modelClasses.add(className);
    }

    public final List<Class<? extends OModel>> getModelClasses() {
        return modelClasses;
    }
}
