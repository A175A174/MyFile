package com.dwly.myfile.setting.view;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dwly.myfile.R;

/**
 * Created by Administrator on 2017/8/18.
 */

public class CleanActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        fragmentManager = getFragmentManager();
        toolbar = (Toolbar) findViewById(R.id.settingtoolbar);
        toolbar.setTitle(getString(R.string.Setting));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示返回按钮
        toolbar.setNavigationOnClickListener(this);

        fragmentManager.beginTransaction().replace(R.id.settingcontent,new SettingActivity.SettingFragment(this)).commit();
    }

    @Override
    public void onClick(View view) {
        CleanActivity.this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);//无动画
    }

    public static class Cleans extends Fragment{
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }
}
