package com.app.tiktok.ui.story.post;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.tiktok.R;
import com.app.tiktok.databinding.ItemProcessPostBinding;
import com.app.tiktok.model.Post;
import com.app.tiktok.model.Product;
import com.app.tiktok.repository.DataRepository;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

class PostViewModel extends ViewModel {

    private DataRepository dataRepository;
    private MutableLiveData<List<Post>> processPosts;
    private MutableLiveData<List<Product>> products;
    private MutableLiveData<View> processPostsView;

    @ViewModelInject
    public PostViewModel(DataRepository dataRepository){
        this.dataRepository = dataRepository;
    }

//    public LiveData<List<Post>> getProcessPosts(Post post) {
//        if (processPosts == null) {
//
//            List<Post> filteredProcessPosts = new ArrayList<>();
//            for(long processPostId: post.getProcessPosts()){
//                Post processPost =  dataRepository
//                        .getPostsData()
//                        .stream()
//                        .filter(p -> p.getId() == processPostId)
//                        .collect(Collectors.toList())
//                        .get(0);
//                filteredProcessPosts.add(processPost);
//            }
//            filteredProcessPosts.add(0, post);
//
//            processPosts = new MutableLiveData<List<Post>>(filteredProcessPosts);
//        }
//        return processPosts;
//    }

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

    public LiveData<View> getProcessPostsView(Executor executor,
                                              LayoutInflater layoutInflater,
                                              ConstraintLayout storyViewParentConstraint,
                                              List<Post> processPosts,
                                              final StoryViewFragment.OnProcessPostClicked onProcessPostClicked,
                                              Context mContext){
        if (processPostsView == null) {

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    ConstraintLayout processPostLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.layout_process_posts_scroll, storyViewParentConstraint, false);
                    LinearLayout scrollLinearLayout = processPostLayout.findViewById(R.id.scroll_linear_layout);
                    for(Post processPost : processPosts){

                        ItemProcessPostBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_process_post, scrollLinearLayout, false);
                        binding.setProcessCaption(processPost.getCaption());
                        binding.setOnProcessPostClicked(onProcessPostClicked);

                        Glide.with(mContext)
                                .load(processPost.getUrl())
                                .thumbnail(0.25f)
                                .override(50, 50)
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .into(binding.processPostImage);

                        scrollLinearLayout.addView(binding.parentCard);
                    }

                    scrollLinearLayout.getChildAt(0).setSelected(true);

                    processPostsView.postValue(processPostLayout);
                }
            });

            processPostsView = new MutableLiveData<>();
        }
        return processPostsView;
    }


}
