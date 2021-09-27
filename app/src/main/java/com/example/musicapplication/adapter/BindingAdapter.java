package com.example.musicapplication.adapter;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

public class BindingAdapter {
    @androidx.databinding.BindingAdapter("imageUrl")
    public static void loadImage(ShapeableImageView view, String url) {
        Glide.with(view.getContext()).load(url).into(view);
    }
}
