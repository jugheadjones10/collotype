package com.app.tiktok.ui.home;

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

    public static final long maxOfficialGalleryId = 15L;
    public static final long adPageId = 8L;
    public static final long appleVSamsung = 7L;

    public static boolean isGalleryForbidden(long galleryId){
        if(galleryId >= maxOfficialGalleryId) {
            return true;
        }else{
            return false;
        }
    }

    public LiveData<List<Gallery>> getGalleries() {
        if (galleries == null) {
            List<Gallery> filteredGalleries = dataRepository
                    .getGalleriesData()
                    .stream()
                    .filter(gallery -> gallery.getOfficial() && !isGalleryForbidden(gallery.getId()))
                    .collect(Collectors.toList());

            galleries = new MutableLiveData<List<Gallery>>(filteredGalleries);
        }
        return galleries;
    }
}