package com.app.tiktok.ui.user.events;

import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.tiktok.R;
import com.app.tiktok.app.MyApp;
import com.app.tiktok.databinding.FragmentUserEventsBinding;
import com.app.tiktok.model.HydratedEvent;
import com.app.tiktok.ui.story.UtilViewModel;
import com.app.tiktok.ui.user.UserViewModel;
import com.app.tiktok.utils.Utility;

import java.util.List;
import java.util.concurrent.Executor;

public class UserEventsFragment extends Fragment {
    private static final String USER_ID_PARAM = "USER_ID_PARAM";

    private long userId;
    private FragmentUserEventsBinding binding;
    private UserViewModel userViewModel;
    private UtilViewModel utilViewModel;
    private NavController navController;

    private EventsController controller;

    public UserEventsFragment() {

    }

    public static UserEventsFragment newInstance(long userId){
        UserEventsFragment fragment = new UserEventsFragment();
        Bundle args = new Bundle();
        args.putLong(USER_ID_PARAM, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            userId = getArguments().getLong(USER_ID_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_events, container, false);

        userViewModel = new ViewModelProvider(requireActivity()).get(Long.toString(userId), UserViewModel.class);
        utilViewModel = new ViewModelProvider(requireActivity()).get(UtilViewModel.class);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initializeRecyclerView();
        getUserEvents();
    }

    private void initializeRecyclerView(){
        controller = new EventsController(requireContext());
        binding.userGalleriesRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.userGalleriesRecyclerView.setController(controller);
        binding.userGalleriesRecyclerView.addItemDecoration(new GridSpacingItemDecoration(
            2,
            Utility.INSTANCE.dpToPx(8, requireContext()),true)
        );
    }

    private void getUserEvents(){
        Executor executor = MyApp.Companion.getExecutorService();
        userViewModel.getUserEvents(userId, executor).observe(getViewLifecycleOwner(), new Observer<List<HydratedEvent>>() {
            @Override
            public void onChanged(List<HydratedEvent> userEvents) {
                if(userEvents != null){
                    updateController(userEvents);
                }
            }
        });
    }

    private void updateController(List<HydratedEvent> hydratedEvents) {
        controller.setControllerData(hydratedEvents);
    }

    class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

}