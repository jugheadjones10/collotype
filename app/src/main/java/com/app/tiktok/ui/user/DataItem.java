package com.app.tiktok.ui.user;

import android.service.autofill.UserData;

import com.app.tiktok.model.StoriesDataModel;

import java.util.List;

abstract class DataItem {
    long id;
}

class UserHeader extends DataItem {

    UserDataModel userDataModel;

    UserHeader(UserDataModel userDataModel){
        this.userDataModel = userDataModel;
        this.id = Long.MAX_VALUE;
    }
}

class AllUserPosts extends DataItem {
    List<StoriesDataModel> storiesDataModels;

    AllUserPosts(List<StoriesDataModel> storiesDataModels){
        this.storiesDataModels = storiesDataModels;
        this.id = Long.MAX_VALUE;
    }
}

class UserGallery extends DataItem {
    StoriesDataModel parentStory;
    List<StoriesDataModel> storiesDataModels;

    UserGallery(StoriesDataModel parentStory, List<StoriesDataModel> storiesDataModels){
        this.parentStory = parentStory;
        this.storiesDataModels = storiesDataModels;
        this.id = parentStory.getStoryId();
    }
}