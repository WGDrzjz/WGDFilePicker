package com.wgd.wgdfilepickerlib;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.wgd.wgdfilepickerlib.utils.SystemBarTintManager;

/**
 * Author: wangguodong
 * Date: 2022/2/15
 * QQ: 1772889689@qq.com
 * WX: gdihh8180
 * Description: 基础activity，用于加载展示fragment
 */
public abstract class BaseFragmentActivity extends BaseHeadActivity {
    public final static String TAG = "BaseFragmentActivity";
    static BaseFragment.FragmentSelect mJcFragmentSelect ;

    public static void start(Context context){
        Intent intent = new Intent(context, BaseFragmentActivity.class);
        context.startActivity(intent);
    }
    public static void start(Context context, String title, BaseFragment.FragmentSelect jcFragmentSelect){
        mJcFragmentSelect = jcFragmentSelect;
        Intent intent = new Intent(context, BaseFragmentActivity.class);
        if (null!=title)intent.putExtra("title", title);
        context.startActivity(intent);
    }

    protected abstract int getLayoutResourceID();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (0==getLayoutResourceID())setContentView(R.layout.jc_activity_base);
        else setContentView(getLayoutResourceID());

        String title = getIntent().getStringExtra("title");
//        JCLogUtil.e("==JCbaseFragmentActivity==onCreate==title=="+title);
        if (null!=title && !TextUtils.isEmpty(title)){
            setCenterTitle(title);
        }
    }

    protected void showFragment(BaseFragment mJCbaseFragment){
        switchContent(mJCbaseFragment, false);
    }

    protected void addFragment(BaseFragment mJCbaseFragment, FragmentTransaction fragmentTransaction){
        switchContent(mJCbaseFragment, true, fragmentTransaction);
    }

    /**
     * 改变系统标题栏颜色
     * @param activity
     * @param color   color xml文件下的颜色
     */
    public static void initSystemBar(Activity activity, int color) {
        setTranslucentStatus(activity, true);
        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        tintManager.setStatusBarTintEnabled(true);
        // 使用颜色资源
        tintManager.setStatusBarTintResource(color);
    }

    /**
     * 设置系统标题栏的透明度
     * @param activity
     * @param on
     */
    @TargetApi(19)
    private static void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
//        final int bits = WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN;
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

     public BaseFragment switchContent(BaseFragment fragment, boolean needAddToBackStack) {
         FragmentManager fm = getSupportFragmentManager();
         FragmentTransaction fragmentTransaction = fm.beginTransaction();
         return switchContent(fragment, needAddToBackStack, fragmentTransaction);
     }

     public BaseFragment switchContent(BaseFragment fragment, boolean needAddToBackStack
             , FragmentTransaction fragmentTransaction) {

        if (needAddToBackStack) {
//            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.add(R.id.fragment_msg, fragment);
        }else {
            fragmentTransaction.replace(R.id.fragment_msg, fragment);
        }
        try {
            fragmentTransaction.commit();
//            fragmentTransaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fragment;
    }

    public void show(BaseFragment fragment, FragmentTransaction fragmentTransaction) {
        Log.e(TAG, "show: ==========001===============");
        fragmentTransaction.show(fragment);
        try {
            fragmentTransaction.commit();
//            fragmentTransaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG, "show: ==========002===============");
    }
    public void hide(BaseFragment fragment, FragmentTransaction fragmentTransaction) {
        Log.e(TAG, "show: ==========003===============");
        fragmentTransaction.hide(fragment);
//        try {
//            fragmentTransaction.commit();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        Log.e(TAG, "show: ==========004===============");
    }
}
