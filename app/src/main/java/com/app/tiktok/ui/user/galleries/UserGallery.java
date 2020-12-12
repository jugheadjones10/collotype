package com.app.tiktok.ui.user.galleries;

import com.app.tiktok.model.Gallery;
import com.app.tiktok.model.Post;
import com.app.tiktok.model.User;
import com.app.tiktok.ui.user.DataItem;

import java.util.List;

public class UserGallery extends DataItem {
    public Gallery gallery;
    public List<Post> posts;
    public List<User> members;

    public UserGallery(Gallery gallery, List<Post> posts, List<User> members){
        this.gallery = gallery;
        this.posts = posts;
        this.members = members;
        this.id = gallery.getId();
    }
}