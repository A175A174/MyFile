package com.dwly.myfile.setting.view;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dwly.myfile.R;
import com.dwly.myfile.setting.view.adapter.BooksAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/18.
 */

public class BooksActivity extends AppCompatActivity implements View.OnClickListener{

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

        fragmentManager.beginTransaction().replace(R.id.settingcontent,new BooksActivity.Cleans(this)).commit();
    }

    @Override
    public void onClick(View view) {
        BooksActivity.this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);//无动画
    }

    public static class Cleans extends Fragment {

        private RecyclerView recyclerView;
        private List<String> lists;
        private Context context;

        public Cleans(Context context){
            this.context = context;
            lists = new ArrayList<String>();
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.data,container,false);
            recyclerView = view.findViewById(R.id.data);
            SharedPreferences preferences = context.getSharedPreferences("books",0);
            Map<String,String> maps = (Map<String, String>) preferences.getAll();
            for (Map.Entry entry : maps.entrySet()){
                lists.add((String) entry.getKey());
            }
            final SharedPreferences.Editor editor = preferences.edit();
            BooksAdapter booksAdapter = new BooksAdapter(lists,context);
            booksAdapter.setOnItemClickLitener(new BooksAdapter.OnItemClickLitener() {
                @Override
                public void onItemClick(String path) {

                }

                @Override
                public void onItemDelClick(String path) {
                    editor.remove(path).commit();
                }
            });
            return view;
        }
    }
}
