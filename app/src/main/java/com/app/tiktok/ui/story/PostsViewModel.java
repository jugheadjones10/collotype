package com.app.tiktok.ui.story;

import android.util.Log;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.tiktok.model.Event;
import com.app.tiktok.model.Gallery;
import com.app.tiktok.model.HydratedEvent;
import com.app.tiktok.model.Post;
import com.app.tiktok.model.Product;
import com.app.tiktok.model.User;
import com.app.tiktok.repository.DataRepository;
import com.app.tiktok.ui.galleryinfo.GalleryInfoEventsRow;
import com.app.tiktok.ui.galleryinfo.GalleryInfoMemberRow;
import com.app.tiktok.ui.galleryinfo.GalleryInfoProductsRow;
import com.app.tiktok.ui.galleryinfo.GalleryInfoRecyclerDataItem;
import com.app.tiktok.utils.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class PostsViewModel extends ViewModel {

    private static final String TAG = "hellson";
    private DataRepository dataRepository;
    private MutableLiveData<List<Post>> posts;
    private MutableLiveData<List<User>> members;
    private MutableLiveData<List<Gallery>> collaborators;
    private MutableLiveData<List<GalleryInfoRecyclerDataItem>> galleryInfoRecyclerDataItems;

    private MutableLiveData<Boolean> setDraggable;
    private MutableLiveData<Boolean> expanding;

    private MutableLiveData<List<List<Post>>> processPosts;

    @ViewModelInject
    public PostsViewModel(DataRepository dataRepository){
        this.dataRepository = dataRepository;
    }

    public LiveData<List<Post>> getPosts(long galleryId) {
        if (posts == null) {
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

//            applyGallerySpecificOrderingExceptions(finalPosts, galleryId);

            posts = new MutableLiveData<List<Post>>(finalPosts);
        }
        return posts;
    }
//
//    private List<Post> applyGallerySpecificOrderingExceptions(List<Post> posts, long galleryId){
//        //If gallery is ironman
//        if(galleryId == 5){
//            List<Post> postsWithProcess = new ArrayList<>();
//            List<Post> childrenPostsCopy = new ArrayList<>(posts);
//
//            for(Post post : posts){
//                if(post.getProcessPosts().size() > 0){
//                    childrenPostsCopy.remove(post);
//                    postsWithProcess.add(post);
//                }
//            }
//
//            List<Post> finalPosts = new ArrayList<Post>(postsWithProcess);
//            finalPosts.addAll(childrenPostsCopy);
//            return finalPosts;
        //If gallery is apple vs samsung
//        }
//        else if(galleryId == 5){
//
//        }

//    }

    public LiveData<List<GalleryInfoRecyclerDataItem>> getFakeGalleryInfoData(Gallery gallery) {
        if (galleryInfoRecyclerDataItems == null) {
            Log.d("hoo", "getFakeGalleryInfoData list was null ");

            List<GalleryInfoRecyclerDataItem> finalList = new ArrayList<>();

            //Add member rows
            List<User> members = dataRepository
                    .getUsersData()
                    .stream()
                    .filter(user -> user.getGalleries().contains(gallery.getId()))
                    .collect(Collectors.toList());

            for (int i = 0; i < members.size(); i++) {
                Log.d(TAG, "getFakeGalleryInfoData: " + members.get(i).getUsername());
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

    public LiveData<List<List<Post>>> getProcessPosts(long galleryId) {
        if (processPosts == null) {
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

            processPosts = new MutableLiveData<List<List<Post>>>(filteredProcessPosts);
        }
        return processPosts;
    }

    public LiveData<List<User>> getMembers(long galleryId) {
        Log.d("hoo", "getFakeGalleryInfoData MEMBER list was run but did not register as null ");
        Log.d("hoo", "Members " + members);
        if (members == null) {
            Log.d("hoo", "getFakeGalleryInfoData MEMBER list was null ");
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

    public LiveData<Boolean> getExpanding(){
        if(expanding == null){
            expanding = new MutableLiveData<>(false);
        }
        return expanding;
    }

    public void setExpanding(boolean expandingInput){
        expanding.setValue(expandingInput);
    }

    public LiveData<Boolean> getDraggable(){
        if(setDraggable == null){
            setDraggable = new MutableLiveData<>(true);
        }
        return setDraggable;
    }


    ////Below are internal Util methods
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
