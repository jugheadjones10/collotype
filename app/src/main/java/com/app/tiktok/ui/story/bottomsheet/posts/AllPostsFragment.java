package com.app.tiktok.ui.story;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.app.tiktok.R;
import com.app.tiktok.app.MyApp;
import com.app.tiktok.databinding.FragmentAllPostsBinding;
import com.app.tiktok.model.Post;
import com.app.tiktok.model.StoriesDataModel;
import com.arasthel.spannedgridlayoutmanager.SpanSize;
import com.arasthel.spannedgridlayoutmanager.SpannedGridLayoutManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;
import java.util.concurrent.Executor;

import kotlin.jvm.functions.Function1;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllPostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllPostsFragment extends Fragment {

    private static final String KEY_PARENT_POST = "KEY_PARENT_POST";

    private String position;
    private FragmentAllPostsBinding binding;
    private List<Post> childrenPosts;
    private PostsViewModel postsViewModel;
    private int squareLength;

    public AllPostsFragment() {
    }

    public static AllPostsFragment newInstance(String position) {
        AllPostsFragment fragment = new AllPostsFragment();
        Bundle args = new Bundle();
        args.putString(KEY_PARENT_POST, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getString(KEY_PARENT_POST);
        }

        squareLength = getResources().getDisplayMetrics().widthPixels/4;

        postsViewModel = new ViewModelProvider(requireActivity()).get(position, PostsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_all_posts, container, false);

        getPosts();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeNestedScrollViewBehaviour();
    }

    private void getPosts(){
        postsViewModel.getPosts().observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                if(posts != null){
                    populatePosts(posts);
                }
            }
        });
    }

    private void populatePosts(List<Post> posts){

        AllPostsAdapter allPostsAdapter = new AllPostsAdapter(getContext(), posts);
        SpannedGridLayoutManager spannedGridLayoutManager = new SpannedGridLayoutManager(
                SpannedGridLayoutManager.Orientation.VERTICAL, 4);
        spannedGridLayoutManager.setSpanSizeLookup(new SpannedGridLayoutManager.SpanSizeLookup(new Function1<Integer, SpanSize>(){
            @Override public SpanSize invoke(Integer position) {
                if(position%7 == 0) {
                    return new SpanSize(2, 2);
                } else {
                    return new SpanSize(1, 1);
                }
            }
        }));
        binding.allPostsRecyclerView.setLayoutManager(spannedGridLayoutManager);
        binding.allPostsRecyclerView.setAdapter(allPostsAdapter);


//        LayoutInflater layoutInflator = getLayoutInflater();
//
//        for (int i = 0; i < posts.size(); i++) {
//            String postUrl = posts.get(i).getUrl();
//
//            ImageView view = (ImageView)layoutInflator.inflate(R.layout.include_bottom_sheet_grid_image, binding.postsGridLayout, false);
//
//            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//
//            if(i % 7 == 0){
//                GridLayout.LayoutParams gridLayoutParams = new GridLayout.LayoutParams();
//                gridLayoutParams.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 2);
//                gridLayoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 2);
//                gridLayoutParams.height = squareLength*2;
//                gridLayoutParams.width = squareLength*2;
//
//                view.setLayoutParams(gridLayoutParams);
//            }else{
//                layoutParams.height = squareLength;
//                layoutParams.width = squareLength;
//
//                view.setLayoutParams(layoutParams);
//            }
//
//            Glide.get(getContext()).setMemoryCategory(MemoryCategory.HIGH);
//            Glide.with(this)
//                    .load(postUrl)
//                    .thumbnail(0.25f)
//                    .override(100, 100)
//                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                    .into(view);
//
//            binding.postsGridLayout.addView(view);
//        }
    }

    float y1 = 0;
    float y2 = 0;
    float dy = 0;
    String direction;

    private void initializeNestedScrollViewBehaviour(){
        binding.allPostsRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

                switch(e.getAction()) {
                    case (MotionEvent.ACTION_DOWN):
                        postsViewModel.setDraggable(false);
                        y1 = e.getY();
                        break;
                    case (MotionEvent.ACTION_MOVE): {
                        y2 = e.getY();

                        dy = y2 - y1;

                        y1 = y2;

                        // Use dx and dy to determine the direction of the move
                        if (dy > 0)
                            direction = "down";
                        else
                            direction = "up";

//                        Log.d("scroller", direction);
                        Log.d("cheez", "" + rv.canScrollVertically(-1));

//                        if (direction.equals("up") || (direction.equals("down") && rv.getScrollY() != 0)) {
//                            Log.d("cheez", "setDraggable false");
//
//                            viewModel.setDraggable(false);
//                        }

//                        if(direction.equals("down") && !rv.canScrollVertically(-1)){
//                            Log.d("cheez", "setDraggable true");
//                            viewModel.setDraggable(true);
//                        }else{
//
                        //viewModel.setDraggable(false);
//                        }

                        break;
                    }
                    case (MotionEvent.ACTION_CANCEL):
                    case (MotionEvent.ACTION_UP):
                        Log.d("cancelled", "I got cancelled");
                        postsViewModel.setDraggable(true);

                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

    }
}