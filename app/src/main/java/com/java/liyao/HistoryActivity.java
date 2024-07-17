// 这个和新闻列表也差不多

package com.java.liyao;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.java.liyao.adapter.NewsListAdapter;
import com.java.liyao.db.HistoryDbHelper;
import com.java.liyao.entity.HistoryInfo;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView newsList;
    private NewsListAdapter newsListAdapter;
    private List<NewsInfo.DataDTO> newsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);

        newsList = findViewById(R.id.newsList);
        newsListAdapter = new NewsListAdapter(this);
        newsList.setAdapter(newsListAdapter);

        newsData = new ArrayList<>();

        // 获取历史记录
        List<HistoryInfo> history = HistoryDbHelper.getInstance(HistoryActivity.this).getHistory(null);

        Gson gson = new Gson();
        for (int i = 0; i < history.size(); i++) {
            newsData.add(gson.fromJson(history.get(i).getNews_json(), NewsInfo.DataDTO.class));
        }

        newsListAdapter.setListData(newsData);

        newsListAdapter.setOnItemClickListener((new NewsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NewsInfo.DataDTO dataDTO, int position) {
                Intent intent = new Intent(HistoryActivity.this, NewsDetailsActivity.class);
                intent.putExtra("dataDTO", dataDTO);
                startActivity(intent);
            }
        }));

        findViewById(R.id.history_toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // HistoryDbHelper.getInstance(HistoryActivity.this).deleteAllHistory(); // 先都清除一下，方便后续测试
                finish();
            }
        });
    }
}