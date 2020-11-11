package com.app.tiktok.ui.story;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.tiktok.mock.Mock;
import com.app.tiktok.model.ResultData;
import com.app.tiktok.model.StoriesDataModel;
import com.app.tiktok.repository.DataRepository;
import com.app.tiktok.ui.user.UserDataModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import kotlinx.coroutines.Dispatchers;

public class StoryBunchViewModel extends ViewModel {

    private DataRepository dataRepository;
    private MutableLiveData<Boolean> setDraggable = new MutableLiveData<>(true);

    @ViewModelInject
    public StoryBunchViewModel(DataRepository dataRepository){
        this.dataRepository = dataRepository;
    }

    public void setDraggable(boolean draggable){
        setDraggable.setValue(draggable);
    }

    public LiveData<Boolean> getDraggable(){
        return setDraggable;
    }

    public List<StoriesDataModel> getDataList(long storyId){
        return dataRepository.getStoriesData()
            .stream()
            .filter(storiesDataModel -> storiesDataModel.getParentId() == storyId)
            .collect(Collectors.toList());
    }

    public StoriesDataModel getPost(long storyId){
        try{
            return dataRepository.getStoriesData()
                    .stream()
                    .filter(storiesDataModel -> storiesDataModel.getStoryId() == storyId)
                    .collect(Collectors.toList())
                    .get(0);
        }catch(IndexOutOfBoundsException e){
            Log.d("exception", "" + storyId);
            throw e;
        }
    }

    public UserDataModel getUser(long userId){
        return dataRepository.getUsersData()
                .stream()
                .filter(userDataModel -> userDataModel.getId() == userId)
                .collect(Collectors.toList())
                .get(0);
    }

    public List<StoriesDataModel> getPostsWithProcessPosts(long parentPostId){
        //So that means parent posts shouldn't have process posts
        return dataRepository.getStoriesData()
                .stream()
                .filter(storiesDataModel -> storiesDataModel.getParentId() == parentPostId && storiesDataModel.getProcessPostIds().size() > 0)
                .collect(Collectors.toList());
    }
}
