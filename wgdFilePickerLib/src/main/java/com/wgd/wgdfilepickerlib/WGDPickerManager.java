package com.wgd.wgdfilepickerlib;

import com.wgd.wgdfilepickerlib.bean.FileEntity;
import com.wgd.wgdfilepickerlib.bean.FileType;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: wangguodong
 * Date: 2022/7/6
 * QQ: 1772889689@qq.com
 * WX: gdihh8180
 * Description: 文件选择器
 */
public class WGDPickerManager {
    public static WGDPickerManager getInstance() {
        return SingletonHolder.INSTANCE;
    }
    private static class SingletonHolder{
        private static final WGDPickerManager INSTANCE = new WGDPickerManager();
    }
    /**
     * 标题栏颜色
     */
    public int color = 0;
    /**
     * 最多能选的文件的个数
     */
    public int maxCount = 3;
    /**
     * 保存结果
     */
    public ArrayList<FileEntity> files;
    /**
     * 筛选条件 类型
     * 最近文件中使用
     */
    public ArrayList<FileType> mFileTypes;
    /**
     * 筛选条件 类型
     * 全部文件中使用
     */
    private String[] mFileTypesAll = new String[]{};

    /**
     * 文件夹筛选
     * 这里包括 微信和QQ中的下载的文件和图片
     */
    public String[] mFilterFolder = new String[]{"MicroMsg/Download","WeiXin","QQfile_recv","MobileQQ/photo"};
    private WGDPickerManager() {
        files = new ArrayList<>();
        mFileTypes = new ArrayList<>();
        initDocTypes();
    }

    public String[] getmFileTypesAll() {
        return mFileTypesAll;
    }

    public void setmFileTypesAll(String[] mFileTypesAll) {
        this.mFileTypesAll = mFileTypesAll;
    }

    public void addDocTypes(List<FileType> fileTypes) {
        if (null!=fileTypes && fileTypes.size()>0){
            for (int i = 0; i < fileTypes.size(); i++) {
                addDocType(fileTypes.get(i));
            }
        }
    }

    public void addDocType(FileType fileType) {
        if (null!=fileType)mFileTypes.add(fileType);
    }

    private void initDocTypes()
    {
        String[] pdfs = {"pdf"};
        addDocType(new FileType("PDF",pdfs, R.mipmap.file_picker_pdf));

        String[] docs = {"doc","docx", "dot","dotx"};
        addDocType(new FileType("DOC",docs,R.mipmap.file_picker_word));

        String[] ppts = {"ppt","pptx"};
        addDocType(new FileType("PPT",ppts,R.mipmap.file_picker_ppt));

        String[] xlss = {"xls","xlt","xlsx","xltx"};
        addDocType(new FileType("XLS",xlss,R.mipmap.file_picker_excle));

        String[] txts = {"txt"};
        addDocType(new FileType("TXT",txts,R.mipmap.file_picker_txt));

        String[] audios = {"m4a","mid","xmf","ogg","wav","mp3"};
        addDocType(new FileType("AUDIO",audios,R.mipmap.file_picker_audio));

        String[] videos = {"mp4","avi","3gp"};
        addDocType(new FileType("VIDEO",videos,R.mipmap.file_picker_video));

        String[] imgs = {"png","jpg","jpeg","gif"};
        addDocType(new FileType("IMG",imgs,0));
    }

    public ArrayList<FileType> getFileTypes() {
        return mFileTypes;
    }


    public WGDPickerManager setMaxCount(int maxCount) {
        this.maxCount = maxCount;
        return this;
    }

    public WGDPickerManager setColor(int color) {
        this.color = color;
        return this;
    }
}
