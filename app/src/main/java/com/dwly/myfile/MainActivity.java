package com.dwly.myfile;

import android.Manifest;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dwly.myfile.Utils.ToastUtil;
import com.dwly.myfile.filebrowser.view.FileBrowser;
import com.dwly.myfile.setting.view.SettingActivity;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private FragmentManager fragmentManager;
    private Toolbar toolbar;
    private NavigationView navigationview;
    private DrawerLayout drawerLayout;
    private TextView headpath;
    private FileBrowser fileBrowser;
    private boolean exit = true;//退出标记

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        applyPermission();
    }

    /**
     * 申请权限
     */
    private void applyPermission() {
        RxPermissions rxPermissions = new RxPermissions(MainActivity.this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean b) throws Exception {
                        if (b) {// 用户已经同意该权限
                            init();
                            fileBrowser = new FileBrowser(MainActivity.this,headpath);
                            fragmentManager.beginTransaction().replace(R.id.content,fileBrowser).commit();
                        } else {
                            // 用户拒绝了该权限
                            ToastUtil.showToast(MainActivity.this,"无权限");
                            finish();
                        }
                    }
                });
    }

    /**
     * 初始化
     */
    private void init() {
        fragmentManager = getFragmentManager();
        headpath = (TextView) findViewById(R.id.headpath);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationview = (NavigationView) findViewById(R.id.navigationview);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//不显示标题
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,0,0){//关联侧边栏
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        actionBarDrawerToggle.syncState();
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        navigationview.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {//侧边栏菜单点击事件
        int i = item.getItemId();
        if (i == R.id.navexit){
            System.exit(0);
        }else if (i == R.id.navsetting){
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
            overridePendingTransition(0,0);
            drawerLayout.closeDrawers();
        }else if (i == R.id.homes){
            drawerLayout.closeDrawers();
            fileBrowser.personPresenter.openDir(getSharedPreferences("mysetting",0).getString("startpath","/sdcard"));
        }else if (i == R.id.books){
            drawerLayout.closeDrawers();
        }else if (i == R.id.xinjian){
            drawerLayout.closeDrawers();
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            final EditText name = new EditText(this);
            builder.setView(name).setTitle(R.string.Create).setNegativeButton(R.string.Folder, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    fileBrowser.personPresenter.creation(headpath.getText().toString(),name.getText().toString(),false);
                }
            }).setPositiveButton(R.string.File, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    fileBrowser.personPresenter.creation(headpath.getText().toString(),name.getText().toString(),true);
                }
            }).setNeutralButton(R.string.No,null).show();
        }
        return false;
    }

    /**
     * 退出的定时任务
     */
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            exit = true;
        }
    };

    @Override
    public void onBackPressed() {//返回键监听
        if (drawerLayout.isDrawerOpen(findViewById(R.id.navigationview))){
            drawerLayout.closeDrawers();
        }else {
            if (headpath.getText().toString().equals("/sdcard")){
                if (exit){
                    exit = false;
                    ToastUtil.showToast(this,"再按一次退出");
                    handler.sendEmptyMessageDelayed(0,2000);
                }else {
                    super.onBackPressed();
                }
            } else{
                fileBrowser.personPresenter.openDir(headpath.getText().toString().substring(0,headpath.getText().toString().lastIndexOf("/")));
            }
        }
    }

    //创建菜单
    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu,menu);
        return super.onCreatePanelMenu(featureId, menu);
    }

    //菜单点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.actionrefresh){//刷新
            fileBrowser.onRefreshFileList();
        }else if (i == R.id.actionsearch){//搜索
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            EditText search = new EditText(this);
            builder.setTitle(getString(R.string.Search)+" :").setView(search)
                    .setNegativeButton(getString(R.string.No), null).setPositiveButton(getString(R.string.Search),null).show();
        }else if (i == R.id.home){
            SharedPreferences.Editor editor = this.getSharedPreferences("mysetting",0).edit();
            editor.putString("startpath",fileBrowser.headpath.getText().toString()).commit();
            fileBrowser.showPrompt(getString(R.string.Finish));
        }else if (i == R.id.addbook){

        }
        return super.onOptionsItemSelected(item);
    }
}
