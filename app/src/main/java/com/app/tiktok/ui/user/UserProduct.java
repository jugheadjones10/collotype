package com.app.tiktok.ui.user;

public class UserProduct extends DataItem{
    public String productName;
    public Long price;
    public String url;
    public Long seller;

    public UserProduct(Long prodcutId, String productName, Long price, String url, Long seller) {
        this.id = prodcutId;
        this.productName = productName;
        this.price = price;
        this.url = url;
        this.seller = seller;
    }
}