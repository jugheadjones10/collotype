package com.app.tiktok.ui.user;

import com.app.tiktok.model.StoriesDataModel;
import com.app.tiktok.model.User;

import java.util.List;

public abstract class DataItem {
    long id;
}

class UserHeader extends DataItem {

    User user;

    UserHeader(User user){
        this.user = user;
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

