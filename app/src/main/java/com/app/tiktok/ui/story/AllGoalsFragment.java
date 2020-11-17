package com.app.tiktok.ui.story;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.app.tiktok.R;
import com.app.tiktok.databinding.FragmentAllGoalsBinding;
import com.app.tiktok.databinding.FragmentAllPostsBinding;
import com.app.tiktok.model.StoriesDataModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;

public class AllGoalsFragment extends Fragment {

    private static final String KEY_PARENT_POST = "KEY_PARENT_POST";

    private StoriesDataModel parentPost;
    private FragmentAllGoalsBinding binding;
    private AllGoalsAdapter allGoalsAdapter;
    private StoryBunchViewModel viewModel;
    private NavController navController;

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

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
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
        Log.d("observe", "All Goals Fragment created");
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

        allGoalsAdapter = new AllGoalsAdapter(getContext(), squareLength, postsWithProcessPosts, parentPost, viewModel, navController);
        binding.allGoalsRecyclerView.setAdapter(allGoalsAdapter);
    }

//    int startPoint = -1;
//    private void initializeNestedScrollViewBehaviour(){
//
//        binding.allGoalsRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//            @Override
//            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
//                Log.d("bushman", "" + e.getAction());
//
//                switch(e.getAction()) {
//
//                    case (MotionEvent.ACTION_DOWN):
//                        Log.d("fuck", "DOWN");
//                        //viewModel.setDraggable(false);
//                       // binding.allGoalsRecyclerView.requestDisallowInterceptTouchEvent(false);
//                        break;
//                    case (MotionEvent.ACTION_MOVE): {
//                        Log.d("fuck", "MOVE");
//                        break;
//                    }
//                    case (MotionEvent.ACTION_CANCEL):
//                    case (MotionEvent.ACTION_UP):
//                        break;
//                }
//                return false;
//            }
//
//            @Override
//            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
//
//            }
//
//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//            }
//        });
//
//
//        GestureListener gestureListener = new GestureListener(binding.allGoalsRecyclerView);
//        GestureDetectorCompat myGestureListener = new GestureDetectorCompat(getContext(), gestureListener);
////        binding.allGoalsRecyclerView.setOnTouchListener(new View.OnTouchListener() {
////            @Override
////            public boolean onTouch(View v, MotionEvent event) {
////                myGestureListener.onTouchEvent(event);
////                return false;
////            }
////        });
//
//    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener{
        RecyclerView targetRecyclerView;

        GestureListener(RecyclerView targetRecyclerView){
            this.targetRecyclerView = targetRecyclerView;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.d("shawn","Gesture onDown");
//            targetRecyclerView.requestDisallowInterceptTouchEvent(true);
            return super.onDown(e);
        }

//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            Log.d("bushman","onFLing");
//            if (Math.abs(velocityX) > Math.abs(velocityY)) {
//                Log.d("bushman","onFLin ---- velX > velY");
//                // Detected a horizontal scroll, prevent the viewpager from switching tabs
//                targetRecyclerView.requestDisallowInterceptTouchEvent(true);
//            }else if(Math.abs(velocityY) > Math.abs(velocityX)){
//                Log.d("bushman","onFLin ---- velY > velX");
//                targetRecyclerView.requestDisallowInterceptTouchEvent(false);
//            }
//            return super.onFling(e1, e2, velocityX, velocityY);
//        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            if(distanceY > 0){
                Log.d("shawn", "Finger going up up up");
                if(viewModel.getDraggable().getValue()){
                    Log.d("shawn", "Finger going up up up : set to NOT draggable");
                    viewModel.setDraggable(false);
                }
            }

            //Distance Y negative means finger going down
            if(distanceY < 0){
                Log.d("shawn", "Finger going down down down");
                if(viewModel.getDraggable().getValue() && targetRecyclerView.getScrollY() != 0){
                    Log.d("shawn", "Finger going down down down: set to NOT draggable");
                    viewModel.setDraggable(false);
                }

                Log.d("observe", "Target recycler view Get Scroll Y" + distanceY);
//                Log.d("shawn", "Target recycler view can scroll vertically" + targetRecyclerView.canScrollVertically(-1));

                if(!viewModel.getDraggable().getValue() && !targetRecyclerView.canScrollVertically(-1)){
                    Log.d("shawn", "Finger going down down down: set TO DRAGGABLE");
                    viewModel.setDraggable(true);
                }
            }


            if(distanceY < 0 && targetRecyclerView.getScrollY() == 0){
               // Log.d("shawn", "me need disabled");
//                viewModel.setDraggable(true);
            }else{
//                viewModel.setDraggable(false);
            }

            //Distance Y positive means finger gonig up

//            if(distanceY > 0){
//                targetRecyclerView.requestDisallowInterceptTouchEvent(false);
//            }

//            Log.d("bushman","onScroll");
//            if (Math.abs(distanceX) > Math.abs(distanceY)) {
//                Log.d("bushman","onScroll ---- distanceX > distanceY");
//                // Detected a horizontal scroll, prevent the viewpager from switching tabs
//                targetRecyclerView.requestDisallowInterceptTouchEvent(true);
//            }else if(Math.abs(distanceY) > Math.abs(distanceX)){
//                Log.d("bushman","onScroll ---- distanceY > distanceX");
//                targetRecyclerView.requestDisallowInterceptTouchEvent(false);
//            }
//            else if (Math.abs(distanceY) > Y_BUFFER) {
//                // Detected a vertical scroll of large enough magnitude so allow the the event
//                // to propagate to ancestor views to allow vertical scrolling.  Without the buffer
//                // a tab swipe would be triggered while holding finger still while glow effect was
//                // visible.
//                mRecyclerView.requestDisallowInterceptTouchEvent(false);
//            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    float y1 = 0;
    float y2 = 0;
    float dy = 0;
    String direction;

    private void initializeNestedScrollViewBehaviour(){
        binding.allGoalsRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

                switch(e.getAction()) {
                    case (MotionEvent.ACTION_DOWN):
                        viewModel.setDraggable(false);
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
                            viewModel.setDraggable(false);
//                        }

                        break;
                    }
                    case (MotionEvent.ACTION_CANCEL):
                    case (MotionEvent.ACTION_UP):
                        viewModel.setDraggable(true);

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


//        binding.allGoalsRecyclerView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//
//                switch(motionEvent.getAction()) {
//                    case (MotionEvent.ACTION_DOWN):
//                        viewModel.setDraggable(false);
//                        y1 = motionEvent.getY();
//                        break;
//                    case (MotionEvent.ACTION_MOVE): {
//                        y2 = motionEvent.getY();
//
//                        dy = y2 - y1;
//
//                        y1 = y2;
//
//                        // Use dx and dy to determine the direction of the move
//                        if (dy > 0)
//                            direction = "down";
//                        else
//                            direction = "up";
//
////                        Log.d("scroller", direction);
////                        Log.d("scroller", "" + view.getScrollY());
//
//                        if (direction.equals("up") || (direction.equals("down") && view.getScrollY() != 0)) {
//                            Log.d("cheez", "setDraggable false");
//
//                            viewModel.setDraggable(false);
//                        }
//
//                        if(direction.equals("down") && view.getScrollY() == 0){
//                            Log.d("cheez", "setDraggable true");
//
//                            viewModel.setDraggable(true);
//                        }
//
//                        break;
//                    }
//                    case (MotionEvent.ACTION_UP):
//                        //viewModel.setDraggable(true);
//
//                        break;
//                }
//                return false;
//
//            }
//        });

    }
}