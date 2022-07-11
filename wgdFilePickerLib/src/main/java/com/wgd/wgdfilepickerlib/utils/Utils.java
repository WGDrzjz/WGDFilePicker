package com.wgd.wgdfilepickerlib.utils;

import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Author: wangguodong
 * Date: 2022/7/11
 * QQ: 1772889689@qq.com
 * WX: gdihh8180
 * Description:
 */
public class Utils {

    public static void setViewSize(ImageView imageView, int w, int h){
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = w;
        layoutParams.height = h;
        imageView.setLayoutParams(layoutParams);
    }

}
