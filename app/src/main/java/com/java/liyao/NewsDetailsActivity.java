package com.java.liyao;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.Gson;
import com.java.liyao.adapter.ImagePagerAdapter;
import com.java.liyao.db.AiSummaryDbHelper;
import com.java.liyao.db.HistoryDbHelper;
import com.java.liyao.db.LikeDbHelper;
import com.java.liyao.entity.UserInfo;

import java.util.List;
import java.util.Locale;

public class NewsDetailsActivity extends AppCompatActivity {
    private NewsInfo.DataDTO dataDTO;
    private Toolbar details_toolbar;
    private ViewPager2 details_image;
    // private TextView ai_summary;
    private TextView details_content;
    private ImageButton like_btn;
    private ImagePagerAdapter imagePagerAdapter;
    private TextView image_indicator;
    private TextView card_date;
    private TextView card_source;
    private TextView ai_summary;
    private boolean isLiked;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        // 初始化控件
        details_toolbar = findViewById(R.id.details_toolbar);
        details_image = findViewById(R.id.details_image);
        ai_summary = findViewById(R.id.ai_summary);
        details_content = findViewById(R.id.details_content);
        RelativeLayout rly = findViewById(R.id.details_btm_bar);
        like_btn = rly.findViewById(R.id.like_btn);
        image_indicator = findViewById(R.id.image_indicator);
        card_date = findViewById(R.id.card_date);
        card_source = findViewById(R.id.card_source);

        // 获取数据
        dataDTO = (NewsInfo.DataDTO) getIntent().getSerializableExtra("dataDTO");
        assert dataDTO != null; // 判空
        details_toolbar.setTitle(dataDTO.getTitle());
        card_date.setText("时间：" + dataDTO.getPublishTime());
        card_source.setText("来源：" + dataDTO.getPublisher());
        details_content.setText(dataDTO.getContent());

        // 初始化ViewPager2
        String videoUrl = null;
        List<String> imageUrls = dataDTO.getImage();
        if (dataDTO.getVideo() != null && !dataDTO.getVideo().isEmpty()) {
            videoUrl = dataDTO.getVideo();
        }

        Log.d("ImageLoader", "onCreate: " + dataDTO.getTitle() + imageUrls.toString());
        if ((imageUrls != null && !imageUrls.isEmpty()) || (videoUrl != null && !videoUrl.isEmpty())) {
            imagePagerAdapter = new ImagePagerAdapter(this, imageUrls, videoUrl);
            details_image.setAdapter(imagePagerAdapter);
            details_image.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

            // 设置指示器
            updateIndicator(1, imagePagerAdapter.getItemCount());

            // 注册页面变化回调
            details_image.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    updateIndicator(position + 1, imagePagerAdapter.getItemCount());
                }
            });

            image_indicator.setVisibility(View.VISIBLE);
        } else if (videoUrl != null && !videoUrl.isEmpty()) {
            // 如果有视频，可以隐藏ViewPager2
            details_image.setVisibility(View.GONE);
            image_indicator.setVisibility(View.GONE);
        } else {
            // 如果没有图片，可以隐藏ViewPager2
            details_image.setVisibility(View.GONE);
            image_indicator.setVisibility(View.GONE);
        }

        // 添加到历史记录
        String s = new Gson().toJson(dataDTO);
        UserInfo userInfo = UserInfo.getUserinfo();
        String eml = userInfo == null ? null : userInfo.getUser_email();
        HistoryDbHelper.getInstance(NewsDetailsActivity.this).addHistory(eml, dataDTO.getUniqueID(), s);

        isLiked = LikeDbHelper.getInstance(NewsDetailsActivity.this).searchLike(dataDTO.getUniqueID(), eml);
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
                    ai_summary.setText("摘要：AI摘要生成中...");
                    new Thread(() -> {
                        String aiSummary = AiSummary.aiSummaryInvoke(dataDTO.getContent());
                        setAi_summary_DTO(aiSummary);
                    }).start();
                } else {
                    ai_summary.setText("摘要：" + AiSummaryDbHelper.getInstance(NewsDetailsActivity.this).getSummary(uk).getAiSummary());
                }
            }
        });

        details_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 执行收藏或取消收藏操作
                if (isLiked) {
                    LikeDbHelper.getInstance(NewsDetailsActivity.this).deleteLike(dataDTO.getUniqueID(), eml);
                    Toast.makeText(NewsDetailsActivity.this, "取消收藏成功！", Toast.LENGTH_SHORT).show();
                } else {
                    LikeDbHelper.getInstance(NewsDetailsActivity.this).addLike(eml, dataDTO.getUniqueID(), s);
                    Toast.makeText(NewsDetailsActivity.this, "收藏成功！", Toast.LENGTH_SHORT).show();
                }

                // 重新查询数据库以更新isLiked状态
                isLiked = LikeDbHelper.getInstance(NewsDetailsActivity.this).searchLike(dataDTO.getUniqueID(), eml);

                // 根据新的isLiked状态更新按钮图标和提示文本
                if (isLiked) {
                    like_btn.setImageResource(R.drawable.liked);
                    like_btn.setTooltipText("取消收藏");
                } else {
                    like_btn.setImageResource(R.drawable.like);
                    like_btn.setTooltipText("收藏");
                }
            }
        });
    }

    private void setAi_summary_DTO(String s) {
        runOnUiThread(() -> {
            ai_summary.setText("摘要：" + s);
            dataDTO.setAiSummary(s);
            AiSummaryDbHelper.getInstance(NewsDetailsActivity.this).addSummary(dataDTO.getUniqueID(), s);
        });
    }

    private void updateIndicator(int current, int total) {
        image_indicator.setText(String.format(Locale.getDefault(), "%d/%d", current, total));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AiSummaryDbHelper.getInstance(NewsDetailsActivity.this).close();
        LikeDbHelper.getInstance(NewsDetailsActivity.this).close();
        HistoryDbHelper.getInstance(NewsDetailsActivity.this).close();
    }
}
