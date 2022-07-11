package com.wgd.wgdfilepickerlib;

import android.Manifest;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
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
 * Description: 最近文件
 */
public class FileCommonFragment extends BaseFragment{

    RecyclerView rvCommonFile;
    TextView emptyView;
    ProgressBar progress;
    private List<FileEntity> datas = new ArrayList<>();
    private CommonAdapter adapter ;

    @Override
    protected int getLayoutResourceID() {
        return R.layout.fragment_file_common;
    }

    @Override
    protected void initView(View v) {
        rvCommonFile = v.findViewById(R.id.rl_normal_file);
        emptyView = v.findViewById(R.id.empty_view);
        progress = v.findViewById(R.id.progress);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvCommonFile.setLayoutManager(layoutManager);
//        rvCommonFile.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL));
        adapter = new CommonAdapter<FileEntity>(getActivity(), R.layout.item_file_picker, datas) {
            @Override
            protected void convert(ViewHolder holder, FileEntity adapterDataBean, int position) {
                ImageView iv_choose = holder.getView(R.id.iv_choose);
                ImageView iv_type = holder.getView(R.id.iv_type);
                TextView tv_name = holder.getView(R.id.tv_name);
                TextView tv_detail = holder.getView(R.id.tv_detail);
                TextView tv_file_size = holder.getView(R.id.tv_file_size);
                final File file = adapterDataBean.getFile();
                tv_name.setText(adapterDataBean.getName());
                if (null!=file&&file.isDirectory()) {
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

                    tv_file_size.setText("大小："+FileUtils.getReadableFileSize(new File(adapterDataBean.getPath()).length()));
                    tv_detail.setText("类型："+adapterDataBean.getMimeType());
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
                    String absolutePath = entity.getPath();
                    ArrayList<FileEntity> files = WGDPickerManager.getInstance().files;
                    if (WGDPickerManager.getInstance().maxCount<=1){
                        files.add(entity);
                        if (null!=jcFragmentSelect)jcFragmentSelect.onSelecte(BaseFragment.SELECTE_FILE_RESULT, files );
                        WGDPickerManager.getInstance().files.clear();
                        getActivity().finish();
                    }else if(files.contains(entity)){
                        files.remove(entity);
                        if (null!=jcFragmentSelect)jcFragmentSelect.onSelecte(BaseFragment.SELECTE_TYPE_NUM_CHANGE, null );
                        entity.setSelected(!entity.isSelected());
                        adapter.notifyDataSetChanged();
                    }else {
                        if(WGDPickerManager.getInstance().files.size()<WGDPickerManager.getInstance().maxCount){
                            files.add(entity);
                            if (null!=jcFragmentSelect)jcFragmentSelect.onSelecte(BaseFragment.SELECTE_TYPE_NUM_CHANGE, null );
                            entity.setSelected(!entity.isSelected());
                            adapter.notifyDataSetChanged();
                        }else {
                            Toast.makeText(getContext(),getString(R.string.file_select_max,WGDPickerManager.getInstance().maxCount),Toast.LENGTH_SHORT).show();
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
        rvCommonFile.setAdapter(adapter);
        initData();
    }

    @SuppressLint("CheckResult")
    private void initData() {
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
                            ThreadManager.getIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    List<FileEntity> fileEntityList = initLocalFiles();
                                    if (null!=fileEntityList&&fileEntityList.size()>0){
                                        datas.clear();
                                        datas.addAll(fileEntityList);
                                        ThreadManager.onMainHandler(new Runnable() {
                                            @Override
                                            public void run() {
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            //只要有一个权限被拒绝，就会执行
                            showToast("未授权权限，部分功能不能使用");
                        }
                    }
                });
    }


    private final String[] DOC_PROJECTION = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Files.FileColumns.TITLE
    };
    private List<FileEntity> initLocalFiles(){
        List<FileEntity> fileEntities = new ArrayList<>();
        String selection = MediaStore.Files.FileColumns.MIME_TYPE + "= ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? ";
        String[] selectionArgs = new String[]{
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("text"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("doc"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("docx"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("dotx"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("dotx"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("ppt"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("pptx"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("potx"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("ppsx"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("xls"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("xlsx"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("xltx"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("jpg"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("jpg"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("png"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("svg"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("gif")
        };
//        for (String str : selectionArgs) {
//            Log.i("selectionArgs" , str);
//        }
        final Cursor cursor = getActivity().getContentResolver().query(
                MediaStore.Files.getContentUri("external"),//数据源
                DOC_PROJECTION,//查询类型
                selection,//查询条件
                selectionArgs,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC");
        if (cursor != null) {
            fileEntities = getFiles(cursor);
            cursor.close();
        }
        return fileEntities;
    }

    private List<FileEntity> getFiles(Cursor cursor) {
        List<FileEntity> fileEntities = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(_ID));
            String path = cursor.getString(cursor.getColumnIndexOrThrow(DATA));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE));
            if (path != null) {
                FileType fileType = FileUtils.getFileType(WGDPickerManager.getInstance().getFileTypes(), path);
                if (fileType != null && !(new File(path).isDirectory())) {

                    FileEntity entity = new FileEntity(id, title, path);
                    entity.setFileType(fileType);

                    String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE));
                    if (mimeType != null && !TextUtils.isEmpty(mimeType))
                        entity.setMimeType(mimeType);
                    else {
                        entity.setMimeType("");
                    }

                    entity.setSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)));
                    if(WGDPickerManager.getInstance().files.contains(entity)){
                        entity.setSelected(true);
                    }
                    if (!fileEntities.contains(entity))
                        fileEntities.add(entity);
                }
            }
        }
        return fileEntities;
    }
}
