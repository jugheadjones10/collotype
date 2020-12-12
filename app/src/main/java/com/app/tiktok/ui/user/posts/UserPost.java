package com.app.tiktok.ui.user.posts;

import com.app.tiktok.model.Gallery;
import com.app.tiktok.model.Post;
import com.app.tiktok.ui.user.DataItem;

public class UserPost extends DataItem {

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

    public UserPost(Gallery gallery, Post post){
        this.gallery = gallery;
        this.post = post;
        this.id = post.getId();
    }
}
