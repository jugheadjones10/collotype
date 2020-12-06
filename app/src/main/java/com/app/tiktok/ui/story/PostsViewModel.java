package com.app.tiktok.ui.story;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.tiktok.model.Gallery;
import com.app.tiktok.model.Post;
import com.app.tiktok.model.User;
import com.app.tiktok.repository.DataRepository;
import com.app.tiktok.utils.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PostsViewModel extends ViewModel {

    private DataRepository dataRepository;
    private MutableLiveData<List<Post>> posts;
    private MutableLiveData<List<User>> members;
    private MutableLiveData<List<Gallery>> collaborators;

    private MutableLiveData<Boolean> setDraggable;

    private MutableLiveData<List<List<Post>>> processPosts;

    @ViewModelInject
    public PostsViewModel(DataRepository dataRepository){
        this.dataRepository = dataRepository;
    }

    public LiveData<List<Post>> getPosts(long galleryId) {
        if (posts == null) {
            List<Post> filteredPosts =  dataRepository
                .getPostsData()
                .stream()
                .filter(post -> post.getGallery() == galleryId)
                .collect(Collectors.toList());


            List<Post> finalPosts = bringVideoPostsToFront(bringProcessPostsToFront(filteredPosts));
            posts = new MutableLiveData<List<Post>>(finalPosts);
        }
        return posts;
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
}
