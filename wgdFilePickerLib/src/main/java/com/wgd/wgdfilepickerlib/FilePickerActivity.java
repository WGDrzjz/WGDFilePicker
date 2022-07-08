package com.wgd.wgdfilepickerlib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Author: wangguodong
 * Date: 2022/7/7
 * QQ: 1772889689@qq.com
 * WX: gdihh8180
 * Description:
 */
public class FilePickerActivity extends BaseFragmentActivity implements View.OnClickListener{

    FileCommonFragment mFileCommonFragment ;
    FileAllFragment mFileAllFragment;
    private Button btn_common,btn_all;

    public static void start(Context context, String title, BaseFragment.FragmentSelect jcFragmentSelect){
        mJcFragmentSelect = jcFragmentSelect;
        Intent intent = new Intent(context, FilePickerActivity.class);
        if (null!=title)intent.putExtra("title", title);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_file_picker;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView(){
        if (0!=WGDPickerManager.getInstance().color){
            setTitleBGColor(WGDPickerManager.getInstance().color);
        }
        btn_common = (Button) findViewById(R.id.btn_common);
        btn_all = (Button) findViewById(R.id.btn_all);
        btn_common.setOnClickListener(this);
        btn_all.setOnClickListener(this);
        setFragment(1);
    }

    private void setFragment(int type) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        switch (type){
            case 1:
                if (null!=mFileAllFragment)hide(mFileAllFragment, fragmentTransaction);
                if(mFileCommonFragment==null){
                    mFileCommonFragment = new FileCommonFragment();
                    if (WGDPickerManager.getInstance().maxCount<=1)mFileCommonFragment.setFragmentSelect(mJcFragmentSelect);
                    else mFileCommonFragment.setFragmentSelect(new BaseFragment.FragmentSelect() {
                        @Override
                        public void onSelecte(int type, Object... objects) {

                        }
                    });
                    addFragment(mFileCommonFragment, fragmentTransaction);
                }else {
                    show(mFileCommonFragment, fragmentTransaction);
                }
                break;
            case 2:
                if (null!=mFileCommonFragment)hide(mFileCommonFragment, fragmentTransaction);
                if(mFileAllFragment==null){
                    mFileAllFragment = new FileAllFragment();
                    if (WGDPickerManager.getInstance().maxCount<=1)mFileAllFragment.setFragmentSelect(mJcFragmentSelect);
                    else mFileAllFragment.setFragmentSelect(new BaseFragment.FragmentSelect() {
                        @Override
                        public void onSelecte(int type, Object... objects) {

                        }
                    });
                    addFragment(mFileAllFragment, fragmentTransaction);
                }else {
                    show(mFileAllFragment, fragmentTransaction);
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_common) {
            setFragment(1);
            btn_common.setBackgroundResource(R.mipmap.no_read_pressed);
            btn_common.setTextColor(ContextCompat.getColor(this,R.color.white));
            btn_all.setBackgroundResource(R.mipmap.already_read);
            btn_all.setTextColor(ContextCompat.getColor(this,R.color.blue));
        } else if (id == R.id.btn_all) {
            setFragment(2);
            btn_common.setBackgroundResource(R.mipmap.no_read);
            btn_common.setTextColor(ContextCompat.getColor(this,R.color.blue));
            btn_all.setBackgroundResource(R.mipmap.already_read_pressed);
            btn_all.setTextColor(ContextCompat.getColor(this,R.color.white));
            //            case R.id.tv_confirm:
//                isConfirm = true;
//                setResult(RESULT_OK);
//                finish();
//                break;
        }
    }
}
