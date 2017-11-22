package com.example.linch.util;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.example.linch.miniweather.R;

/**
 * Created by linch on 2017/11/11.
 */

public  class ImageRotateUtil {
    private ImageView rotateImage;
    private Context context;
    public ImageRotateUtil(ImageView imageView, Context context){
        this.rotateImage = imageView;
        this.context = context;
    }

    /**
     *启动图标旋转
     */
    public void startRotate(){
        Animation animation = AnimationUtils.loadAnimation(context,R.anim.update_image_rotate);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);

        if(animation!=null){
            rotateImage.startAnimation(animation);
        }
    }

    /**
     * 体制图标旋转
     */
    public void stopRotate(){
        rotateImage.clearAnimation();
    }
}
