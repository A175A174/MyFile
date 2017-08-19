package com.dwly.myfile.filebrowser.view;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.dwly.myfile.R;
import com.dwly.myfile.Utils.FileTools;
import com.dwly.myfile.Utils.ToastUtil;
import com.dwly.myfile.filebrowser.presenter.PersonPresenter;
import com.dwly.myfile.filebrowser.view.adapter.FileBrowserAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/10.
 */

public class FileBrowser extends Fragment implements IView,FileBrowserAdapter.OnItemClickLitener,View.OnClickListener{
    private Context context;
    private RecyclerView recyclerView;//文件列表
    private FileBrowserAdapter adapter;//文件列表适配器
    public PersonPresenter personPresenter;
    public TextView headpath;//顶栏路径显示
    private AlertDialog jindu;//延迟动画对象
    private boolean isMove;//标记移动复制
    private FloatingActionButton fab;//新建按钮
    private FloatingActionButton fabpaste;//粘贴按钮
    private FloatingActionButton fabcancel;//移动复制取消按钮
    private boolean some;

    public FileBrowser(Context context,TextView headpath) {
        this.context = context;
        this.headpath = headpath;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        some = false;
        personPresenter = new PersonPresenter(this,context);
        View view = inflater.inflate(R.layout.activity_browser,container,false);
        fab = view.findViewById(R.id.fab);
        fabpaste = view.findViewById(R.id.fabpaste);
        fabcancel = view.findViewById(R.id.fabcancel);
        recyclerView = view.findViewById(R.id.filerecyclelist);
        fab.setOnClickListener(this);
        fabpaste.setOnClickListener(this);
        fabcancel.setOnClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new FileBrowserAdapter(context,new ArrayList<File>(),"");
        personPresenter.openDir(context.getSharedPreferences("mysetting",0).getString("startpath","/sdcard"));
        adapter.setOnItemClickLitener(this);
        recyclerView.setAdapter(adapter);
        return view;
    }

    /**
     * 显示消息提示
     * @param prompt 要提示的内容
     */
    @Override
    public void showPrompt(String prompt) {
        ToastUtil.showToast(context,prompt);
    }

    /**
     * 显示延迟操作动画
     */
    @Override
    public void showLoadView() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);//进度条
        builder.setTitle(R.string.Operation).setView(R.layout.loading).setCancelable(false);//进度条返回键不消失
        builder.setNegativeButton(R.string.Backstage, null);
        this.jindu = builder.show();
    }

    /**
     * 关闭延迟操作动画
     */
    @Override
    public void hideLoadView() {
        this.jindu.dismiss();
    }

    /**
     * 刷新文件列表
     */
    @Override
    public void onRefreshFileList() {

        personPresenter.openDir(headpath.getText().toString());
    }
    /**
     * 打开文件列表
     * @param path
     * @param fileList
     */
    public void onOpenFileList(String path,List<File> fileList) {
        headpath.setText(path);
        adapter.setFileListAndHeadViewSwitch(fileList,path);
        if (some){
            some = false;
            recyclerView.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 返回Item事件
     */
    @Override
    public void onItemBackClick() {
        personPresenter.openDir(headpath.getText().toString().substring(0,headpath.getText().toString().lastIndexOf("/")));
    }

    /**
     * 标记文件事件
     * @param files
     * @param ischeck
     * @param file
     */
    @Override
    public void onBoxClick(List<File> files, boolean ischeck, File file) {
        if (ischeck){
            files.add(file);
        }else {
            files.remove(file);
        }
    }

    /**
     * 单击文件事件
     * @param view
     * @param position
     * @param fileList
     */
    @Override
    public void onItemClick(View view, int position, List<File> fileList) {
        if (fileList.get(position).isDirectory()){
            personPresenter.openDir(fileList.get(position).getAbsolutePath());
        }else {
            final File file = fileList.get(position);
            String suffix = file.getName().substring(file.getName().lastIndexOf("."));
            if (suffix.contains(".apk")){
                View apkmessage = View.inflate(context,R.layout.apk_message,null);
                Map<String,String> message = FileTools.getapkmessage(context,file.getAbsolutePath());
                final PopupWindow popupWindow = new PopupWindow(apkmessage,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                if (message != null){
                    ((TextView)apkmessage.findViewById(R.id.apkname)).setText(message.get("name"));
                    ((TextView)apkmessage.findViewById(R.id.apkversions)).setText(message.get("versions"));
                    ((TextView)apkmessage.findViewById(R.id.apkbanbenname)).setText(message.get("banbenname"));
                    ((TextView)apkmessage.findViewById(R.id.apkpackagename)).setText(message.get("packagename"));
                    ((ImageView)apkmessage.findViewById(R.id.apkicon)).setImageDrawable(FileTools.GetApkIcon(context,file.getAbsolutePath()));
                    ((TextView)apkmessage.findViewById(R.id.installapk)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction(Intent.ACTION_VIEW);
                            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                                Uri uriForFile = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                intent.setDataAndType(uriForFile, context.getContentResolver().getType(uriForFile));
                            }else{
                                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                            }
                            try {
                                context.startActivity(intent);
                            } catch (Exception e) {
                                ToastUtil.showToast(context,"不是标准Apk文件");
                            }
                        }
                    });
                    ((TextView)apkmessage.findViewById(R.id.installcancel)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            popupWindow.dismiss();
                        }
                    });
                    popupWindow.setFocusable(true);
                    popupWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER,0,0);
                }else {
                    ToastUtil.showToast(context,"不是标准Apk文件");
                }
            }else if (suffix.contains(".jpg")){

            }else if(suffix.contains(".txt")){

            }
        }
    }

    /**
     * 长按事件
     * @param view
     * @param position
     * @param fileList 当前路径所有文件
     * @param files 待操作文件
     */
    @Override
    public void onItemLongClick(View view, final int position, final List<File> fileList, final List<File> files) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String[] menu = {getString(R.string.Move),getString(R.string.Copy), getString(R.string.Delete),getString(R.string.Rename),getString(R.string.addclean)};
        builder.setTitle(R.string.Operation).setItems(menu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0){//移动
                    isMove = true;
                    if (files.size() == 0){
                        files.add(fileList.get(position));
                        personPresenter.setFiles(files);
                    }else {
                        some = true;
                        personPresenter.setFiles(files);
                    }
                    fabpaste.setVisibility(View.VISIBLE);
                    fabcancel.setVisibility(View.VISIBLE);
                }else if (i == 1){//复制
                    isMove = false;
                    if (files.size() == 0){
                        files.add(fileList.get(position));
                        personPresenter.setFiles(files);
                    }else {
                        some = true;
                        personPresenter.setFiles(files);
                    }
                    fabpaste.setVisibility(View.VISIBLE);
                    fabcancel.setVisibility(View.VISIBLE);
                }else if (i == 2){//删除
                    if (files.size() == 0){
                        files.add(fileList.get(position));
                        personPresenter.delete(files);
                    }else {
                        some = true;
                        personPresenter.delete(files);
                    }
                }else if (i == 3){//重命名
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    final EditText name = new EditText(context);
                    name.setText(fileList.get(position).getName());
                    builder.setView(name).setTitle(R.string.Rename).setNegativeButton(R.string.No, null).setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            personPresenter.rename(fileList.get(position),name.getText().toString());
                        }
                    }).show();
                }else if (i == 4){

                }
            }
        }).show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.fab://新建
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final EditText name = new EditText(context);
                builder.setView(name).setTitle(R.string.Create).setNegativeButton(R.string.Folder, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        personPresenter.creation(headpath.getText().toString(),name.getText().toString(),false);
                    }
                }).setPositiveButton(R.string.File, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        personPresenter.creation(headpath.getText().toString(),name.getText().toString(),true);
                    }
                }).setNeutralButton(R.string.No,null).show();
                break;
            case R.id.fabpaste://移动复制
                fabpaste.setVisibility(View.GONE);
                fabcancel.setVisibility(View.GONE);
                if (isMove){
                    personPresenter.move(headpath.getText().toString());
                }else {
                    personPresenter.copy(headpath.getText().toString());
                }
                break;
            case R.id.fabcancel://移动复制取消
                personPresenter.setFiles(new ArrayList<File>());
                fabpaste.setVisibility(View.GONE);
                fabcancel.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }
}
