package com.xxx.myapplication.model.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.xxx.myapplication.ConfigClass;
import com.xxx.myapplication.R;

import okhttp3.HttpUrl;


/**
 * Glide工具类
 */
public class GlideUtil {

    /**
     * 加载圆角图片
     */
    public static void load(Context context, String url, final ImageView imageView) {
        if (context != null) {
            loadImage(Glide.with(context).load(url), imageView);
        }
    }

    public static void loadBase(Context context, String url, final ImageView imageView) {
        if (context != null) {
            loadImage(Glide.with(context).load(ConfigClass.BASE_URL  + url), imageView);
        }
    }

    public static void loadBaseCircle(Context context, String url, final ImageView imageView) {
        if (context != null) {
            loadCircleImage(Glide.with(context).load(ConfigClass.BASE_URL  + url), context, imageView);
        }
    }

    public static void loadBaseFillet(Context context, String url, final ImageView imageView) {
        if (context != null) {
            loadFilletImage(Glide.with(context).load(ConfigClass.BASE_URL + url), context, imageView);
        }
    }


    public static void loadCircle(Context context, String url, final ImageView imageView) {
        if (context != null) {
            loadFilletImage(Glide.with(context).load(url), context, imageView);
        }
    }

    public static void loadFillet(Context context, String url, final ImageView imageView) {
        if (context != null) {
            loadFilletImage(Glide.with(context).load(url), context, imageView);
        }
    }


    private static void loadImage(DrawableTypeRequest<?> drawableTypeRequest, final ImageView imageView) {
        drawableTypeRequest
                .error(R.mipmap.ic_launcher)//设置缓存
                .into(imageView);
    }

    private static void loadCircleImage(DrawableTypeRequest<?> drawableTypeRequest, final Context context, final ImageView imageView) {
        drawableTypeRequest.asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .error(R.mipmap.ic_launcher)//设置缓存
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        imageView.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

    private static void loadFilletImage(DrawableTypeRequest<?> drawableTypeRequest, final Context context, final ImageView imageView) {
        drawableTypeRequest.asBitmap()
                .error(R.mipmap.ic_launcher)//设置缓存
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(10);
                        imageView.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

}