package com.klickit.kcpeteacherapp.adapters;

import android.content.Context;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.klickit.kcpeteacherapp.R;

public class ImageBindingAdapter {
    @BindingAdapter("imageResource")
    public static void setDrawableImage(ImageView view, int imageResource) {
        Context context = view.getContext();
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo);
        Glide.with(context)
                .applyDefaultRequestOptions(requestOptions)
                .load(imageResource)
                .into(view);

    }

    @BindingAdapter("imageUrl")
    public static void setImageUrl(ImageView view, String imageUrl) {
        Context context = view.getContext();
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo);


        Glide.with(context)
                .applyDefaultRequestOptions(requestOptions)
                .load(imageUrl)
                .into(view);

    }
    @BindingAdapter("circularImage")
    public static void setCircularImage(ImageView view, int circularImage){
        Context context = view.getContext();
        Glide.with(context)
                .load(circularImage)
                .apply(RequestOptions.circleCropTransform())
                .into(view);
    }

    @BindingAdapter("circularImageUrl")
    public static void setCircularImageUrl(ImageView view, String url){
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo);
        Context context = view.getContext();
        Glide.with(context)
                .load(url)
                .apply(requestOptions)
                .apply(RequestOptions.circleCropTransform())
                .into(view);
    }
}
