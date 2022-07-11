package com.wgd.wgdfilepickerlib;

import android.Manifest;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wgd.baservadapterx.CommonAdapter;
import com.wgd.baservadapterx.MultiItemTypeAdapter;
import com.wgd.baservadapterx.base.ViewHolder;
import com.wgd.wgdfilepickerlib.bean.FileEntity;
import com.wgd.wgdfilepickerlib.bean.FileType;
import com.wgd.wgdfilepickerlib.thraed.ThreadManager;
import com.wgd.wgdfilepickerlib.utils.FileSelectFilter;
import com.wgd.wgdfilepickerlib.utils.FileUtils;
import com.wgd.wgdfilepickerlib.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.MediaColumns.DATA;

/**
 * Author: wangguodong
 * Date: 2022/7/6
 * QQ: 1772889689@qq.com
 * WX: gdihh8180
 * Description: 全部文件
 */
public class FileAllFragment extends BaseFragment{

    RecyclerView rvAllFile;
    TextView emptyView;
    LinearLayout ll_back;
    private List<FileEntity> datas = new ArrayList<>();
    private CommonAdapter adapter ;

    private String mPath;
    private String rootPath;
    private FileSelectFilter mFilter;
    //筛选类型条件
//    private String[] mFileTypes = new String[]{};

    @Override
    protected int getLayoutResourceID() {
        return R.layout.fragment_file_all;
    }

    @Override
    protected void initView(View v) {
        rvAllFile = v.findViewById(R.id.rl_all_file);
        emptyView = v.findViewById(R.id.empty_view);
        ll_back = v.findViewById(R.id.ll_back);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvAllFile.setLayoutManager(layoutManager);
        adapter = new CommonAdapter<FileEntity>(getActivity(), R.layout.item_file_picker, datas) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void convert(ViewHolder holder, FileEntity adapterDataBean, int position) {
                ImageView iv_choose = holder.getView(R.id.iv_choose);
                ImageView iv_type = holder.getView(R.id.iv_type);
                TextView tv_name = holder.getView(R.id.tv_name);
                TextView tv_detail = holder.getView(R.id.tv_detail);
                TextView tv_file_size = holder.getView(R.id.tv_file_size);
                final File file = adapterDataBean.getFile();
                tv_name.setText(file.getName());
                if (file.isDirectory()) {
                    iv_type.setImageResource(R.mipmap.file_picker_folder);
                    iv_choose.setVisibility(View.GONE);
                    tv_detail.setVisibility(View.GONE);
                    tv_file_size.setVisibility(View.GONE);
                    Utils.setViewSize(iv_type, 60, 60);
                } else {
                    if(adapterDataBean.getFileType()!=null){
                        String title = adapterDataBean.getFileType().getTitle();
                        if (title.equals("IMG")) {
                            Glide.with(mContext).load(new File(adapterDataBean.getPath())).into(iv_type);
                            Utils.setViewSize(iv_type, 120, 120);
                        } else {
                            iv_type.setImageResource(adapterDataBean.getFileType().getIconStyle());
                            Utils.setViewSize(iv_type, 60, 60);
                        }
                    }else {
                        iv_type.setImageResource(R.mipmap.file_picker_def);
                        Utils.setViewSize(iv_type, 60, 60);
                    }

                    if (WGDPickerManager.getInstance().maxCount<=1){
                        iv_choose.setVisibility(View.GONE);
                    }else {
                        iv_choose.setVisibility(View.VISIBLE);
                    }
                    tv_file_size.setText(FileUtils.getReadableFileSize(file.length()));
                    if (null==adapterDataBean.getFileType()) {
                        tv_detail.setText("类型：文件");
                    }else {
                        tv_detail.setText("类型："+adapterDataBean.getFileType().getTitle());
                    }
                    tv_detail.setVisibility(View.VISIBLE);
                    tv_file_size.setVisibility(View.VISIBLE);
                    if (adapterDataBean.isSelected()) {
                        iv_choose.setImageResource(R.drawable.file_selection);
                    } else {
                        iv_choose.setImageResource(R.drawable.file_selection_no);
                    }
                }
            }
        };
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                try {
                    FileEntity entity = datas.get(position);
                    //如果是文件夹点击进入文件夹
                    if (entity.getFile().isDirectory()) {
                        getIntoChildFolder(position);
                    }else {
                        File file = entity.getFile();
                        ArrayList<FileEntity> files = WGDPickerManager.getInstance().files;
                        if (WGDPickerManager.getInstance().maxCount<=1){
                            files.add(entity);
                            if (null!=jcFragmentSelect)jcFragmentSelect.onSelecte(BaseFragment.SELECTE_FILE_RESULT, files );
                            WGDPickerManager.getInstance().files.clear();
                            getActivity().finish();
                        }else if(files.contains(entity)){
                            files.remove(entity);
                            if (null!=jcFragmentSelect)jcFragmentSelect.onSelecte(BaseFragment.SELECTE_TYPE_NUM_CHANGE, null);
                            entity.setSelected(!entity.isSelected());
                            adapter.notifyItemChanged(position);
                        }else {
                            if(WGDPickerManager.getInstance().files.size()<WGDPickerManager.getInstance().maxCount){
                                files.add(entity);
                                if (null!=jcFragmentSelect)jcFragmentSelect.onSelecte(BaseFragment.SELECTE_TYPE_NUM_CHANGE, null);
                                entity.setSelected(!entity.isSelected());
                                adapter.notifyItemChanged(position);
                            }else {
                                showToast(getString(R.string.file_select_max,WGDPickerManager.getInstance().maxCount));
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        rvAllFile.setAdapter(adapter);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempPath = new File(mPath).getParent();
                if (tempPath == null || mPath.equals(rootPath)) {
                    showToast("最外层了");
                    return;
                }
                mPath = tempPath;
                getNetData();
            }
        });
        initData();
    }

    @SuppressLint("CheckResult")
    private void initData() {
        mPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        RxPermissions rxPermissions = new RxPermissions(getActivity());
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_EXTERNAL_STORAGE
        )
                .subscribe(new Consumer<Boolean>() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void accept(Boolean aBoolean) {
                        if (aBoolean) {
                            //申请的权限全部允许
                            getNetData();
                        } else {
                            //只要有一个权限被拒绝，就会执行
                            showToast("未授权权限，部分功能不能使用");
                        }
                    }
                });
    }

    private void getNetData(){
        ThreadManager.getIO().execute(new Runnable() {
            @Override
            public void run() {
                List<FileEntity> fileEntityList = getData();
                datas.clear();
                if (null!=fileEntityList&&fileEntityList.size()>0){
                    datas.addAll(fileEntityList);
                }
                ThreadManager.onMainHandler(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private List<FileEntity> getData(){
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            showToast("sd卡不可用");
            return null;
        }
        mFilter = new FileSelectFilter(WGDPickerManager.getInstance().getmFileTypesAll());
        return getFileList(mPath);
    }
    /**
     * 根据地址获取当前地址下的所有目录和文件，并且排序
     *
     * @param path
     * @return List<File>
     */
    private List<FileEntity> getFileList(String path) {
        List<FileEntity> fileListByDirPath = FileUtils.getFileListByDirPath(path, mFilter);
        if(fileListByDirPath.size()>0){
            emptyView.setVisibility(View.GONE);
        }else {
            emptyView.setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < WGDPickerManager.getInstance().files.size(); i++) {
            for (int j = 0; j < fileListByDirPath.size(); j++) {
                FileEntity entity = fileListByDirPath.get(j);
                if(WGDPickerManager.getInstance().files.contains(entity)) {
                    entity.setSelected(true);
                    fileListByDirPath.set(j, entity);
                }
            }
        }
        return fileListByDirPath;
    }

    //进入子文件夹
    private void getIntoChildFolder(int position) {
        mPath = datas.get(position).getFile().getAbsolutePath();
        //更新数据源
        getNetData();
    }

}
