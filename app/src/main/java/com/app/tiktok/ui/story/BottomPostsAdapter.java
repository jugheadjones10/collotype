package com.app.tiktok.ui.story;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tiktok.R;

import java.util.List;

class BottomPostsAdapter extends RecyclerView.Adapter<BottomPostsAdapter.BottomPostViewHolder> {

    private List<Integer> list;


    public BottomPostsAdapter(List<Integer> horizontalList)
    {
        this.list = horizontalList;
    }


    public class BottomPostViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public BottomPostViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.bottom_post_image);
        }
    }

    @NonNull
    @Override
    public BottomPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_post_item, parent, false);

        return new BottomPostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BottomPostViewHolder holder, int position) {
        holder.imageView.setImageResource(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
