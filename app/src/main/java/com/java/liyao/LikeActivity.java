// 这个和新闻列表也差不多

package com.java.liyao;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.java.liyao.adapter.NewsListAdapter;
import com.java.liyao.db.LikeDbHelper;
import com.java.liyao.entity.HistoryInfo;
import com.java.liyao.entity.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class LikeActivity extends AppCompatActivity {
    private RecyclerView newsList;
    private NewsListAdapter newsListAdapter;
    private List<NewsInfo.DataDTO> newsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_like);

        newsList = findViewById(R.id.newsList);
        newsListAdapter = new NewsListAdapter(this);
        newsList.setAdapter(newsListAdapter);

        newsData = new ArrayList<>();

        // 获取收藏列表
        UserInfo userInfo = UserInfo.getUserinfo();
        String eml = userInfo == null ? null : userInfo.getUser_email();
        List<HistoryInfo> like = LikeDbHelper.getInstance(LikeActivity.this).getLike(eml);

        Gson gson = new Gson();
        for (int i = 0; i < like.size(); i++) {
            newsData.add(gson.fromJson(like.get(i).getNews_json(), NewsInfo.DataDTO.class));
        }

        newsListAdapter.setListData(newsData);

        newsListAdapter.setOnItemClickListener((new NewsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NewsInfo.DataDTO dataDTO, int position) {
                Intent intent = new Intent(LikeActivity.this, NewsDetailsActivity.class);
                intent.putExtra("dataDTO", dataDTO);
                startActivity(intent);
            }
        }));

        findViewById(R.id.like_toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // HistoryDbHelper.getInstance(HistoryActivity.this).deleteAllHistory(); // 先都清除一下，方便后续测试
                finish();
            }
        });
    }
}