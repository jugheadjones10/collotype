package com.app.tiktok.ui.galleryinfo;

import com.app.tiktok.model.Gallery;
import com.app.tiktok.model.User;

import java.util.List;

public class GalleryInfoMemberRow extends GalleryInfoRecyclerDataItem{
    User user;
    List<Gallery> galleries;

    public GalleryInfoMemberRow(User user, List<Gallery> galleries) {
        this.user = user;
        this.galleries = galleries;
    }
}