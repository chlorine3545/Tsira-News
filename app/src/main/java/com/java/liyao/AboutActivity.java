package com.java.liyao;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.noties.markwon.Markwon;
// 导入删除线的扩展
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;

public class AboutActivity extends AppCompatActivity {
    private TextView about_text;
    private Toolbar about_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);
        about_text = findViewById(R.id.about_text);
        about_toolbar = findViewById(R.id.about_toolbar);

        about_toolbar.setNavigationOnClickListener(v -> finish());

        Markwon markwon = Markwon.builder(this)
                .usePlugin(new StrikethroughPlugin()) // 使用扩展
                .build();

        // 从assets目录下的Markdown文件
        String markdown = readMarkdownFromAssets();
        markwon.setMarkdown(about_text, markdown);
    }

    private String readMarkdownFromAssets() {
        StringBuilder content = new StringBuilder();
        try {
            InputStream is = getAssets().open("about.md");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}