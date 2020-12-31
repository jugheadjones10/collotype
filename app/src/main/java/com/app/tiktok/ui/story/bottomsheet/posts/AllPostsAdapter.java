package com.app.tiktok.ui.story.bottomsheet.posts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tiktok.R;
import com.app.tiktok.databinding.IncludeBottomSheetGridImageBinding;
import com.app.tiktok.model.Post;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

class AllPostsAdapter extends RecyclerView.Adapter<AllPostsAdapter.PostViewHolder>{

    private final List<Post> posts;
    private final Context mContext;
    private final static int ITEM_LARGE_POST = 0;
    private final static int ITEM_SMALL_POST = 1;

    public AllPostsAdapter(Context mContext, List<Post> posts){
        this.mContext = mContext;
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        IncludeBottomSheetGridImageBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.include_bottom_sheet_grid_image, parent,false);
//        int squareLength = mContext.getResources().getDisplayMetrics().widthPixels/4;
//        if(viewType == ITEM_LARGE_POST){
//            ViewGroup.LayoutParams params = binding.includeBottomSheetGrid.getLayoutParams();
//            params.width = squareLength * 2;
//            params.height = squareLength * 2;
//        }else{
//            ViewGroup.LayoutParams params = binding.includeBottomSheetGrid.getLayoutParams();
//            params.width = squareLength;
//            params.height = squareLength;
//        }
        return new PostViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder viewHolder, int position) {

        Post post = posts.get(position);

        Glide.with(mContext)
                .load(post.getUrl())
                .thumbnail(0.25f)
                .override(50, 50)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(viewHolder.binding.includeBottomSheetGrid);
    }

//    @Override
//    public int getItemViewType(int position) {
//        if(position%7 == 0){
//            return ITEM_LARGE_POST;
//        }else{
//            return ITEM_SMALL_POST;
//        }
//    }


    @Override
    public int getItemCount() {
        return posts.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder{

        IncludeBottomSheetGridImageBinding binding;

        public PostViewHolder(IncludeBottomSheetGridImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
