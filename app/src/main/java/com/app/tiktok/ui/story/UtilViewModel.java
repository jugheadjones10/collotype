package com.app.tiktok.ui.story;

import android.util.Log;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.ViewModel;

import com.app.tiktok.model.Gallery;
import com.app.tiktok.model.Post;
import com.app.tiktok.model.User;
import com.app.tiktok.repository.DataRepository;

import java.util.stream.Collectors;

public class UtilViewModel extends ViewModel {

    private DataRepository dataRepository;

    @ViewModelInject
    public UtilViewModel(DataRepository dataRepository){
        this.dataRepository = dataRepository;
    }

    public Post getPost(long postId){
        return dataRepository
            .getPostsData()
            .stream()
            .filter(post -> post.getId() == postId)
            .collect(Collectors.toList())
            .get(0);
    }

    public User getUser(long userId){
        return dataRepository
                .getUsersData()
                .stream()
                .filter(user -> user.getId() == userId)
                .collect(Collectors.toList())
                .get(0);
    }

    public Post getRandomUserPost(long galleryId){
        Gallery gallery =  dataRepository
                .getGalleriesData()
                .stream()
                .filter(g -> g.getId() == galleryId)
                .collect(Collectors.toList())
                .get(0);

        Log.d("kendrick", "gallery Id" + galleryId + "");
        long postId = gallery.getPosts().get(0);
        Log.d("kendrick", "postId Id" + postId + "");

        Post post = dataRepository
                .getPostsData()
                .stream()
                .filter(p -> p.getId() == postId)
                .collect(Collectors.toList())
                .get(0);

        return post;
    }


}
