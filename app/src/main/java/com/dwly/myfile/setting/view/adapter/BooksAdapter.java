package com.dwly.myfile.setting.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dwly.myfile.R;

import java.util.List;

/**
 * Created by Administrator on 2017/8/18.
 */

public class BooksAdapter extends RecyclerView.Adapter{

    private List<String> lists;
    private Context context;

    public BooksAdapter(List<String> lists,Context context){
        this.context = context;
        this.lists = lists;
    }

    public interface OnItemClickLitener {
        void onItemClick(String path);
        void onItemDelClick(String path);
    }
    private OnItemClickLitener mOnItemClickLitener;
    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.data_item,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder){
            ((MyViewHolder) holder).filename.setText(lists.get(position));
            ((MyViewHolder) holder).filename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickLitener.onItemClick(lists.get(position));
                }
            });
            ((MyViewHolder) holder).button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickLitener.onItemDelClick(lists.get(position));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView filename;
        private Button button;
        public MyViewHolder(View itemView) {
            super(itemView);
            filename = itemView.findViewById(R.id.ppath);
            button = itemView.findViewById(R.id.ddel);
        }
    }
}
