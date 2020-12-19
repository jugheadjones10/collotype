package com.app.tiktok.ui.user.events;

import android.content.Context;

import com.airbnb.epoxy.EpoxyController;
import com.app.tiktok.ItemUserEventBindingModel_;
import com.app.tiktok.model.HydratedEvent;

import java.util.List;

class EventsController extends EpoxyController {

    List<HydratedEvent> events;
    Context context;

    public EventsController(Context context){
        this.context = context;
    }

    public void setControllerData(List<HydratedEvent> events){
        this.events = events;
        requestModelBuild();
    }

    @Override
    protected void buildModels() {

        //================Before Product Post models=================//
        for(HydratedEvent hydratedEvent : events){
            new ItemUserEventBindingModel_()
                    .id(hydratedEvent.getId())
                    .hydratedEvent(hydratedEvent)
                    .context(context)
                    .addTo(this);
        }

    }
}