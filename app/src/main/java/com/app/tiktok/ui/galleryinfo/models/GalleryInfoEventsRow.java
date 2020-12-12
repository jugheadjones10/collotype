package com.app.tiktok.ui.galleryinfo.models;

import com.app.tiktok.model.HydratedEvent;
import com.app.tiktok.ui.galleryinfo.GalleryInfoRecyclerDataItem;

import java.util.List;

public class GalleryInfoEventsRow extends GalleryInfoRecyclerDataItem {
    public List<HydratedEvent> events;

    public GalleryInfoEventsRow(List<HydratedEvent> events) {
        this.events = events;
    }
}
