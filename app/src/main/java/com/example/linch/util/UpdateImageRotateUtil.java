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

public  class UpdateImageRotateUtil {
    private ImageView titleupdate;
    private Context context;
    public UpdateImageRotateUtil(ImageView imageView, Context context){
        this.titleupdate = imageView;
        this.context = context;
    }
    public void startRotate(){
        Animation animation = AnimationUtils.loadAnimation(context,R.anim.update_image_rotate);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);

        if(animation!=null){
            titleupdate.startAnimation(animation);
        }
    }
    public void stopRotate(){
        titleupdate.clearAnimation();
    }
}
