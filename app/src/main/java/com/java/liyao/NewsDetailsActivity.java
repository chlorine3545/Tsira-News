package com.java.liyao;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.java.liyao.db.HistoryDbHelper;
import com.java.liyao.db.LikeDbHelper;
import com.java.liyao.entity.UserInfo;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NewsDetailsActivity extends AppCompatActivity {

    private NewsInfo.DataDTO dataDTO;
    private Toolbar details_toolbar;
    private ViewPager2 details_image;
    private TextView ai_summary;
    // private TextView details_content;
    private TextView details_time;
    private ImageButton like_btn;
    private WebView details_webView;

    void setAi_summary_DTO(String s) {
        runOnUiThread(() -> {
            ai_summary.setText(s);
            dataDTO.setAiSummary(s);
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_news_details);

        // 每日初始化控件
        details_toolbar = findViewById(R.id.details_toolbar);
        // details_image = findViewById(R.id.details_image);
        ai_summary = findViewById(R.id.ai_summary_card).findViewById(R.id.ai_summary);
        // details_content = findViewById(R.id.details_content);
        RelativeLayout rly = findViewById(R.id.details_btm_bar);
        like_btn = rly.findViewById(R.id.like_btn);
        details_time = rly.findViewById(R.id.details_time);
        details_webView = findViewById(R.id.details_webView);

        // 啊哈哈哈，数据来咯！
        dataDTO = (NewsInfo.DataDTO) getIntent().getSerializableExtra("dataDTO");
        details_webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        details_webView.getSettings().setUseWideViewPort(true);
        details_webView.getSettings().setLoadWithOverviewMode(true);
        WebSettings webSettings = details_webView.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);

        assert dataDTO != null; // 日常判空
        details_toolbar.setTitle(dataDTO.getTitle());
        // Log.d("WEBVIEW", "onCreate: 即将加载页面");
        details_webView.loadUrl(dataDTO.getUrl());

        // 添加到历史记录
        String s = new Gson().toJson(dataDTO);
        UserInfo userInfo = UserInfo.getUserinfo();
        String eml = userInfo == null ? null : userInfo.getUser_email();
        HistoryDbHelper.getInstance(NewsDetailsActivity.this).addHistory(eml, dataDTO.getUniqueID(), s);

        boolean isLiked = LikeDbHelper.getInstance(NewsDetailsActivity.this).searchLike(dataDTO.getUniqueID(), eml);
        if (isLiked) {
            like_btn.setImageResource(R.drawable.liked);
            like_btn.setTooltipText("取消收藏");
        } else {
            like_btn.setImageResource(R.drawable.like);
            like_btn.setTooltipText("收藏");
        }

        // AI 摘要的逻辑比较复杂
        ai_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 摘要的逻辑比较复杂，暂时不写
                if (Objects.equals(dataDTO.getAiSummary(), "")) {
                    ai_summary.setText("AI摘要生成中...");
                    // 这里需要调用接口生成摘要，先放这里搁着
                    // 开造！（慈禧音）
                    Log.d("AI_SUMMARY", dataDTO.getContent());
                    new Thread(() -> {
                        String aiSummary = AiSummary.aiSummaryInvoke(dataDTO.getContent());
                        Log.d("AI_SUMMARY_RESULT", aiSummary);
                        setAi_summary_DTO(aiSummary);
                    }).start();
                } else {
                    ai_summary.setText(dataDTO.getAiSummary());
                }
            }
        });

        // 图片的逻辑也比较复杂，暂时不写

        // details_content.setText(dataDTO.getContent());
        details_time.setText(dataDTO.getPublishTime());

        details_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 收藏按钮，心里有点没底 qaq
        // 你过关！（超大声）
        like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLiked) {
                    // 把心形变白
                    like_btn.setTooltipText("收藏");
                    like_btn.setImageResource(R.drawable.like);
                    LikeDbHelper.getInstance(NewsDetailsActivity.this).deleteLike(dataDTO.getUniqueID(), eml);
                    Toast.makeText(NewsDetailsActivity.this, "取消收藏成功！", Toast.LENGTH_SHORT).show();
                    Log.d("UnlikedSuccessfully", "onClick: 已经取消收藏");
                } else {
                    // 把心形变红
                    like_btn.setImageResource(R.drawable.liked);
                    like_btn.setTooltipText("取消收藏");
                    LikeDbHelper.getInstance(NewsDetailsActivity.this).addLike(eml, dataDTO.getUniqueID(), s);
                    Toast.makeText(NewsDetailsActivity.this, "收藏成功！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}