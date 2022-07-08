package com.wgd.wgdfilepickerlib;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.fragment.app.FragmentActivity;

/**
 * 简单返回的头部
 */

public abstract class BaseHeadActivity extends FragmentActivity {
    protected TextView titleTv;
    protected TextView titleRight;
    protected ImageView titleBack;
    protected ImageView menu;
    protected RelativeLayout rl_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.jc_basehead_activity);
        initBaseView();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        View.inflate(this, layoutResID, (ViewGroup) findViewById(R.id.base_content));
    }

    /**
     * 调用后 才能得到titleTv否则为空
     */
    private void initBaseView() {
        rl_title = findViewById(R.id.rl_title);
        titleTv = (TextView) findViewById(R.id.title_content);
        titleBack = (ImageView) findViewById(R.id.iv_back);
        menu = (ImageView) findViewById(R.id.menu);
        titleRight = (TextView) findViewById(R.id.right);
        titleRight.setEnabled(true);
        BaseTitleClick baseTitleClick = new BaseTitleClick();
        titleBack.setOnClickListener(baseTitleClick);
        titleRight.setOnClickListener(baseTitleClick);
        titleTv.setOnClickListener(baseTitleClick);
        menu.setOnClickListener(baseTitleClick);
    }

    /**
     * 设置标题栏背景颜色
     *
     * @param color
     */
    public void setTitleBGColor(int color) {
        if (0 != color) {
            if (rl_title != null) {
                rl_title.setBackgroundColor(color);
            }
        }
    }

    /**
     * 设置中间标题
     *
     * @param titleText
     */
    public void setCenterTitle(String titleText) {
        if (titleText != null) {
            if (titleTv != null) {
                titleTv.setText(titleText);
            }
        }
    }

    /**
     * @param text
     * @param drawableRes 设置右侧的按钮，可显示文字或图片
     */
    public void setTitleRight(String text, Drawable drawableRes) {
        if (titleRight == null) {
            return;
        }
        if (text == null && drawableRes == null) {
            titleRight.setVisibility(View.GONE);
        } else {
            titleRight.setVisibility(View.VISIBLE);
        }
        if (text != null) {
            titleRight.setText(text);
            //titleRight.setBackgroundResource(R.color.colorTransparents);
        }
        if (drawableRes != null) {
            titleRight.setBackgroundDrawable(drawableRes);
            titleRight.setText("");
        }

    }

    /**
     * 标题按钮的点击事件
     */
    private class BaseTitleClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.iv_back) {
                onBackClick();
            } else if (id == R.id.right) {
                onRightClick();
            }else if (id ==R.id.title_content){
                onTitleClick();
            }else if (id ==R.id.menu){
                onMenuClick();
            }
        }
    }

    /**
     * 显示menue按钮，
     */
    protected void showMenuClick(boolean b) {
        if (b)menu.setVisibility(View.VISIBLE);
        else menu.setVisibility(View.GONE);
    }
    /**
     * menue点击事件，
     */
    protected void onMenuClick() {
    }

    /**
     * 标题Title，
     */
    protected void onTitleClick() {
    }

    /**
     * 标题中右边的部分，
     */
    protected void onRightClick() {
    }

    /**
     * 返回按钮的点击事件
     */
    public void onBackClick() {
        finish();
    }

}