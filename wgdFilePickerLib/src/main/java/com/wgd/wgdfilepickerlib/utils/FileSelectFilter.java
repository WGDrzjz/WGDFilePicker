package com.wgd.wgdfilepickerlib.utils;

import android.util.Log;

import java.io.File;
import java.io.FileFilter;

/**
 * Author: wangguodong
 * Date: 2022/7/6
 * QQ: 1772889689@qq.com
 * WX: gdihh8180
 * Description: File筛选
 */
public class FileSelectFilter implements FileFilter {
    private String[] mTypes;
    public FileSelectFilter(String[] types) {
        this.mTypes = types;
    }
    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        if (mTypes != null && mTypes.length > 0) {
            for (int i = 0; i < mTypes.length; i++) {
                if (file.getName().endsWith(mTypes[i].toLowerCase()) || file.getName().endsWith(mTypes[i].toUpperCase())) {
                    return true;
                }
            }
        }else {
            return true;
        }
        return false;
    }
}
