package com.dwly.myfile.setting.view;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.dwly.myfile.R;

public class SettingActivity extends AppCompatActivity implements IView,View.OnClickListener{

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

        fragmentManager.beginTransaction().replace(R.id.settingcontent,new SettingFragment(this)).commit();
    }

    @Override
    public void onClick(View view) {
        SettingActivity.this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);//无动画
    }

    public static class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
        private Context context;
        private EditTextPreference stratpath;
        public SettingFragment(Context context){
            this.context = context;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getPreferenceManager().setSharedPreferencesName("mysetting");
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
            addPreferencesFromResource(R.xml.setting);
        }

        /**
         * 显示启动路径
         * @param savedInstanceState
         */
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            stratpath = (EditTextPreference) findPreference("startpath");
            stratpath.setSummary(stratpath.getText());
        }

        /**
         * 修改后立即获取数据
         * @param preferenceScreen
         * @param preference
         * @return
         */
        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        /**
         * 点击更新界面启动路径
         * @param sharedPreferences
         * @param s
         */
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            Preference pref = findPreference(s);
            if (pref instanceof EditTextPreference) {
                EditTextPreference etp = (EditTextPreference) pref;
                pref.setSummary(etp.getText());
            }
        }
    }
}
