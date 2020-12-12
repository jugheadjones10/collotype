package com.app.tiktok.ui.galleryinfo.models;

import com.app.tiktok.model.Product;
import com.app.tiktok.ui.galleryinfo.GalleryInfoRecyclerDataItem;

import java.util.List;

public class GalleryInfoProductsRow extends GalleryInfoRecyclerDataItem {
    public List<Product> products;

    public GalleryInfoProductsRow(List<Product> products) {
        this.products = products;
    }
}
