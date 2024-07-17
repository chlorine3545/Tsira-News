package com.java.liyao;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.Gson;
import com.java.liyao.db.HistoryDbHelper;

import org.w3c.dom.Text;

public class NewsDetailsActivity extends AppCompatActivity {

    private NewsInfo.DataDTO dataDTO;
    private Toolbar details_toolbar;
    private ViewPager2 details_image;
    private TextView ai_summary;
    private TextView details_content;
    private TextView details_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_news_details);

        // 每日初始化控件
        details_toolbar = findViewById(R.id.details_toolbar);
        details_image = findViewById(R.id.details_image);
        ai_summary = findViewById(R.id.ai_summary_card).findViewById(R.id.ai_summary);
        details_content = findViewById(R.id.details_content);
        details_time = findViewById(R.id.details_time);

        // 啊哈哈哈，数据来咯！
        dataDTO = (NewsInfo.DataDTO) getIntent().getSerializableExtra("dataDTO");

        assert dataDTO != null; // 日常判空
        details_toolbar.setTitle(dataDTO.getTitle());

        // 添加到历史记录
        String s = new Gson().toJson(dataDTO);
        HistoryDbHelper.getInstance(NewsDetailsActivity.this).addHistory(null, dataDTO.getUniqueID(), s);

        // AI 摘要的逻辑比较复杂，暂时不写
        // 图片的逻辑也比较复杂，暂时不写

        details_content.setText(dataDTO.getContent());
        details_time.setText(dataDTO.getPublishTime());

        details_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}