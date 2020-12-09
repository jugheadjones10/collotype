package com.app.tiktok.ui.galleryinfo;

import com.app.tiktok.model.Product;

import java.util.List;

public class GalleryInfoProductsRow extends GalleryInfoRecyclerDataItem{
    List<Product> products;

    public GalleryInfoProductsRow(List<Product> products) {
        this.products = products;
    }
}
