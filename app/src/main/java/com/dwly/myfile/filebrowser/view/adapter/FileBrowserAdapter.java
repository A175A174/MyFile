package com.dwly.myfile.filebrowser.view.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.dwly.myfile.R;
import com.dwly.myfile.Utils.ShellUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.dwly.myfile.Utils.FileTools.GetApkIcon;
import static com.dwly.myfile.Utils.FileTools.LongToString;
import static com.dwly.myfile.Utils.FileTools.getpicicon;

/**
 * Created by Administrator on 2017/8/11.
 */

public class FileBrowserAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<File> fileList;
    private List<File> files;
    private boolean headViewSwitch;
    private View headView;

    public FileBrowserAdapter(Context context,List<File> fileList,String headpath){
        this.context = context;
        this.fileList = fileList;
        files = new ArrayList<File>();
        headViewSwitch = headpath.equals("/sdcard")?false:true;
    }

    public void setFileListAndHeadViewSwitch(List<File> fileList,String headpath) {
        files.clear();
        this.fileList = fileList;
        this.headViewSwitch = headpath.equals("/sdcard")?false:true;
    }

    /**
     * ItemClick的回调接口
     */
    public interface OnItemClickLitener {
        void onItemBackClick();
        void onBoxClick(List<File> files,boolean ischeck,File file);
        void onItemClick(View view, int position, List<File> fileList);
        void onItemLongClick(View view , int position , List<File> fileList, List<File> files);
    }
    private OnItemClickLitener mOnItemClickLitener;
    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public int getItemViewType(int position) {
        if (headViewSwitch == true && position == 0){
            return 0;
        }else {
            return 1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = null;
        View view = null;
        if (viewType == 0){
            headView = LayoutInflater.from(context).inflate(R.layout.fileitem_header,parent,false);
            holder = new MyViewHolder(headView);
        }else {
            view = LayoutInflater.from(context).inflate(R.layout.fileitem,parent,false);
            holder = new MyViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == 0){
            headView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickLitener.onItemBackClick();
                }
            });
            return;
        }else {
            if(holder instanceof MyViewHolder) {
                final int pos = headViewSwitch?position-1:position;
                final File file = fileList.get(pos);
                ((MyViewHolder) holder).filebox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickLitener.onBoxClick(files,((MyViewHolder) holder).filebox.isChecked(),file);
                    }
                });
                if (file.isDirectory()){
                    ((MyViewHolder) holder).fileicon.setImageResource(R.drawable.ic_folder_48dp);
                    ((MyViewHolder) holder).filesize.setText("");
                }else {
                    if (file.getName().endsWith(".apk")){
                        ((MyViewHolder) holder).fileicon.setImageDrawable(GetApkIcon(context,file.getAbsolutePath()));
                    }else if (file.getName().endsWith(".jpg")){
                        ((MyViewHolder) holder).fileicon.setImageBitmap(getpicicon(file.getAbsolutePath()));
                    }else {
                        ((MyViewHolder) holder).fileicon.setImageResource(R.drawable.ic_file_48dp);
                    }
                    ((MyViewHolder) holder).filesize.setText(LongToString(file.length()));
                }
                ((MyViewHolder) holder).filename.setText(file.getName());
                ((MyViewHolder) holder).filetime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(file.lastModified())));
                String size = ShellUtils.execCommand("ls -ld " + file.getAbsolutePath(),false,true).successMsg;
                if (size.length() > 10){
                    ((MyViewHolder) holder).fileperm.setText(size.substring(1,10));
                }else {
                    ((MyViewHolder) holder).fileperm.setText(size);
                }
                ((MyViewHolder) holder).fileitem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickLitener.onItemClick(holder.itemView, pos, fileList);
                    }
                });
                ((MyViewHolder) holder).fileitem.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mOnItemClickLitener.onItemLongClick(holder.itemView, pos, fileList, files);
                        return true;
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return headViewSwitch?fileList.size()+1:fileList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private ConstraintLayout fileitem;
        private ImageView fileicon;
        private TextView filename;
        private TextView filetime;
        private TextView fileperm;
        private TextView filesize;
        private CheckBox filebox;
        public MyViewHolder(View itemView) {
            super(itemView);
            if (itemView == headView){
                return;
            }else {
                fileitem = itemView.findViewById(R.id.fileitem);
                fileicon = itemView.findViewById(R.id.fileicon);
                filename = itemView.findViewById(R.id.filename);
                filetime = itemView.findViewById(R.id.filetime);
                fileperm = itemView.findViewById(R.id.fileperm);
                filesize = itemView.findViewById(R.id.filesize);
                filebox = itemView.findViewById(R.id.filebox);
                filebox.setChecked(false);
            }
        }
    }
}
