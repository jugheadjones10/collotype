//package com.app.tiktok.ui.story;
//
//import android.content.Context;
//
//import com.airbnb.epoxy.AutoModel;
//import com.airbnb.epoxy.TypedEpoxyController;
//
//import java.util.List;
//
//class BottomPostsController extends TypedEpoxyController<List<String>>{
//
//    Context context;
//    int width;
//    public BottomPostsController(Context context, int width){
//        this.context = context;
//        this.width = width;
//    }
//
//    @AutoModel
//    LargeBottomPostModel_ largeBottomPostModel;
//
//    @Override
//    protected void buildModels(List<String> postUrls) {
//
//        for (int i = 0; i < postUrls.size(); i++) {
//            largeBottomPostModel
//                .url(postUrl)
//                .context(context)
//                .width(i % 7 == 0 ? width * 2 : width)
//                .addTo(this);
//        }
//    }
//
//}
