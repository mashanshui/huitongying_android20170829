package com.huixiangshenghuo.app.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;

import java.util.ArrayList;

public class HelpTabOneActivity extends BaseActivity {


    private ListView listView;
    private ArrayList<Help> dataList;
    private CustomBaseAdapter<Help> adapter;

    public static void startHelpTabOneActivity(Context context){
        Intent intent = new Intent(context,HelpTabOneActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_tab_one);
        initAdapter();
        initView();
    }

    private void initView(){
        initTitleBar_1();
        titleView.setText("帮助中心");
        listView = (ListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }

    private void initAdapter(){
        dataList = new ArrayList<>();
        adapter = new CustomBaseAdapter<Help>(dataList,R.layout.activity_help_tab_one_item) {
            @Override
            public void bindView(ViewHolder holder, Help obj) {
                TextView titleView = holder.getView(R.id.title);
                TextView contentView = holder.getView(R.id.content);
                titleView.setText(obj.title);
                contentView.setText(obj.content);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        Help help = new Help();
        help.title = "1、帮助中心";
        help.content = "帮助中心帮助中心帮助中心帮助中心帮助中心帮助中心";
        dataList.add(help);

        Help help2 = new Help();
        help2.title = "2、疑难问题";
        help2.content = "疑难问题疑难问题疑难问题疑难问题疑难问题疑难问题疑难问题";
        dataList.add(help2);

        adapter.notifyDataSetChanged();
    }

    private class Help{
         String title;
        String content;
    }
}
