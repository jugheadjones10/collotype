package com.app.tiktok.ui.user;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserDataModel implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public UserDataModel createFromParcel(Parcel in) {
            return new UserDataModel(in);
        }

        public UserDataModel[] newArray(int size) {
            return new UserDataModel[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(username);
        dest.writeLong(followersCount);
        dest.writeLong(viewsCount);
        dest.writeLong(postsCount);
        dest.writeString(userProfilePicUrl);
        dest.writeList(galleries);
    }

    protected UserDataModel(Parcel in) {
        id = in.readInt();
        username = in.readString();
        followersCount = in.readInt();
        viewsCount = in.readInt();
        postsCount = in.readInt();
        userProfilePicUrl = in.readString();
        galleries = in.readArrayList(List.class.getClassLoader());
    }

    private long id;
    private String username;
    private long followersCount;
    private long viewsCount;
    private long postsCount;
    private String userProfilePicUrl;
    private ArrayList<Long> galleries;

    public UserDataModel(long id, String username, long followersCount, long viewsCount, long postsCount, String userProfilePicUrl, ArrayList<Long> galleries) {
        this.id = id;
        this.username = username;
        this.followersCount = followersCount;
        this.viewsCount = viewsCount;
        this.postsCount = postsCount;
        this.userProfilePicUrl = userProfilePicUrl;
        this.galleries = galleries;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(long followersCount) {
        this.followersCount = followersCount;
    }

    public long getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(long viewsCount) {
        this.viewsCount = viewsCount;
    }

    public long getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(long postsCount) {
        this.postsCount = postsCount;
    }

    public String getUserProfilePicUrl() {
        return userProfilePicUrl;
    }

    public void setUserProfilePicUrl(String userProfilePicUrl) {
        this.userProfilePicUrl = userProfilePicUrl;
    }

    public ArrayList<Long> getGalleries() {
        return galleries;
    }

    public void setGalleries(ArrayList<Long> galleries) {
        this.galleries = galleries;
    }
}
