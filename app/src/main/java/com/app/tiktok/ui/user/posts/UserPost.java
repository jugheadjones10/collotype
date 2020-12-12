package com.app.tiktok.ui.user;

import com.app.tiktok.model.Gallery;
import com.app.tiktok.model.Post;

class UserPost extends DataItem {

    private Gallery gallery;
    private Post post;

    public Gallery getGallery() {
        return gallery;
    }

    public void setGallery(Gallery gallery) {
        this.gallery = gallery;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    UserPost(Gallery gallery, Post post){
        this.gallery = gallery;
        this.post = post;
        this.id = post.getId();
    }
}
