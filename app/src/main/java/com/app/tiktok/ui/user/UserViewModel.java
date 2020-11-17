package com.app.tiktok.ui.user;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.ViewModel;

import com.app.tiktok.model.StoriesDataModel;
import com.app.tiktok.repository.DataRepository;

import java.util.List;
import java.util.stream.Collectors;

public class UserViewModel extends ViewModel {

    private DataRepository dataRepository;

    @ViewModelInject
    public UserViewModel(DataRepository dataRepository){
        this.dataRepository = dataRepository;
    }

    public List<StoriesDataModel> getChildrenPosts(long storyId){
        return dataRepository.getStoriesData()
                .stream()
                .filter(storiesDataModel -> storiesDataModel.getParentId() == storyId)
                .collect(Collectors.toList());
    }

    public StoriesDataModel getParentPost(long storyId){
        return dataRepository.getStoriesData()
                .stream()
                .filter(storiesDataModel -> storiesDataModel.getStoryId() == storyId && storiesDataModel.getParentId() == 0)
                .collect(Collectors.toList())
                .get(0);
    }

    public StoriesDataModel getParentPostWithGhosts(long storyId){
        return dataRepository.getStoriesData()
                .stream()
                .filter(storiesDataModel -> storiesDataModel.getStoryId() == storyId && (storiesDataModel.getParentId() == 0 || storiesDataModel.getParentId() == -1))
                .collect(Collectors.toList())
                .get(0);
    }
}