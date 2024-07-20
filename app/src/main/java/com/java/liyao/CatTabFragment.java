package com.java.liyao;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import java.util.Objects;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CatTabFragment extends Fragment {
    private static final String ARG_PARAM = "catTitle";
    private String catT;
    private Handler mHandler;
    private View rootView;
    private RecyclerView newsList;
    private NewsListAdapter newsListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private HashSet<String> alreadyViewed;
    private HashSet<String> alreadyLiked;

    private boolean isLoading = false;
    private int currentPage = 1;

    private static final String CACHE_PREFIX = "news_cache_";
    private static final long CACHE_EXPIRATION = 5 * 60 * 1000; // 5 minutes

    private void saveCache(String data) {
        SharedPreferences prefs = requireActivity().getSharedPreferences(CACHE_PREFIX + catT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("data", data);
        editor.putLong("timestamp", System.currentTimeMillis());
        editor.apply();
    }

    private String getCache() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(CACHE_PREFIX + catT, Context.MODE_PRIVATE);
        long timestamp = prefs.getLong("timestamp", 0);
        if (System.currentTimeMillis() - timestamp > CACHE_EXPIRATION) {
            return null; // Cache expired
        }
        return prefs.getString("data", null);
    }

    private void clearCache() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(CACHE_PREFIX + catT, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    public CatTabFragment() {
        // 初始化 Handler，统一处理消息
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
                        newsListAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), "获取数据失败！", Toast.LENGTH_SHORT).show();
                    }
                }
                isLoading = false;
            }
        };
    }


    public static CatTabFragment newInstance(String param) {
        CatTabFragment fragment = new CatTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cat_tab, container, false);
        newsList = rootView.findViewById(R.id.newsList);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 初始化 NewsListAdapter
        newsListAdapter = new NewsListAdapter(getActivity());

        // 获取用户信息
        UserInfo userInfo = UserInfo.getUserinfo();
        String eml = userInfo == null ? null : userInfo.getUser_email();

        // 获取已浏览和已喜欢的新闻 ID
        alreadyViewed = HistoryDbHelper.getInstance(getActivity()).getHistory(eml).stream()
                .map(HistoryInfo::getUnique_id)
                .collect(Collectors.toCollection(HashSet::new));
        alreadyLiked = LikeDbHelper.getInstance(getActivity()).getLike(eml).stream()
                .map(HistoryInfo::getUnique_id)
                .collect(Collectors.toCollection(HashSet::new));

        // 设置已浏览和已喜欢的新闻到 adapter
        newsListAdapter.setAlreadyViewed(alreadyViewed);
        newsListAdapter.setAlreadyLiked(alreadyLiked);

        // 设置 adapter 到 RecyclerView
        newsList.setAdapter(newsListAdapter);

        // 设置点击事件监听器
        newsListAdapter.setOnItemClickListener(new NewsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NewsInfo.DataDTO dataDTO, int position) {
                Intent intent = new Intent(getActivity(), NewsDetailsActivity.class);
                intent.putExtra("dataDTO", dataDTO);
                startActivity(intent);
            }
        });

        // 设置下拉刷新监听器
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                newsListAdapter.clearData();
                clearCache(); // 清除缓存
                try {
                    fetcher();
                } catch (UnsupportedEncodingException e) {
                    Log.e("EncodingError", "Unsupported Encoding Exception", e);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // 获取分类标题
        if (getArguments() != null) {
            catT = getArguments().getString(ARG_PARAM);
        }

        // 尝试从缓存加载数据
        String cachedData = getCache();
        if (cachedData != null) {
            processData(cachedData);
        } else {
            // 如果没有缓存，则从网络获取数据
            try {
                fetcher();
            } catch (UnsupportedEncodingException e) {
                Log.e("EncodingError", "Unsupported Encoding Exception", e);
            }
        }

        // 设置 LayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        newsList.setLayoutManager(layoutManager);

        // 添加滚动监听器以实现加载更多
        newsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5
                        && firstVisibleItemPosition >= 0 && dy > 0) {
                    loadMoreItems();
                }
            }
        });
    }

    private void loadMoreItems() {
        if (isLoading) return;
        isLoading = true;
        currentPage++;
        try {
            fetcher();
        } catch (UnsupportedEncodingException e) {
            Log.e("EncodingError", "Unsupported Encoding Exception", e);
            isLoading = false;
        }
    }

    private void fetcher() throws UnsupportedEncodingException {

        String cachedData = getCache();
        if (cachedData != null && currentPage == 1) {
            processData(cachedData);
            return;
        }

        OkHttpClient okHttpClient = OkHttpSingleton.getInstance(); // Use the singleton instance
        // 《新闻》（指四年前）
        String baseUrl = "https://api2.newsminer.net/svc/news/queryNewsList?size=15&startDate=2020-07-01&endDate=2024-08-30&words=&categories=";
        String encodedCatT = Objects.equals(catT, "全部") ? "" : URLEncoder.encode(catT, StandardCharsets.UTF_8.toString());
        String url = baseUrl + encodedCatT + "&page=" + currentPage;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("NetworkError", "onFailure: " + e.toString());
                mHandler.post(() -> {
                    isLoading = false;
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String data = response.body().string();
                    NewsInfo newsInfo = new Gson().fromJson(data, NewsInfo.class);
                    if (newsInfo.getData().isEmpty()) {
                        mHandler.post(() -> Toast.makeText(getActivity(), "我们可能需要到更加古老的年代爬取新闻……", Toast.LENGTH_LONG).show());
                    } else {
                        mHandler.post(() -> processData(data));
                    }
                } else {
                    Log.d("NetworkError", "Response not successful or body is null");
                    mHandler.post(() -> isLoading = false);
                }
            }
        });
    }

    // 还需要一个关键的功能：对于浏览过的页面卡片，显示为灰色
    // 好的，解决了。下面做分类标签列表。
    // 这个也写完啦

    private void processData(String data) {
        NewsInfo newsInfo = new Gson().fromJson(data, NewsInfo.class);
        newsInfo.generateUniqueID();
        if (newsListAdapter != null) {
            if (currentPage == 1) {
                newsListAdapter.setListData(newsInfo.getData());
            } else {
                newsListAdapter.addListData(newsInfo.getData());
            }
            newsListAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getActivity(), "获取数据失败！", Toast.LENGTH_SHORT).show();
        }
        isLoading = false;

        if (currentPage == 1) {
            saveCache(data);
        }
    }
}