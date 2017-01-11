package com.test.admin.conurbations.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.admin.conurbations.R;
import com.test.admin.conurbations.model.BookBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ItemListActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.item_list)
    RecyclerView recyclerView;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    List<BookBean> list = new ArrayList<>();
    SimpleItemRecyclerViewAdapter viewAdapter;


    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 2) {
                viewAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        show();
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        viewAdapter = new SimpleItemRecyclerViewAdapter(list);
        recyclerView.setAdapter(viewAdapter);
    }

    private void show() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        Document doc = Jsoup.connect("http://www.lz13.cn/lizhi/lizhiwenzhang.html").get();
                        Elements divs = doc.select("#MainMenu");
                        Document divContions = Jsoup.parse(divs.toString());
                        Elements element = divContions.getElementsByTag("li");
                        for (Element links : element) {
                            BookBean bookBean = new BookBean();
                            String title = links.getElementsByTag("a").text();
                            String url = links.select("a").attr("href");
                            bookBean.setTitle(title);
                            bookBean.setUrl(url);
                            list.add(bookBean);
                        }
                        Looper.prepare();
                        Message msg = Message.obtain();
                        msg.what = 2;
                        mHandler.sendMessage(msg);
                        Looper.loop();
                    } catch (Exception e) {
                        Log.i("mytag", e.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<BookBean> mValues;

        public SimpleItemRecyclerViewAdapter(List<BookBean> list) {
            mValues = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_content, parent, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mContentView.setText((position + 1)+"  "+mValues.get(position).getTitle());
            holder.mContentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ItemListItemActivity.class);
                    intent.putExtra(ItemDetailFragment.ITEM_TITLE_ID, mValues.get(position).getUrl());
                    intent.putExtra("item_title", mValues.get(position).getTitle());
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues != null ? mValues.size() : 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final TextView mContentView;
            public ViewHolder(View view) {
                super(view);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
    }
}