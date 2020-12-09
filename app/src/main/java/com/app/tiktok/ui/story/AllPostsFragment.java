package com.app.tiktok.ui.story;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.app.tiktok.R;
import com.app.tiktok.databinding.FragmentAllPostsBinding;
import com.app.tiktok.model.Post;
import com.app.tiktok.model.StoriesDataModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllPostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllPostsFragment extends Fragment {

    private static final String KEY_PARENT_POST = "KEY_PARENT_POST";

    private String position;
    private FragmentAllPostsBinding binding;
    private List<StoriesDataModel> childrenPosts;
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
        LayoutInflater layoutInflator = getLayoutInflater();

        for (int i = 0; i < posts.size(); i++) {
            String postUrl = posts.get(i).getUrl();

            ImageView view = (ImageView)layoutInflator.inflate(R.layout.include_bottom_sheet_grid_image, binding.postsGridLayout, false);

            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

            if(i % 7 == 0){
                GridLayout.LayoutParams gridLayoutParams = new GridLayout.LayoutParams();
                gridLayoutParams.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 2);
                gridLayoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 2);
                gridLayoutParams.height = squareLength*2;
                gridLayoutParams.width = squareLength*2;

                view.setLayoutParams(gridLayoutParams);
            }else{
                layoutParams.height = squareLength;
                layoutParams.width = squareLength;

                view.setLayoutParams(layoutParams);
            }

            Glide.get(getContext()).setMemoryCategory(MemoryCategory.HIGH);
            Glide.with(this)
                    .load(postUrl)
                    .thumbnail(0.25f)
                    .override(100, 100)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(view);

            binding.postsGridLayout.addView(view);
        }
    }

    float y1 = 0;
    float y2 = 0;
    float dy = 0;
    String direction;

    private void initializeNestedScrollViewBehaviour(){
        binding.nestedScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch(motionEvent.getAction()) {
                    case (MotionEvent.ACTION_DOWN):
                        postsViewModel.setDraggable(false);
                        y1 = motionEvent.getY();
                        break;
                    case (MotionEvent.ACTION_MOVE): {
                        y2 = motionEvent.getY();

                        dy = y2 - y1;

                        y1 = y2;

                        // Use dx and dy to determine the direction of the move
                        if (dy > 0)
                            direction = "down";
                        else
                            direction = "up";

                        Log.d("scroller", direction);
                        Log.d("scroller", "" + view.getScrollY());

//                        if (direction.equals("up") || (direction.equals("down") && view.getScrollY() != 0)) {
//                            Log.d("scroller", "A");

                            //viewModel.setDraggable(false);
//                        }

//                        if(direction.equals("down") && view.getScrollY() == 0){
//                            Log.d("scroller", "B");
//
//                            viewModel.setDraggable(true);
//                        }

                        break;
                    }
                    case (MotionEvent.ACTION_CANCEL):
                    case (MotionEvent.ACTION_UP):
                        postsViewModel.setDraggable(true);

                        break;
                }
                return false;

            }
        });

    }
}