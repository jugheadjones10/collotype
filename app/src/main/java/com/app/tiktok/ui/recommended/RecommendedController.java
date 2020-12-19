package com.app.tiktok.ui.recommended;

import android.content.Context;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.Carousel;
import com.airbnb.epoxy.CarouselModel_;
import com.airbnb.epoxy.EpoxyController;
import com.airbnb.epoxy.EpoxyModel;
import com.app.tiktok.ItemChipGroupBindingModel_;
import com.app.tiktok.ItemProductBindingModel_;
import com.app.tiktok.ItemRecommendedHeaderBindingModel_;
import com.app.tiktok.ItemRectangularPostBindingModel_;
import com.app.tiktok.ItemUserEventBindingModel_;
import com.app.tiktok.model.HydratedEvent;
import com.app.tiktok.model.Product;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

class RecommendedController extends EpoxyController {

    public static final String UPCOMING = "UPCOMING";
    public static final String PAST = "PAST";
    private String pastOrUpcoming = UPCOMING;

    List<HydratedEvent> pastEvents;
    List<HydratedEvent> upcomingEvents;
    List<GalleryPost> beforeProductPosts;
    List<GalleryPost> afterProductPosts;
    List<Product> products;

    @AutoModel
    ItemChipGroupBindingModel_ itemChipGroup;

    Context context;
    private AdapterCallbacks callbacks;
    interface AdapterCallbacks{
        void onPastOrUpcomingClicked(String pastOrUpcoming);
    }

    public RecommendedController(Context context, AdapterCallbacks adapterCallbacks){
        Carousel.setDefaultGlobalSnapHelperFactory(null);
        this.context = context;
        this.callbacks = adapterCallbacks;
    }

    public void setControllerData(List<HydratedEvent> pastEvents,
                             List<HydratedEvent> upcomingEvents,
                             List<GalleryPost> beforeProductPosts,
                             List<GalleryPost> afterProductPosts,
                             List<Product> products) {
        this.pastEvents = pastEvents;
        this.upcomingEvents = upcomingEvents;
        this.beforeProductPosts = beforeProductPosts;
        this.afterProductPosts = afterProductPosts;
        this.products = products;

        requestModelBuild();
    }

    public void setPastOrUpcoming(String pastOrUpcoming){
        this.pastOrUpcoming = pastOrUpcoming;

        requestModelBuild();
    }


    @Override
    protected void buildModels() {

        //================Recommended Events header=================//
        new ItemRecommendedHeaderBindingModel_()
            .id("Recommended Events header")
            .header("Events")
            .spanSizeOverride(new EpoxyModel.SpanSizeOverrideCallback() {
                @Override
                public int getSpanSize(int totalSpanCount, int position, int itemCount) {
                    return 2;
                }
            })
            .addTo(this);

        //================Recommended Events chip group=================//
        itemChipGroup
            .clickListener((model, parentView, clickedView, position) -> {
                if(((Chip)clickedView).getText().toString().equals("Upcoming")){
                    callbacks.onPastOrUpcomingClicked(UPCOMING);
                }else{
                    callbacks.onPastOrUpcomingClicked(PAST);
                }
            })
            .spanSizeOverride(new EpoxyModel.SpanSizeOverrideCallback() {
                @Override
                public int getSpanSize(int totalSpanCount, int position, int itemCount) {
                    return 2;
                }
            })
            .addTo(this);

        //================Past Event models=================//
        List<ItemUserEventBindingModel_> pastEventModels = new ArrayList();
        for (HydratedEvent event : pastEvents) {
            pastEventModels.add(new ItemUserEventBindingModel_()
                    .id(event.getId())
                    .hydratedEvent(event)
                    .context(context)
            );
        }

        new CarouselModel_()
            .id("Past Events")
            .models(pastEventModels)
            .numViewsToShowOnScreen(2.2f)
            .padding(Carousel.Padding.dp(12, 5, 12, 16, 8))
            .addIf(pastOrUpcoming.equals(PAST), this);

        //================Upcoming Event models=================//
        List<ItemUserEventBindingModel_> upcomingEventModels = new ArrayList();
        for (HydratedEvent event : upcomingEvents) {
            upcomingEventModels.add(new ItemUserEventBindingModel_()
                    .id(event.getId())
                    .hydratedEvent(event)
                    .context(context)
            );
        }
        new CarouselModel_()
                .id("Upcoming Events")
                .models(upcomingEventModels)
                .numViewsToShowOnScreen(2.2f)
                .padding(Carousel.Padding.dp(12, 5, 12, 16, 8))
                .addIf(pastOrUpcoming.equals(UPCOMING), this);

        //================Before Product Post models=================//
        for(GalleryPost galleryPost : beforeProductPosts){
            new ItemRectangularPostBindingModel_()
                    .id(galleryPost.getPost().getId())
                    .context(context)
                    .post(galleryPost.getPost())
                    .gallery(galleryPost.getGallery())
                    .addTo(this);
        }

        //================Recommended Products header=================//
        new ItemRecommendedHeaderBindingModel_()
                .id("Recommended Products header")
                .header("Products")
                .spanSizeOverride(new EpoxyModel.SpanSizeOverrideCallback() {
                    @Override
                    public int getSpanSize(int totalSpanCount, int position, int itemCount) {
                        return 2;
                    }
                })
                .addTo(this);

        //================Product models=================//
        List<ItemProductBindingModel_> productModels = new ArrayList();
        for (Product product : products) {
            productModels.add(new ItemProductBindingModel_()
                    .id(product.getId())
                    .context(context)
                    .product(product)
            );
        }

        new CarouselModel_()
                .id("Products")
                .models(productModels)
                .padding(Carousel.Padding.dp(12, 5, 12, 16, 8))
                .addTo(this);

        //================After Product Post models=================//
        for(GalleryPost galleryPost : afterProductPosts){
            new ItemRectangularPostBindingModel_()
                    .id(galleryPost.getPost().getId())
                    .context(context)
                    .post(galleryPost.getPost())
                    .gallery(galleryPost.getGallery())
                    .addTo(this);
        }

    }
}
