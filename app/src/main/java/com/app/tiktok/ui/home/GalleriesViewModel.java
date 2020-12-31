package com.app.tiktok.ui.home;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.tiktok.model.Gallery;
import com.app.tiktok.repository.DataRepository;

import java.util.List;
import java.util.stream.Collectors;

public class GalleriesViewModel extends ViewModel {

    private final DataRepository dataRepository;
    private MutableLiveData<List<Gallery>> galleries;

    @ViewModelInject
    public GalleriesViewModel(DataRepository dataRepository){
        this.dataRepository = dataRepository;
    }

    public LiveData<List<Gallery>> getGalleries() {
        if (galleries == null) {
            List<Gallery> filteredGalleries = dataRepository
                    .getGalleriesData()
                    .stream()
                    .filter(gallery -> gallery.getOfficial())
                    .collect(Collectors.toList());

            galleries = new MutableLiveData<List<Gallery>>(filteredGalleries);
        }
        return galleries;
    }
}