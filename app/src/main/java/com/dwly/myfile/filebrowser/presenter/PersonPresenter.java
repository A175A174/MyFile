package com.dwly.myfile.filebrowser.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.dwly.myfile.R;
import com.dwly.myfile.filebrowser.model.IModel;
import com.dwly.myfile.filebrowser.model.ModelImpl;
import com.dwly.myfile.filebrowser.view.IView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/10.
 */

public class PersonPresenter {
    private Context context;
    private IModel iModel;
    private IView iView;
    private List<File> files;

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public PersonPresenter(IView iView, Context context){
        iModel = new ModelImpl();
        this.iView = iView;
        this.context = context;
        files = new ArrayList<File>();
    }

    public void rename(File file,String name) {
        if (iModel.rename(file,name)){
            iView.onRefreshFileList();
            iView.showPrompt(context.getString(R.string.Finish));
        }else {
            iView.showPrompt(context.getString(R.string.RenameNo));
        }
    }

    public void delete(final List<File> files) {
        iView.showLoadView();
        new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] objects) {
                return iModel.delete(files);
            }
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                iView.hideLoadView();
                iView.onRefreshFileList();
                if ((boolean) o){
                    iView.showPrompt(context.getString(R.string.Finish));
                }else {
                    iView.showPrompt(context.getString(R.string.DeleteNo));
                }
            }
        }.execute();
    }

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setFiles(new ArrayList<File>());
            iView.hideLoadView();
            iView.onRefreshFileList();
        }
    };

    public void move(final String path) {
        iView.showLoadView();
        new Thread(){
            private void moves(List<File> files,String path){
                for (File file : files){
                    File tofile = new File(path,file.getName());
                    if (tofile.isFile()){
                        if (tofile.exists()){
                            iModel.rename(tofile,tofile.getName()+".bak");
                            iModel.move(file,path);
                        }else {
                            iModel.move(file,path);
                        }
                    }else {
                        if (tofile.exists()){
                            List<File> data = new ArrayList<File>();
                            for (File f : file.listFiles()){
                                data.add(f);
                            }
                            moves(data,tofile.getAbsolutePath());
                            file.delete();
                        }else {
                            iModel.move(file,path);
                        }
                    }
                }
            }
            @Override
            public void run() {
                super.run();
                moves(getFiles(),path);
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    public void copy(final String path) {
        iView.showLoadView();
        new Thread(){
            private void copys(List<File> files,String path){
                for (File file : files){
                    File tofile = new File(path,file.getName());
                    if (tofile.isFile()){
                        if (tofile.exists()){
                            iModel.rename(tofile,tofile.getName()+".bak");
                            iModel.copy(file,path);
                        }else {
                            iModel.copy(file,path);
                        }
                    }else {
                        if (tofile.exists()){
                            List<File> data = new ArrayList<File>();
                            for (File f : file.listFiles()){
                                data.add(f);
                            }
                            copys(data,tofile.getAbsolutePath());
                        }else {
                            tofile.mkdirs();
                            iModel.copy(file,path);
                        }
                    }
                }
            }
            @Override
            public void run() {
                super.run();
                copys(getFiles(),path);
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    public void openDir(String path) {
        iView.showLoadView();
        List<File> fileList = iModel.openDir(path);
        if (fileList == null){
            iView.showPrompt(context.getString(R.string.NoExisted));
            iView.onOpenFileList("/sdcard",iModel.openDir("/sdcard"));
        }else {
            iView.onOpenFileList(path,fileList);
        }
        iView.hideLoadView();
    }

    public void creation(String path,String name,boolean isFile){
        if (iModel.creation(path,name,isFile)){
            iView.onRefreshFileList();
            iView.showPrompt(context.getString(R.string.Finish));
        }else {
            iView.showPrompt(context.getString(R.string.CreateNo));
        }
    }
}
