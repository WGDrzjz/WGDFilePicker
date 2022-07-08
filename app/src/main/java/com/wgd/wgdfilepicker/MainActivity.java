package com.wgd.wgdfilepicker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.wgd.wgdfilepickerlib.BaseFragment;
import com.wgd.wgdfilepickerlib.FilePickerActivity;

/**
 *
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button bt_selevte = findViewById(R.id.bt_selevte);
        bt_selevte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilePickerActivity.start(MainActivity.this, "选择文件", new BaseFragment.FragmentSelect() {
                    @Override
                    public void onSelecte(int type, Object... objects) {

                    }
                });
            }
        });
    }
}