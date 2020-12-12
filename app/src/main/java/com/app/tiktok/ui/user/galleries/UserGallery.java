package com.app.tiktok.ui.user;

import com.app.tiktok.model.Gallery;
import com.app.tiktok.model.Post;
import com.app.tiktok.model.User;

import java.util.List;

class UserGallery extends DataItem {
    Gallery gallery;
    List<Post> posts;
    List<User> members;

    UserGallery(Gallery gallery, List<Post> posts, List<User> members){
        this.gallery = gallery;
        this.posts = posts;
        this.members = members;
        this.id = gallery.getId();
    }
}