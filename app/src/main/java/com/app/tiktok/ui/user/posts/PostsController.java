package com.app.tiktok.ui.user.posts;

import android.content.Context;

import com.airbnb.epoxy.EpoxyController;
import com.app.tiktok.ItemRectangularPostBindingModel_;
import com.app.tiktok.ui.recommended.GalleryPost;

import java.util.List;

class PostsController extends EpoxyController {

    List<GalleryPost> posts;
    Context context;

    public PostsController(Context context){
        this.context = context;
    }

    public void setControllerData(List<GalleryPost> posts){
        this.posts = posts;
        requestModelBuild();
    }

    @Override
    protected void buildModels() {

        //================Before Product Post models=================//
        for(GalleryPost galleryPost : posts){
            new ItemRectangularPostBindingModel_()
                    .id(galleryPost.getPost().getId())
                    .context(context)
                    .post(galleryPost.getPost())
                    .gallery(galleryPost.getGallery())
                    .addTo(this);
        }

    }
}
