package com.whatscolors.demo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.whatscolors.demo.base.BaseApplication;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * Description : 图片加载工具类 使用glide框架封装
 */
public class GlideUtils {

    private static RequestOptions requestOptions = new RequestOptions()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.ALL);
    //            .placeholder(R.mipmap.ic_launcher)
//            .error(R.mipmap.ic_launcher)
//            .fallback(R.mipmap.ic_launcher);
    private static Context context = BaseApplication.getAppContext();


    public static void display(ImageView imageView, Object url, Drawable placeholder, Drawable error) {
        display(imageView, url, placeholder, error, 1.0f);
    }

    public static void display(ImageView imageView, Object url, Drawable placeholder, Drawable error, Object sizeMultiplier) {
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        requestOptions.placeholder(placeholder)
                .error(error)
                .fallback(placeholder);
        Glide.with(context).load(url).apply(requestOptions).thumbnail((Float) sizeMultiplier).into(imageView);
    }

    public static void display(ImageView imageView, String url) {
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        requestOptions.placeholder(null)
                .error(null)
                .fallback(null)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);
        Glide.with(context).load(url).apply(requestOptions).into(imageView);
    }

    public static void display(ImageView imageView, Uri uri) {
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);
        Glide.with(context).load(uri).apply(requestOptions).into(imageView);
    }

    public static void display(ImageView imageView, int urlresource) {
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        requestOptions.placeholder(null)
                .error(null)
                .fallback(null);
        Glide.with(context).load(urlresource).apply(requestOptions).into(imageView);
    }

    public static void display(ImageView imageView, File url) {
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        requestOptions.placeholder(null)
                .error(null)
                .fallback(null);
        Glide.with(context).load(url)
                .apply(requestOptions).into(imageView);
    }

    public static Bitmap getBitmap(String url) throws ExecutionException, InterruptedException {


        requestOptions.skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .format(DecodeFormat.PREFER_RGB_565);
//                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

        return Glide.with(context).asBitmap().load(url)
                .apply(requestOptions)
//                .thumbnail(1.0f)
//                .preload()
//                .listener(new RequestListener<Bitmap>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//                        //加载失败
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
//                        //加载成功，resource为加载到的bitmap
//                        return false;
//                    }
//                })
                .submit().get();

    }

//    //先获取缩略图
//    DrawableRequestBuilder<String> thumbnailRequest = Glide
//            .with(context)
//            .load(picture.getSmall())
//            .bitmapTransform(new BlurTransformation(context, 5),//模糊转换
//                    new TopCropTransformation(context));

}
