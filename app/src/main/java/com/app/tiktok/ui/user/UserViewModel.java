package com.app.tiktok.ui.user;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.tiktok.model.Gallery;
import com.app.tiktok.model.HydratedEvent;
import com.app.tiktok.model.Post;
import com.app.tiktok.model.User;
import com.app.tiktok.repository.DataRepository;
import com.app.tiktok.ui.recommended.GalleryPost;
import com.app.tiktok.ui.user.galleries.UserGallery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class UserViewModel extends ViewModel {

    private final DataRepository dataRepository;
    private MutableLiveData<List<GalleryPost>> userPosts;
    private MutableLiveData<List<UserGallery>> userGalleriesWithPosts;
    private MutableLiveData<List<HydratedEvent>> userEvents;

    @ViewModelInject
    public UserViewModel(DataRepository dataRepository){
        this.dataRepository = dataRepository;
    }

    public LiveData<List<GalleryPost>> getUserPosts(long userId, Executor executor){
        if (userPosts == null) {
            executor.execute(new Runnable() {
                 @Override
                 public void run() {
                     List<Gallery> userGalleries =  dataRepository
                         .getGalleriesData()
                         .stream()
                         .filter(gallery -> gallery.getMembers().contains(userId))
                         .collect(Collectors.toList());

                     List<GalleryPost> filteredUserPosts = new ArrayList<>();
                     for(Gallery gallery: userGalleries){
                         List<GalleryPost> galleryPosts =  dataRepository
                             .getPostsData()
                             .stream()
                             .filter(post -> post.getGallery() == gallery.getId())
                             .map(post -> new GalleryPost(post, gallery))
                             .collect(Collectors.toList());
                         filteredUserPosts.addAll(galleryPosts);
                     }
                     Collections.shuffle(filteredUserPosts, new Random());
                     userPosts.postValue(filteredUserPosts);
                 }
             });
            userPosts = new MutableLiveData<List<GalleryPost>>();
        }
        return userPosts;
    }

    public int getRandomUserIndex() {
        Random random = new Random();
        return (random.nextInt(dataRepository.getUsersData().size() - 1) + 1);
    }

    public LiveData<List<HydratedEvent>> getUserEvents(long userId, Executor executor){

         if (userEvents == null) {
             executor.execute(new Runnable() {
                 @Override
                 public void run() {
                     List<Gallery> userGalleriesWithEvents =  dataRepository
                         .getGalleriesData()
                         .stream()
                         .filter(gallery -> gallery.getMembers().contains(userId) && gallery.getEvents().size() > 0)
                         .collect(Collectors.toList());

                     ArrayList<User> hosts =  new ArrayList<>(List.of(
                             dataRepository.getUsersData().get(getRandomUserIndex()),
                             dataRepository.getUsersData().get(getRandomUserIndex())
                     ));

                     List<HydratedEvent> hydratedEvents = new ArrayList<>();
                     for(Gallery gallery: userGalleriesWithEvents){
                         List<HydratedEvent> filteredHydratedEvents =  dataRepository
                             .getEventsData()
                             .stream()
                             .filter(event -> gallery.getEvents().contains(event.getId()))
                             .map(event -> new HydratedEvent(
                                     event.getId(),
                                     event.getUrl(),
                                     event.getTitle(),
                                     event.getGallery(),
                                     hosts,
                                     event.getDatetime()
                             ))
                             .collect(Collectors.toList());
                         hydratedEvents.addAll(filteredHydratedEvents);
                     }
                     Collections.shuffle(hydratedEvents, new Random());
                     userEvents.postValue(hydratedEvents);
                 }
             });
            userEvents = new MutableLiveData<List<HydratedEvent>>();
        }
        return userEvents;
    }

    public LiveData<List<UserGallery>> getUserGalleries(long userId, Executor executor){
        if (userGalleriesWithPosts == null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
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

                    userGalleriesWithPosts.postValue(filteredUserGalleries);
                }
            });
            userGalleriesWithPosts = new MutableLiveData<List<UserGallery>>();
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

}