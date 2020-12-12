//package com.app.tiktok.ui.story;
//
//import android.content.Context;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//
//import androidx.annotation.StringRes;
//
//import com.airbnb.epoxy.EpoxyAttribute;
//import com.airbnb.epoxy.EpoxyHolder;
//import com.airbnb.epoxy.EpoxyModelClass;
//import com.airbnb.epoxy.EpoxyModelWithHolder;
//import com.app.tiktok.R;
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//
//@EpoxyModelClass(layout = R.layout.include_bottom_sheet_grid_image)
//public abstract class LargeBottomPostModel extends EpoxyModelWithHolder<LargeBottomPostModel.LargeBottomPostHolder> {
//
//    // Declare your model properties like this
//    @EpoxyAttribute String url;
//    @EpoxyAttribute int width;
//    @EpoxyAttribute Context context;
//
//    @Override
//    public void bind(LargeBottomPostHolder holder) {
//
//        ViewGroup.LayoutParams params = holder.imageView.getLayoutParams();
//        params.width = width;
//        params.height = width;
//
//        Glide.with(context)
//            .load(url)
//            .thumbnail(0.25f)
//            .override(100, 100)
//            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//            .into(holder.imageView);
//    }
//
//    static class LargeBottomPostHolder extends EpoxyHolder {
//        ImageView imageView;
//
//        @Override
//        protected void bindView(View itemView) {
//            imageView = itemView.findViewById(R.id.include_bottom_sheet_grid);
//        }
//    }
//}