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
        dest.writeInt(id);
        dest.writeString(username);
        dest.writeInt(followersCount);
        dest.writeInt(viewsCount);
        dest.writeInt(postsCount);
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

    private int id;
    private String username;
    private int followersCount;
    private int viewsCount;
    private int postsCount;
    private String userProfilePicUrl;
    private ArrayList<Integer> galleries;

    public UserDataModel(int id, String username, int followersCount, int viewsCount, int postsCount, String userProfilePicUrl, ArrayList<Integer> galleries) {
        this.id = id;
        this.username = username;
        this.followersCount = followersCount;
        this.viewsCount = viewsCount;
        this.postsCount = postsCount;
        this.userProfilePicUrl = userProfilePicUrl;
        this.galleries = galleries;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(int viewsCount) {
        this.viewsCount = viewsCount;
    }

    public int getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(int postsCount) {
        this.postsCount = postsCount;
    }

    public String getUserProfilePicUrl() {
        return userProfilePicUrl;
    }

    public void setUserProfilePicUrl(String userProfilePicUrl) {
        this.userProfilePicUrl = userProfilePicUrl;
    }

    public ArrayList<Integer> getGalleries() {
        return galleries;
    }

    public void setGalleries(ArrayList<Integer> galleries) {
        this.galleries = galleries;
    }
}
