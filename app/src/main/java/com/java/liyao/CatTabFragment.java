package com.java.liyao;

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
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.java.liyao.adapter.NewsListAdapter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

    public CatTabFragment() {
        // 初始化 Handler，统一处理消息
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 200) {
                    String data = (String) msg.obj;
                    NewsInfo newsInfo = new Gson().fromJson(data, NewsInfo.class);
                    if (newsInfo != null && newsListAdapter != null) {
                        newsListAdapter.setListData(newsInfo.getData());
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
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        newsListAdapter = new NewsListAdapter(getActivity());
        newsList.setAdapter(newsListAdapter);
        if (getArguments() != null) {
            catT = getArguments().getString(ARG_PARAM);
        }
        try {
            fetcher();
        } catch (UnsupportedEncodingException e) {
            Log.e("EncodingError", "Unsupported Encoding Exception", e);
        }
    }

    private void fetcher() throws UnsupportedEncodingException {
        OkHttpClient okHttpClient = new OkHttpClient();
        // 回来收拾这个写死的 URL
        String baseUrl = "https://api2.newsminer.net/svc/news/queryNewsList?size=15&startDate=2024-07-05&endDate=2024-08-30&words=&page=1&categories=";
        String encodedCatT = URLEncoder.encode(catT, StandardCharsets.UTF_8.toString());
        Request request = new Request.Builder()
                .url(baseUrl + encodedCatT)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("NetworkError", "onFailure: " + e.toString());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String data = response.body().string();
                    // Log.d("Connected", data);
                    Message message = Message.obtain(); // 使用 obtain 方法获取 Message 实例。咱也不知道为什么，反正 Copilot 说这样写
                    message.what = 200;
                    message.obj = data;
                    mHandler.sendMessage(message);
                } else {
                    Log.d("NetworkError", "Response not successful or body is null");
                }
            }
        });
    }
}