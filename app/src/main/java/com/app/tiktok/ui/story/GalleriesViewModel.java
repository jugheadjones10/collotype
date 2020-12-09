package com.app.tiktok.ui.story;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.tiktok.model.Gallery;
import com.app.tiktok.repository.DataRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GalleriesViewModel extends ViewModel {

    private DataRepository dataRepository;
    private MutableLiveData<List<Gallery>> galleries;

    @ViewModelInject
    public GalleriesViewModel(DataRepository dataRepository){
        this.dataRepository = dataRepository;
    }

    public static List<Long> forbiddenCollabGalleries = new ArrayList<>(List.of(13L, 14L, 15L, 16L, 17L));

    public LiveData<List<Gallery>> getGalleries() {
        if (galleries == null) {
            List<Gallery> filteredGalleries = dataRepository
                    .getGalleriesData()
                    .stream()
                    .filter(gallery -> gallery.getOfficial() && !forbiddenCollabGalleries.contains(gallery.getId()))
                    .collect(Collectors.toList());

            galleries = new MutableLiveData<List<Gallery>>(filteredGalleries);
        }
        return galleries;
    }
}
