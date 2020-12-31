package com.app.tiktok.ui.story;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.tiktok.model.Gallery;
import com.app.tiktok.model.HydratedEvent;
import com.app.tiktok.model.HydratedLiveGallery;
import com.app.tiktok.model.Post;
import com.app.tiktok.model.Product;
import com.app.tiktok.model.User;
import com.app.tiktok.repository.DataRepository;
import com.app.tiktok.ui.galleryinfo.models.GalleryInfoEventsRow;
import com.app.tiktok.ui.galleryinfo.models.GalleryInfoMemberRow;
import com.app.tiktok.ui.galleryinfo.models.GalleryInfoProductsRow;
import com.app.tiktok.ui.galleryinfo.GalleryInfoRecyclerDataItem;
import com.app.tiktok.ui.recommended.GalleryPost;
import com.app.tiktok.utils.PrototypeExceptions;
import com.app.tiktok.utils.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class PostsViewModel extends ViewModel {

    private static final String TAG = "hellson";
    private final DataRepository dataRepository;
    private MutableLiveData<List<Post>> posts;
    private MutableLiveData<HydratedLiveGallery> liveGallery;
    private MutableLiveData<List<User>> members;
    private MutableLiveData<List<Gallery>> collaborators;
    private MutableLiveData<List<GalleryInfoRecyclerDataItem>> galleryInfoRecyclerDataItems;

    private MutableLiveData<Boolean> enableInteractions;
    private MutableLiveData<Boolean> setDraggable;

    private MutableLiveData<HashMap<String, List<?>>> recommendedData;
    private MutableLiveData<List<List<Post>>> processPosts;
    private MutableLiveData<List<User>> randomUsersForLive;

    @ViewModelInject
    public PostsViewModel(DataRepository dataRepository){
        this.dataRepository = dataRepository;
    }

    public LiveData<HashMap<String, List<?>>> getRecommendedData(Gallery gallery, Executor executor) {
        if (recommendedData == null) {

            executor.execute(new Runnable() {
                @Override
                public void run() {

                    HashMap<String, List<?>> finalMap = new HashMap<>();

                    ArrayList<User> hosts = new ArrayList<>(List.of(
                            getUser(gallery.getMembers().get(0)),
                            getUser(gallery.getMembers().get(1))
                    ));

                    List<HydratedEvent> filteredEvents = dataRepository
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

                    finalMap.put("Past Events", filteredEvents.subList(0, filteredEvents.size()/2));
                    finalMap.put("Upcoming Events", filteredEvents.subList(filteredEvents.size()/2, filteredEvents.size()));

                    List<GalleryPost> filteredPosts = dataRepository
                            .getPostsData()
                            .stream()
                            .filter(post -> gallery.getRecposts().contains(post.getId()))
                            //Below is another prototype-specific hack
                            .map(filteredPost -> new GalleryPost(filteredPost, getGallery(getRandomGalleryId())))
                            .collect(Collectors.toList());

                    int boundary = filteredPosts.size()/2;
                    if(boundary%2 != 0){
                        boundary += 1;
                    }
                    finalMap.put("Before Product Posts", filteredPosts.subList(0, boundary));
                    finalMap.put("After Product Posts", filteredPosts.subList(boundary, filteredPosts.size()));

                    List<Product> filteredProducts = dataRepository
                            .getProductsData()
                            .stream()
                            .filter(product -> gallery.getRecproducts().contains(product.getId()))
                            .collect(Collectors.toList());

                    finalMap.put("Products", filteredProducts);

                    recommendedData.postValue(finalMap);
                }
            });

            recommendedData = new MutableLiveData<HashMap<String, List<?>>>();
        }
        return recommendedData;
    }

    public LiveData<List<Post>> getPosts(long galleryId, Executor executor) {
        if (posts == null) {

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    List<Post> filteredPosts = dataRepository
                            .getPostsData()
                            .stream()
                            .filter(post -> post.getGallery() == galleryId)
                            .collect(Collectors.toList());

                    //Don't bring process posts to front for Iron man
                    if(galleryId != 1){
                        filteredPosts = bringProcessPostsToFront(filteredPosts);
                    }

                    List<Post> finalPosts = bringVideoPostsToFront(filteredPosts);

                    posts.postValue(finalPosts);
                }
            });

            posts = new MutableLiveData<List<Post>>();
        }
        return posts;
    }

    public long getRandomGalleryId() {
        Random random = new Random();
        return (long)(random.nextInt((int) PrototypeExceptions.maxOfficialGalleryId - 1) + 1);
    }

    public LiveData<List<GalleryInfoRecyclerDataItem>> getFakeGalleryInfoData(Gallery gallery) {
        if (galleryInfoRecyclerDataItems == null) {

            List<GalleryInfoRecyclerDataItem> finalList = new ArrayList<>();

            //Add member rows
            List<User> members = dataRepository
                    .getUsersData()
                    .stream()
                    .filter(user -> user.getGalleries().contains(gallery.getId()))
                    .collect(Collectors.toList());

            for (int i = 0; i < members.size(); i++) {
                if(i < 2) {
                    GalleryInfoMemberRow memberRow = new GalleryInfoMemberRow(
                            members.get(i),
                            hydrateGalleries(members.get(i).getGalleries())
                    );
                    finalList.add(memberRow);
                }
            }

            //Add fake event and product rows
            List<Post> filteredPosts =  dataRepository
                    .getPostsData()
                    .stream()
                    .filter(post -> post.getGallery() == gallery.getId())
                    .collect(Collectors.toList());

            //TODO: this code is problematic for galleries with only a few posts. Only use this for prototyping
            //Below line is to only show two members per event
            members.subList(2, members.size()).clear();

            finalList.add(getGalleryInfoEventsRow(0,  4, filteredPosts, members));
            finalList.add(getGalleryInfoEventsRow(4,  8, filteredPosts, members));

            List<Product> products  = new ArrayList<>();
            for (int i = 8; i < filteredPosts.size(); i++) {
               products.add(new Product(
                       new Random().nextLong(),
                       "Ironman Water Painting",
                       filteredPosts.get(i).getUrl(),
                       125L,
                       900982232L,
                       12L,
                       gallery.getMembers().get(0)
               ));
            }
            finalList.add(new GalleryInfoProductsRow(
                    products
            ));

            galleryInfoRecyclerDataItems = new MutableLiveData<List<GalleryInfoRecyclerDataItem>>(finalList);
        }
        return galleryInfoRecyclerDataItems;
    }

    private GalleryInfoEventsRow getGalleryInfoEventsRow(int start, int end, List<Post> filteredPosts, List<User> members){
        List<HydratedEvent> events = new ArrayList<>();
        for (int i = start; i < end; i++) {
            events.add(new HydratedEvent(
                    new Random().nextLong(),
                    filteredPosts.get(i).getUrl(),
                    "Water Coloring Session",
                    1L,
                    (ArrayList<User>) members,
                    "@ 5pm 04.56"
            ));
        }
        return new GalleryInfoEventsRow(
            events
        );
    }

    private List<Post> bringProcessPostsToFront(List<Post> posts){
        List<Post> postsWithProcess = new ArrayList<>();
        List<Post> childrenPostsCopy = new ArrayList<>(posts);

        for(Post post : posts){
            if(post.getProcessPosts().size() > 0){
                childrenPostsCopy.remove(post);
                postsWithProcess.add(post);
            }
        }

        List<Post> finalPosts = new ArrayList<Post>(postsWithProcess);
        finalPosts.addAll(childrenPostsCopy);
        return finalPosts;
    }

    private List<Post> bringVideoPostsToFront(List<Post> posts){
        List<Post> videoPosts = new ArrayList<>();
        List<Post> childrenPostsCopy = new ArrayList<>(posts);

        for(Post post : posts){
            String urlType = Utility.INSTANCE.extractS3URLfileType(post.getUrl());
            if(!Utility.INSTANCE.isImage(urlType)){
                childrenPostsCopy.remove(post);
                videoPosts.add(post);
            }
        }

        List<Post> finalPosts = new ArrayList<Post>(videoPosts);
        finalPosts.addAll(childrenPostsCopy);
        return finalPosts;
    }

    //This method here is dangerous. But I guess considering the natural hierearchy of the app it's reasonanle to make the
    //assumption that posts will never be null?
    public LiveData<List<Post>> getPosts() {
        return posts;
    }

    public LiveData<List<List<Post>>> getProcessPosts(long galleryId, Executor executor) {
        if (processPosts == null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    List<Post> parentProcessPosts =  dataRepository
                            .getPostsData()
                            .stream()
                            .filter(post -> post.getGallery() == galleryId && post.getProcessPosts().size() > 0)
                            .collect(Collectors.toList());

                    List<List<Post>> filteredProcessPosts = new ArrayList<>();
                    for(Post parentProcessPost: parentProcessPosts){
                        List<Post> childrenProcessPosts = new ArrayList<>();
                        for(long childrenProcessPostId: parentProcessPost.getProcessPosts()){
                            childrenProcessPosts.add(getPost(childrenProcessPostId));
                        }
                        childrenProcessPosts.add(0, parentProcessPost);
                        filteredProcessPosts.add(childrenProcessPosts);
                    }

                    processPosts.postValue(filteredProcessPosts);

                }
            });

            processPosts = new MutableLiveData<List<List<Post>>>();
        }
        return processPosts;
    }

    public LiveData<List<User>> getRandomUsers(){
        if (randomUsersForLive == null) {

            List<User> randomUsers =  dataRepository
                    .getUsersData()
                    .stream()
                    .limit(4)
                    .collect(Collectors.toList());

            randomUsersForLive = new MutableLiveData<>(randomUsers);
        }
        return randomUsersForLive;
    }

    public LiveData<HydratedLiveGallery> getHyrdratedLiveGallery(Gallery gallery, Executor executor) {
        if (liveGallery == null) {

            executor.execute(new Runnable() {
                @Override
                public void run() {

                    List<Gallery> rivalGalleries =  dataRepository
                            .getGalleriesData()
                            .stream()
                            .filter(filteredGallery -> gallery.getRivalGalleries().contains(filteredGallery.getId()))
                            .collect(Collectors.toList());

                    List<Long> rivalMemberIds = new ArrayList<>(List.of(
                            rivalGalleries.get(0).getMembers().get(0),
                            rivalGalleries.get(1).getMembers().get(0)
                    ));

                    List<User> rivalMembers = dataRepository
                            .getUsersData()
                            .stream()
                            .filter(user -> rivalMemberIds.contains(user.getId()))
                            .collect(Collectors.toList());

                    liveGallery.postValue(new HydratedLiveGallery(
                            gallery,
                            (ArrayList<Gallery>) rivalGalleries,
                            (ArrayList<User>) rivalMembers
                    ));
                }
            });

            liveGallery = new MutableLiveData<>();
        }
        return liveGallery;
    }

    public LiveData<List<User>> getMembers(long galleryId) {
        if (members == null) {
            List<User> filteredMembers =  dataRepository
                    .getUsersData()
                    .stream()
                    .filter(user -> user.getGalleries().contains(galleryId))
                    .collect(Collectors.toList());
            members = new MutableLiveData<List<User>>(filteredMembers);
        }
        return members;
    }

    public LiveData<List<Gallery>> getCollaborators(List<Long> collaboratorIds) {
        if (collaborators == null) {

            List<Gallery> filteredGalleries = new ArrayList<>();
            for(long collabId: collaboratorIds){
                filteredGalleries.add(getGallery(collabId));
            }

            collaborators = new MutableLiveData<List<Gallery>>(filteredGalleries);
        }
        return collaborators;
    }

    public LiveData<List<User>> getMembers() {
        if(members == null){
            members = new MutableLiveData<List<User>>();
        }
        return members;
    }


    public void setDraggable(boolean draggable){
        setDraggable.setValue(draggable);
    }

    public LiveData<Boolean> getDraggable(){
        if(setDraggable == null){
            setDraggable = new MutableLiveData<>(true);
        }
        return setDraggable;
    }

    public void setEnableInteractions(boolean enableInteractionsInput){
        enableInteractions.setValue(enableInteractionsInput);
    }

    public LiveData<Boolean> getEnableInteractions(){
        if(enableInteractions == null){
            enableInteractions = new MutableLiveData<>(true);
        }
        return enableInteractions;
    }


    ////Below are internal Util methods
    private User getUser(long userId){
        return dataRepository
                .getUsersData()
                .stream()
                .filter(user -> user.getId() == userId)
                .collect(Collectors.toList())
                .get(0);
    }

    private Post getPost(long postId){
        return dataRepository
                .getPostsData()
                .stream()
                .filter(post -> post.getId() == postId)
                .collect(Collectors.toList())
                .get(0);
    }

    private Gallery getGallery(long galleryId){
        return dataRepository
                .getGalleriesData()
                .stream()
                .filter(gallery -> gallery.getId() == galleryId)
                .collect(Collectors.toList())
                .get(0);
    }

    private List<Gallery> hydrateGalleries(List<Long> galleries){
        return dataRepository
                .getGalleriesData()
                .stream()
                .filter(gallery -> galleries.contains(gallery.getId()))
                .collect(Collectors.toList()) ;
    }
}
