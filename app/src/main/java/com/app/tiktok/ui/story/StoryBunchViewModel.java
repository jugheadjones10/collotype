package com.app.tiktok.ui.story;

import android.content.Context;
import android.os.Build;

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

//    private MutableLiveData<Integer> position = new MutableLiveData<>(0);
    private DataRepository dataRepository;

    @ViewModelInject
    public StoryBunchViewModel(DataRepository dataRepository){
        this.dataRepository = dataRepository;
    }

    public List<StoriesDataModel> getDataList(long storyId){
        return dataRepository.getStoriesData()
            .stream()
            .filter(storiesDataModel -> storiesDataModel.getParentId() == storyId)
            .collect(Collectors.toList());
    }

    public UserDataModel getUser(long userId){
        return dataRepository.getUsersData()
                .stream()
                .filter(userDataModel -> userDataModel.getId() == userId)
                .collect(Collectors.toList())
                .get(0);
    }
}
