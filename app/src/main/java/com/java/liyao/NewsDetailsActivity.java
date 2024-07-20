package com.java.liyao;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.Gson;
import com.java.liyao.db.AiSummaryDbHelper;
import com.java.liyao.db.HistoryDbHelper;
import com.java.liyao.db.LikeDbHelper;
import com.java.liyao.NewsInfo;
import com.java.liyao.entity.UserInfo;

import java.util.List;

public class NewsDetailsActivity extends AppCompatActivity {

    private NewsInfo.DataDTO dataDTO;
    private Toolbar details_toolbar;
    private ViewPager2 details_image;
    private TextView ai_summary;
    private TextView details_content;
    private TextView details_time;
    private ImageButton like_btn;
    private ImagePagerAdapter imagePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        // 每日初始化控件
        details_toolbar = findViewById(R.id.details_toolbar);
        details_image = findViewById(R.id.details_image);
        ai_summary = findViewById(R.id.ai_summary_card).findViewById(R.id.ai_summary);
        details_content = findViewById(R.id.details_content);
        RelativeLayout rly = findViewById(R.id.details_btm_bar);
        like_btn = rly.findViewById(R.id.like_btn);
        details_time = rly.findViewById(R.id.details_time);

        // 啊哈哈哈，数据来咯！
        dataDTO = (NewsInfo.DataDTO) getIntent().getSerializableExtra("dataDTO");
        assert dataDTO != null; // 日常判空
        details_toolbar.setTitle(dataDTO.getTitle());

        details_content.setText(dataDTO.getContent());

        // 初始化ViewPager2
        List<String> imageUrls = dataDTO.getImage();
        imagePagerAdapter = new ImagePagerAdapter(this, imageUrls);
        details_image.setAdapter(imagePagerAdapter);
        details_image.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

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

        // AI 摘要的逻辑
        ai_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uk = dataDTO.getUniqueID();
                boolean isSummarized = AiSummaryDbHelper.getInstance(NewsDetailsActivity.this).searchSummary(uk);
                if (!isSummarized) {
                    ai_summary.setText("AI摘要生成中...");
                    Log.d("AI_SUMMARY", dataDTO.getContent());
                    new Thread(() -> {
                        String aiSummary = AiSummary.aiSummaryInvoke(dataDTO.getContent());
                        Log.d("AI_SUMMARY_RESULT", aiSummary);
                        setAi_summary_DTO(aiSummary);
                    }).start();
                } else {
                    ai_summary.setText(AiSummaryDbHelper.getInstance(NewsDetailsActivity.this).getSummary(uk).getAiSummary());
                }
            }
        });

        details_time.setText(dataDTO.getPublishTime());

        details_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLiked) {
                    like_btn.setTooltipText("收藏");
                    like_btn.setImageResource(R.drawable.like);
                    LikeDbHelper.getInstance(NewsDetailsActivity.this).deleteLike(dataDTO.getUniqueID(), eml);
                    Toast.makeText(NewsDetailsActivity.this, "取消收藏成功！", Toast.LENGTH_SHORT).show();
                    Log.d("UnlikedSuccessfully", "onClick: 已经取消收藏");
                } else {
                    like_btn.setImageResource(R.drawable.liked);
                    like_btn.setTooltipText("取消收藏");
                    LikeDbHelper.getInstance(NewsDetailsActivity.this).addLike(eml, dataDTO.getUniqueID(), s);
                    Toast.makeText(NewsDetailsActivity.this, "收藏成功！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setAi_summary_DTO(String s) {
        runOnUiThread(() -> {
            ai_summary.setText(s);
            dataDTO.setAiSummary(s);
            AiSummaryDbHelper.getInstance(NewsDetailsActivity.this).addSummary(dataDTO.getUniqueID(), s);
        });
    }
}