package com.wgd.wgdfilepickerlib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.wgd.wgdfilepickerlib.bean.FileEntity;

import java.util.List;

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
    private LinearLayout ll_botm_confirm;
    private TextView tv_size, tv_confirm;

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

    @SuppressLint("SetTextI18n")
    private void initView(){
        if (0!=WGDPickerManager.getInstance().color){
            setTitleBGColor(WGDPickerManager.getInstance().color);
        }
        btn_common = (Button) findViewById(R.id.btn_common);
        btn_all = (Button) findViewById(R.id.btn_all);
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        tv_size = (TextView) findViewById(R.id.tv_size);
        ll_botm_confirm = (LinearLayout) findViewById(R.id.ll_botm_confirm);
        if (WGDPickerManager.getInstance().maxCount<=1){
            ll_botm_confirm.setVisibility(View.GONE);
        }else {
            tv_confirm.setText("确认（0/"+WGDPickerManager.getInstance().maxCount+"）");
        }
        btn_common.setOnClickListener(this);
        btn_all.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
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
                        public void onSelecte(int type, List<FileEntity> files, Object... objects) {
                            if (type == BaseFragment.SELECTE_FILE_RESULT){
                                if (null!=mJcFragmentSelect)mJcFragmentSelect.onSelecte(type, files, objects);
                            }else {
                                tv_confirm.setText("确认（"+WGDPickerManager.getInstance().files.size()+"/"+WGDPickerManager.getInstance().maxCount+"）");
                            }
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
                        public void onSelecte(int type, List<FileEntity> files, Object... objects) {
                            if (type == BaseFragment.SELECTE_FILE_RESULT){
                                if (null!=mJcFragmentSelect)mJcFragmentSelect.onSelecte(type, files, objects);
                            }else {
                                tv_confirm.setText("确认（"+WGDPickerManager.getInstance().files.size()+"/"+WGDPickerManager.getInstance().maxCount+"）");
                            }
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

        } else if (id == R.id.tv_confirm) {
            if (null!=mJcFragmentSelect)mJcFragmentSelect.onSelecte
                    (BaseFragment.SELECTE_FILE_RESULT, WGDPickerManager.getInstance().files );
            WGDPickerManager.getInstance().files.clear();
            finish();
        }
    }
}
