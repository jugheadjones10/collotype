package com.app.tiktok.ui.story.bottomsheet.goals;

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
import com.app.tiktok.app.MyApp;
import com.app.tiktok.databinding.FragmentAllGoalsBinding;
import com.app.tiktok.model.Post;
import com.app.tiktok.model.User;
import com.app.tiktok.ui.story.PostsViewModel;

import java.util.List;
import java.util.concurrent.Executor;

public class AllGoalsFragment extends Fragment {

    private static final String KEY_PARENT_POST = "KEY_PARENT_POST";
    private static final String KEY_GALLERY_ID = "KEY_GALLERY_ID";

    private String position;
    private long galleryId;
    private FragmentAllGoalsBinding binding;
    private AllGoalsAdapter allGoalsAdapter;
    private PostsViewModel postsViewModel;
    private List<List<Post>> mProcessPosts;

    public AllGoalsFragment() {
        // Required empty public constructor
    }

    public static AllGoalsFragment newInstance(String position, long galleryId) {
        AllGoalsFragment fragment = new AllGoalsFragment();
        Bundle args = new Bundle();
        args.putString(KEY_PARENT_POST, position);
        args.putLong(KEY_GALLERY_ID, galleryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            position = getArguments().getString(KEY_PARENT_POST);
            galleryId = getArguments().getLong(KEY_GALLERY_ID);
        }

        postsViewModel = new ViewModelProvider(requireActivity()).get(position, PostsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_all_goals, container, false);

        getProcessPosts();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initializeNestedScrollViewBehaviour();
    }



    @Override
    public void onResume() {
        super.onResume();
        if(mProcessPosts != null && binding.allGoalsRecyclerView.getAdapter() == null){
            initializeRecyclerView(mProcessPosts);
        }
    }

    private void getProcessPosts() {
        Executor executor = MyApp.Companion.getExecutorService();
        postsViewModel.getProcessPosts(galleryId, executor).observe(getViewLifecycleOwner(), new Observer<List<List<Post>>>() {
            @Override
            public void onChanged(List<List<Post>> processPosts) {
                if (processPosts != null) {
                    mProcessPosts = processPosts;
                }
            }
        });
    }

    private void initializeRecyclerView(List<List<Post>> processPosts){
        postsViewModel.getMembers().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> members) {
                if(members != null){
                    allGoalsAdapter = new AllGoalsAdapter(getContext(), postsViewModel, processPosts, members);
                    binding.allGoalsRecyclerView.setAdapter(allGoalsAdapter);
                }
            }
        });
    }

    private void initializeNestedScrollViewBehaviour(){
        binding.allGoalsRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
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