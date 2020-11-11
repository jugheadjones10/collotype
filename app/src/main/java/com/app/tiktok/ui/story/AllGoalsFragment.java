package com.app.tiktok.ui.story;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.app.tiktok.R;
import com.app.tiktok.databinding.FragmentAllGoalsBinding;
import com.app.tiktok.databinding.FragmentAllPostsBinding;
import com.app.tiktok.model.StoriesDataModel;
import java.util.ArrayList;
import java.util.List;

public class AllGoalsFragment extends Fragment {

    private static final String KEY_PARENT_POST = "KEY_PARENT_POST";

    float y1 = 0;
    float y2 = 0;
    float dy = 0;
    String direction;

    private StoriesDataModel parentPost;
    private FragmentAllGoalsBinding binding;
    private AllGoalsAdapter allGoalsAdapter;
    private StoryBunchViewModel viewModel;

    public AllGoalsFragment() {
        // Required empty public constructor
    }

    public static AllGoalsFragment newInstance(StoriesDataModel parentPost) {
        AllGoalsFragment fragment = new AllGoalsFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_PARENT_POST, parentPost);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            parentPost = getArguments().getParcelable(KEY_PARENT_POST);
        }

        viewModel = new ViewModelProvider(getActivity()).get(StoryBunchViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_all_goals, container, false);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listenForWidth();
        initializeNestedScrollViewBehaviour();
    }

    private void listenForWidth(){
        final ViewTreeObserver observer = binding.allGoalsRecyclerView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // I don't understand onGlobalLayout. What exactly does it listen for?
                int squareLength = binding.allGoalsRecyclerView.getWidth()/4;

                initializeRecyclerView(squareLength);

                ViewTreeObserver innerObserver = binding.allGoalsRecyclerView.getViewTreeObserver();
                innerObserver.removeOnGlobalLayoutListener(this);
            }
        });

    }

    private void initializeRecyclerView(int squareLength){
        List<StoriesDataModel> postsWithProcessPosts =  viewModel.getPostsWithProcessPosts(parentPost.getStoryId());

        allGoalsAdapter = new AllGoalsAdapter(getContext(), squareLength, postsWithProcessPosts, parentPost, viewModel);
        binding.allGoalsRecyclerView.setAdapter(allGoalsAdapter);
    }

    private void initializeNestedScrollViewBehaviour(){
        binding.allGoalsRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch(motionEvent.getAction()) {
                    case (MotionEvent.ACTION_DOWN):
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

                        Log.d("Goalsscroller", direction);
                        Log.d("Goalsscroller", "just scroll Y" + view.getScrollY());
                        Log.d("Goalsscroller", "recycler scrol Y" + binding.allGoalsRecyclerView.computeVerticalScrollOffset());

                        if (direction.equals("up") || (direction.equals("down") && binding.allGoalsRecyclerView.computeVerticalScrollOffset() != 0)) {
                            Log.d("Goalsscroller", "A");

                            viewModel.setDraggable(false);
                        }

                        if(direction.equals("down") && binding.allGoalsRecyclerView.computeVerticalScrollOffset() == 0){
                            Log.d("Goalsscroller", "B");

                            viewModel.setDraggable(true);
                        }

                        break;
                    }
                    case (MotionEvent.ACTION_UP):
                        //viewModel.setDraggable(true);

                        break;
                }
                return false;

            }
        });

    }
}