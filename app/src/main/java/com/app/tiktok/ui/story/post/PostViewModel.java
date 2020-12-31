package com.app.tiktok.ui.story.post;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.tiktok.model.Post;
import com.app.tiktok.model.Product;
import com.app.tiktok.repository.DataRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

class PostViewModel extends ViewModel {

    private final DataRepository dataRepository;
    private MutableLiveData<List<Post>> processPosts;
    private MutableLiveData<List<Product>> products;

    @ViewModelInject
    public PostViewModel(DataRepository dataRepository){
        this.dataRepository = dataRepository;
    }


    public LiveData<List<Post>> getProcessPostsAsync(Post post, Executor executor) {
        if (processPosts == null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    List<Post> filteredProcessPosts = new ArrayList<>();
                    for(long processPostId: post.getProcessPosts()){
                        Post processPost =  dataRepository
                                .getPostsData()
                                .stream()
                                .filter(p -> p.getId() == processPostId)
                                .collect(Collectors.toList())
                                .get(0);
                        filteredProcessPosts.add(processPost);
                    }
                    filteredProcessPosts.add(0, post);

                    processPosts.postValue(filteredProcessPosts);
                }
            });
            processPosts = new MutableLiveData<>();
        }
        return processPosts;
    }

    public LiveData<List<Product>> getProducts(Post post) {
        if (products == null) {
            List<Product> filteredProducts = new ArrayList<>();

            for(long productId: post.getProducts()){
                Product product =  dataRepository
                        .getProductsData()
                        .stream()
                        .filter(p -> p.getId() == productId)
                        .collect(Collectors.toList())
                        .get(0);
                filteredProducts.add(product);
            }
            products = new MutableLiveData<List<Product>>(filteredProducts);
        }
        return products;
    }

}
