package com.wgd.wgdfilepicker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.wgd.wgdfilepickerlib.BaseFragment;
import com.wgd.wgdfilepickerlib.FilePickerActivity;
import com.wgd.wgdfilepickerlib.WGDPickerManager;
import com.wgd.wgdfilepickerlib.bean.FileEntity;

import java.util.List;

/**
 *
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        WGDPickerManager.getInstance().setMaxCount(1);
        Button bt_selevte = findViewById(R.id.bt_selevte);
        bt_selevte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilePickerActivity.start(MainActivity.this, "选择文件", new BaseFragment.FragmentSelect() {
                    @Override
                    public void onSelecte(int type, List<FileEntity> files, Object... objects) {

                    }
                });
            }
        });
    }
}