package com.app.tiktok.ui.galleryinfo;

import com.app.tiktok.model.Event;
import com.app.tiktok.model.HydratedEvent;

import java.util.List;

public class GalleryInfoEventsRow extends GalleryInfoRecyclerDataItem{
    List<HydratedEvent> events;

    public GalleryInfoEventsRow(List<HydratedEvent> events) {
        this.events = events;
    }
}
