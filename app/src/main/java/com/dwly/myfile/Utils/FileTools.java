package com.dwly.myfile.Utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileTools {

    //文件可读大小转换
    public static String LongToString(long l){
        String size = "";
        DecimalFormat decimalFormat = new DecimalFormat("#.00");//定义格式
        if (l == 0){
            size = l + "B";
        } else if (l < 1024) {
            size = decimalFormat.format((double) l) + "B";
        } else if (l < 1048576) {
            size = decimalFormat.format((double) l / 1024) + "K";
        } else if (l < 1073741824) {
            size = decimalFormat.format((double) l / 1048576) + "M";
        } else {
            size = decimalFormat.format((double) l / 1073741824) +"G";
        }
        return size;
    }

    //复制文件
    public static boolean Copy(File file,String path){
        if (file.isFile()){
            FileInputStream fi = null;
            FileOutputStream fo = null;
            FileChannel in = null;
            FileChannel out = null;
            try {
                fi = new FileInputStream(file);
                fo = new FileOutputStream(new File(path,file.getName()));
                in = fi.getChannel();
                out = fo.getChannel();
                in.transferTo(0,in.size(),out);
            } catch (IOException e) {
               return false;
            }finally {
                try {
                    fi.close();
                    fo.close();
                    in.close();
                    out.close();
                } catch (IOException e) {
                    return false;
                }
            }
            return true;
        }else {
            new File(path,file.getName()).mkdirs();
            for (File f : file.listFiles()){
                Copy(f,path + File.separator + file.getName());
            }
            return true;
        }
    }

    //删除文件
    public static void Delete(File file){
        if (file.isFile()){
            file.delete();
        }else {
            for (File f : file.listFiles()){
                Delete(f);
            }
            file.delete();
        }
    }
    public static void Delete(List<File> fileList){
        for (File file : fileList){
            System.out.println(file.getAbsolutePath());
            Delete(file);
        }
    }

    //获取APK图标
    public static Drawable GetApkIcon(Context context, String apkpath){
        PackageManager pm = context.getPackageManager();
        PackageInfo inof = pm.getPackageArchiveInfo(apkpath,PackageManager.GET_ACTIVITIES);
        try {
            ApplicationInfo appinfo = inof.applicationInfo;
            appinfo.sourceDir = apkpath;
            appinfo.publicSourceDir = apkpath;
            return appinfo.loadIcon(pm);
        } catch (Exception e) {
            return null;
        }
    }

    //获取APK信息
    public static Map<String,String> getapkmessage(Context context, String apkpath){
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkpath,PackageManager.GET_ACTIVITIES);
        try {
            ApplicationInfo appinfo = info.applicationInfo;
            appinfo.sourceDir = apkpath;
            appinfo.publicSourceDir = apkpath;

            Map<String,String> apkmap = new HashMap<>();
            apkmap.put("name",pm.getApplicationLabel(appinfo).toString());
            apkmap.put("packagename",appinfo.packageName);
            apkmap.put("banbenname",info.versionName);
            apkmap.put("versions",String.valueOf(info.versionCode));
            return apkmap;
        } catch (Exception e) {
            return null;
        }
    }

    //获取图片略缩图
    public static Bitmap getpicicon(String picpath){
        Bitmap bitmap = BitmapFactory.decodeFile(picpath);
        return ThumbnailUtils.extractThumbnail(bitmap,48,48);
    }
}
