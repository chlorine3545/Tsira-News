package com.java.liyao;

import android.content.Intent;
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
                    } else {
                        Toast.makeText(getActivity(), "获取数据失败！", Toast.LENGTH_SHORT).show();
                    }
                }
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
        newsListAdapter = new NewsListAdapter(getActivity());
        UserInfo userInfo = UserInfo.getUserinfo();
        String eml = userInfo == null ? null : userInfo.getUser_email();
        alreadyViewed = HistoryDbHelper.getInstance(getActivity()).getHistory(eml).stream().map(HistoryInfo::getUnique_id).collect(Collectors.toCollection(HashSet::new));
        alreadyLiked = LikeDbHelper.getInstance(getActivity()).getLike(eml).stream().map(HistoryInfo::getUnique_id).collect(Collectors.toCollection(HashSet::new));
        newsListAdapter.setAlreadyViewed(alreadyViewed);
        newsListAdapter.setAlreadyLiked(alreadyLiked);
        newsList.setAdapter(newsListAdapter);

        newsListAdapter.setOnItemClickListener((new NewsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NewsInfo.DataDTO dataDTO, int position) {
                // Log.d("Entered", "onItemClick: 已进入点击事件");
                // 还是那句话，凡是涉及到 Activity 的跳转，都需要 Intent
                Intent intent = new Intent(getActivity(), NewsDetailsActivity.class);
                intent.putExtra("dataDTO", dataDTO);
                startActivity(intent);
                // Log.d("JUMPED", "onItemClick: 成功开启活动：" + dataDTO.getTitle());
            }
        }));

        // 下滑刷新事件
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1; // 为什么要重置 currentPage？
                try {
                    fetcher();
                } catch (UnsupportedEncodingException e) {
                    Log.e("EncodingError", "Unsupported Encoding Exception", e);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        if (getArguments() != null) {
            catT = getArguments().getString(ARG_PARAM);
        }
        try {
            fetcher();
        } catch (UnsupportedEncodingException e) {
            Log.e("EncodingError", "Unsupported Encoding Exception", e);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        newsList.setLayoutManager(layoutManager);

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

    private void loadMoreItems() {
        isLoading = true;
        currentPage++;
        try {
            fetcher();
        } catch (UnsupportedEncodingException e) {
            Log.e("EncodingError", "Unsupported Encoding Exception", e);
        }
    }

    private void fetcher() throws UnsupportedEncodingException {
        OkHttpClient okHttpClient = new OkHttpClient();
        String baseUrl = "https://api2.newsminer.net/svc/news/queryNewsList?size=15&startDate=2023-01-01&endDate=2024-08-30&words=&categories=";
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
                isLoading = false;
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
                }
                isLoading = false;
            }
        });
    }

    // 还需要一个关键的功能：对于浏览过的页面卡片，显示为灰色
}