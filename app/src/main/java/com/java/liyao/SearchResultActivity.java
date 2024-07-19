package com.java.liyao;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.java.liyao.adapter.NewsListAdapter;
import com.java.liyao.db.HistoryDbHelper;
import com.java.liyao.db.LikeDbHelper;
import com.java.liyao.entity.HistoryInfo;
import com.java.liyao.entity.UserInfo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchResultActivity extends AppCompatActivity {

    private Toolbar search_result_toolbar;
    private RecyclerView newsList;
    private NewsListAdapter newsListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler mHandler;
    private String keyword, cat, startDate, endDate;
    private int currentPage = 1;
    private boolean isLoading = false;
    private HashSet<String> alreadyViewed;
    private HashSet<String> alreadyLiked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Intent intent = getIntent();
        keyword = intent.getStringExtra("KEYWORD");
        cat = intent.getStringExtra("CATEGORY");
        startDate = intent.getStringExtra("START_DATE");
        endDate = intent.getStringExtra("END_DATE");

        Log.d("IntentArgPassed", "onCreate: " + keyword + " " + cat + " " + startDate + " " + endDate);

        search_result_toolbar = findViewById(R.id.search_result_toolbar);
        newsList = findViewById(R.id.newsList);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        initRecyclerView();
        initHandler();

        search_result_toolbar.setNavigationOnClickListener(v -> finish());

        swipeRefreshLayout.setOnRefreshListener(() -> {
            currentPage = 1;
            isLoading = true;
            try {
                fetchSearchResults();
            } catch (UnsupportedEncodingException e) {
                Log.e("EncodingError", "Unsupported Encoding Exception", e);
            }
            swipeRefreshLayout.setRefreshing(false);
        });

        try {
            fetchSearchResults();
        } catch (UnsupportedEncodingException e) {
            Log.e("EncodingError", "Unsupported Encoding Exception", e);
        }
    }

    private void initRecyclerView() {
        newsListAdapter = new NewsListAdapter(this);
        UserInfo userInfo = UserInfo.getUserinfo();
        String eml = userInfo == null ? null : userInfo.getUser_email();
        alreadyViewed = HistoryDbHelper.getInstance(this).getHistory(eml).stream().map(HistoryInfo::getUnique_id).collect(Collectors.toCollection(HashSet::new));
        alreadyLiked = LikeDbHelper.getInstance(this).getLike(eml).stream().map(HistoryInfo::getUnique_id).collect(Collectors.toCollection(HashSet::new));
        newsListAdapter.setAlreadyViewed(alreadyViewed);
        newsListAdapter.setAlreadyLiked(alreadyLiked);
        newsList.setAdapter(newsListAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        newsList.setLayoutManager(layoutManager);

        newsListAdapter.setOnItemClickListener(new NewsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NewsInfo.DataDTO dataDTO, int position) {
                Intent intent = new Intent(SearchResultActivity.this, NewsDetailsActivity.class);
                intent.putExtra("dataDTO", dataDTO);
                startActivity(intent);
            }
        });

        newsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0) {
                    loadMoreItems();
                }
            }
        });
    }

    private void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 200) {
                    String data = (String) msg.obj;
                    NewsInfo newsInfo = new Gson().fromJson(data, NewsInfo.class);
                    newsInfo.generateUniqueID();
                    if (newsListAdapter != null) {
                        if (currentPage == 1) {
                            newsListAdapter.setListData(newsInfo.getData());
                        } else {
                            newsListAdapter.addListData(newsInfo.getData());
                        }
                    } else {
                        Toast.makeText(SearchResultActivity.this, "获取数据失败！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
    }

    private void loadMoreItems() {
        isLoading = true;
        currentPage++;
        try {
            fetchSearchResults();
        } catch (UnsupportedEncodingException e) {
            Log.e("EncodingError", "Unsupported Encoding Exception", e);
        }
    }

    private void fetchSearchResults() throws UnsupportedEncodingException {
        OkHttpClient okHttpClient = new OkHttpClient();
        String baseUrl = "https://api2.newsminer.net/svc/news/queryNewsList?size=15";

        // 由于模拟器不能输入中文，先把关键词写死测试一下搜索功能能不能用
        String encodedKeyword = URLEncoder.encode("拜登", StandardCharsets.UTF_8.toString());
        String encodedCat = cat.equals("全部") ? "" : URLEncoder.encode(cat, StandardCharsets.UTF_8.toString());

        String url = baseUrl +
                "&startDate=" + startDate +
                "&endDate=" + endDate +
                "&words=" + encodedKeyword +
                "&categories=" + encodedCat +
                "&page=" + currentPage;

        Log.d("SearchingWeb", "fetchSearchResults: " + url);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("NetworkError", "onFailure: " + e.toString());
                isLoading = false;
                runOnUiThread(() -> Toast.makeText(SearchResultActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String data = response.body().string();
                    Message message = Message.obtain();
                    message.what = 200;
                    message.obj = data;
                    mHandler.sendMessage(message);
                } else {
                    Log.d("NetworkError", "Response not successful or body is null");
                    runOnUiThread(() -> Toast.makeText(SearchResultActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show());
                }
                isLoading = false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateViewedStatus();
    }

    private void updateViewedStatus() {
        UserInfo userInfo = UserInfo.getUserinfo();
        String eml = userInfo == null ? null : userInfo.getUser_email();
        alreadyViewed = HistoryDbHelper.getInstance(this).getHistory(eml).stream().map(HistoryInfo::getUnique_id).collect(Collectors.toCollection(HashSet::new));
        alreadyLiked = LikeDbHelper.getInstance(this).getLike(eml).stream().map(HistoryInfo::getUnique_id).collect(Collectors.toCollection(HashSet::new));
        newsListAdapter.setAlreadyViewed(alreadyViewed);
        newsListAdapter.setAlreadyLiked(alreadyLiked);
        newsListAdapter.notifyDataSetChanged();
    }
}