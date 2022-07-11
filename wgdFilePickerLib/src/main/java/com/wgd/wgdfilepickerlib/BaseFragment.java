package com.wgd.wgdfilepickerlib;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wgd.wgdfilepickerlib.bean.FileEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Author: wangguodong
 * Date: 2022/2/15
 * QQ: 1772889689@qq.com
 * WX: gdihh8180
 * Description: fragment基类
 */
public abstract class BaseFragment extends Fragment implements Serializable {
    public FragmentSelect jcFragmentSelect;

    public static final int SELECTE_TYPE_NUM_CHANGE = 0;//选择数量更新；
    public static final int SELECTE_FILE_RESULT = 1;//文件选择；

    public static interface FragmentSelect {
        void onSelecte(int type, List<FileEntity> files, Object... objects);
    }

    public FragmentSelect getFragmentSelect() {
        return jcFragmentSelect;
    }

    public void setFragmentSelect(FragmentSelect jcFragmentSelect) {
        this.jcFragmentSelect = jcFragmentSelect;
    }

    protected abstract int getLayoutResourceID();

    protected abstract void initView(View v);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(getLayoutResourceID(), null);;
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    public void showToast(String text){
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

}
