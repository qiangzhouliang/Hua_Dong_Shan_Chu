package com.example.qzl.hua_dong_shan_chu;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private ListView listView;
    private List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        //1 准备数据
        for (int i = 0; i < 30; i++) {
            list.add("name - "+i);
        }
        listView.setAdapter(new MyAdapter());
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            //状态改变时执行
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    //如果垂直滑动，则需要关闭已经打开的layout
                    SwipeLayoutManager.getInstance().closerSwipeLayout();
                }
            }
            //只要滚动就会执行
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }

    class MyAdapter extends BaseAdapter implements SwipeLayout.OnSwipeStateChangeListener{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null){
                view = View.inflate(getApplicationContext(),R.layout.adapter_list,null);
            }
            ViewHolder holder = ViewHolder.getHolder(view);
            holder.tv_name.setText(list.get(i));
            holder.swipeLayout.setTag(i);
            holder.swipeLayout.setOnSwipeStateChageListener(this);
            return view;
        }

        @Override
        public void onOpen(Object tag) {
            Toast.makeText(MainActivity.this, "第"+(Integer)tag+1+"个打开了", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onClose(Object tag) {
            Toast.makeText(MainActivity.this, "第"+(Integer)tag+1+"个关闭了", Toast.LENGTH_SHORT).show();
        }
    }

    static class ViewHolder{
        private TextView tv_name,tv_delete;
        private SwipeLayout swipeLayout;
        //构造方法
        public ViewHolder(View convertView){
            tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            tv_delete = (TextView) convertView.findViewById(R.id.tv_delete);
            swipeLayout = (SwipeLayout) convertView.findViewById(R.id.swipeLayout);
        }
        //获取holder
        public static ViewHolder getHolder(View convertView){
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder == null){
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            return holder;
        }
    }
}
