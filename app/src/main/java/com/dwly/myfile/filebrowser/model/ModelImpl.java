package com.dwly.myfile.filebrowser.model;

import com.dwly.myfile.Utils.FileTools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2017/8/10.
 */

public class ModelImpl implements IModel {
    @Override
    public boolean rename(File file,String name) {
        try {
            file.renameTo(new File(file.getParent()+File.separator+name));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean delete(List<File> files) {
        try {
            FileTools.Delete(files);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean move(File file,String path) {
        try {
            file.renameTo(new File(path+File.separator+file.getName()));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean copy(File file,String path) {
        return FileTools.Copy(file,path);
    }

    //文件名比较排序
    static class NameComparator implements Comparator<File> {
        @Override
        public int compare(File t1, File t2) {
            return t1.getName().toLowerCase().compareTo(t2.getName().toLowerCase());
        }
    }

    /**
     * 打开文件夹
     * @param path 要打开文件夹的路径
     * @return
     */
    @Override
    public List<File> openDir(String path) {
        List<File> files = new ArrayList<File>();
        List<File> fileList = new ArrayList<File>();
        try {
            for (File file : new File(path).listFiles()){
                if (file.isDirectory()){
                    fileList.add(file);
                }else {
                    files.add(file);
                }
            }
        } catch (Exception e) {
            return null;
        }
        Collections.sort(files,new NameComparator());
        Collections.sort(fileList,new NameComparator());
        fileList.addAll(files);
        return fileList;
    }

    /**
     * 创建文件或文件夹
     * @param path 创建路径
     * @param name 名字
     * @param isFile 创建文件还是文件夹
     * @return
     */
    @Override
    public boolean creation(String path,String name, boolean isFile) {
        try {
            if (isFile){
                if (new File(path,name).createNewFile()){
                    return true;
                }else {
                    return false;
                }
            }else {
                if (new File(path+File.separator+name+File.separator).mkdirs()){
                    return true;
                } else{
                    return false;
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
