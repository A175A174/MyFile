package com.dwly.myfile.filebrowser.model;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2017/8/10.
 */

public interface IModel {
    boolean rename(File file,String name);
    boolean delete(List<File> files);
    boolean move(File file,String path);
    boolean copy(File file,String path);
    List<File> openDir(String path);
    boolean creation(String path,String name,boolean isFile);
}
