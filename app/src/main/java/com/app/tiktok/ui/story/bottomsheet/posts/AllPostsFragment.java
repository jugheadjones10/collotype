package com.app.tiktok.ui.story.bottomsheet.posts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.app.tiktok.R;
import com.app.tiktok.databinding.FragmentAllPostsBinding;
import com.app.tiktok.model.Post;
import com.app.tiktok.ui.story.PostsViewModel;
import com.arasthel.spannedgridlayoutmanager.SpanSize;
import com.arasthel.spannedgridlayoutmanager.SpannedGridLayoutManager;

import java.util.List;

import kotlin.jvm.functions.Function1;

public class AllPostsFragment extends Fragment {

    private static final String KEY_PARENT_POST = "KEY_PARENT_POST";

    private String position;
    private FragmentAllPostsBinding binding;
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

    }

    private void initializeNestedScrollViewBehaviour(){
        binding.allPostsRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                switch(e.getAction()) {
                    case (MotionEvent.ACTION_DOWN):
                        postsViewModel.setDraggable(false);
                        break;
                    case (MotionEvent.ACTION_CANCEL):
                    case (MotionEvent.ACTION_UP):
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