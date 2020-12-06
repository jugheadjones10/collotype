package com.app.tiktok.ui.user;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.tiktok.model.Gallery;
import com.app.tiktok.model.Post;
import com.app.tiktok.model.StoriesDataModel;
import com.app.tiktok.model.User;
import com.app.tiktok.repository.DataRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class UserViewModel extends ViewModel {

    private DataRepository dataRepository;
    private MutableLiveData<List<UserPost>> userPosts;
    private MutableLiveData<List<UserGallery>> userGalleriesWithPosts;

    @ViewModelInject
    public UserViewModel(DataRepository dataRepository){
        this.dataRepository = dataRepository;
    }

    public LiveData<List<UserPost>> getUserPosts(long userId){
        if (userPosts == null) {
            List<Gallery> userGalleries =  dataRepository
                    .getGalleriesData()
                    .stream()
                    .filter(gallery -> gallery.getMembers().contains(userId))
                    .collect(Collectors.toList());

            List<UserPost> filteredUserPosts = new ArrayList<>();
            for(Gallery gallery: userGalleries){
                List<UserPost> galleryPosts =  dataRepository
                        .getPostsData()
                        .stream()
                        .filter(post -> post.getGallery() == gallery.getId())
                        .map(post -> new UserPost(gallery, post))
                        .collect(Collectors.toList());
                filteredUserPosts.addAll(galleryPosts);
            }
            Collections.shuffle(filteredUserPosts, new Random());

            userPosts = new MutableLiveData<List<UserPost>>(filteredUserPosts);
        }
        return userPosts;
    }

    public LiveData<List<UserGallery>> getUserGalleries(long userId){

        if (userGalleriesWithPosts == null) {

            List<Gallery> userGalleries =  dataRepository
                    .getGalleriesData()
                    .stream()
                    .filter(gallery -> gallery.getMembers().contains(userId))
                    .collect(Collectors.toList());

            List<UserGallery> filteredUserGalleries = new ArrayList<>();
            for(Gallery gallery: userGalleries){
                List<Post> galleryPosts =  dataRepository
                        .getPostsData()
                        .stream()
                        .filter(post -> post.getGallery() == gallery.getId())
                        .collect(Collectors.toList());

                List<User> members = new ArrayList<>();
                for(long memberId: gallery.getMembers()){
                    members.add(getUser(memberId));
                }

                filteredUserGalleries.add(new UserGallery(gallery, galleryPosts, members));
            }

            userGalleriesWithPosts = new MutableLiveData<List<UserGallery>>(filteredUserGalleries);
        }
        return userGalleriesWithPosts;
    }



    //////Below are internal Util methods

    private User getUser(long userId){
        return dataRepository
                .getUsersData()
                .stream()
                .filter(user -> user.getId() == userId)
                .collect(Collectors.toList())
                .get(0);
    }


    //////////////////////////

    public List<StoriesDataModel> getChildrenPosts(long storyId){
        return dataRepository.getStoriesData()
                .stream()
                .filter(storiesDataModel -> storiesDataModel.getParentId() == storyId)
                .collect(Collectors.toList());
    }

    public StoriesDataModel getParentPost(long storyId){
        return dataRepository.getStoriesData()
                .stream()
                .filter(storiesDataModel -> storiesDataModel.getStoryId() == storyId && storiesDataModel.getParentId() == 0)
                .collect(Collectors.toList())
                .get(0);
    }

    public StoriesDataModel getParentPostWithGhosts(long storyId){
        return dataRepository.getStoriesData()
                .stream()
                .filter(storiesDataModel -> storiesDataModel.getStoryId() == storyId && (storiesDataModel.getParentId() == 0 || storiesDataModel.getParentId() == -1))
                .collect(Collectors.toList())
                .get(0);
    }
}