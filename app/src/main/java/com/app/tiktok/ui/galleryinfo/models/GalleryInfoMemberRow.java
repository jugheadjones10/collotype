package com.app.tiktok.ui.galleryinfo.models;

import com.app.tiktok.model.Gallery;
import com.app.tiktok.model.User;
import com.app.tiktok.ui.galleryinfo.GalleryInfoRecyclerDataItem;

import java.util.List;

public class GalleryInfoMemberRow extends GalleryInfoRecyclerDataItem {
    public User user;
    public List<Gallery> galleries;

    public GalleryInfoMemberRow(User user, List<Gallery> galleries) {
        this.user = user;
        this.galleries = galleries;
    }
}