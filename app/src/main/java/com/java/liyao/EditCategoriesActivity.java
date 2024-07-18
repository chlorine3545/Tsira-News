package com.java.liyao;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.java.liyao.entity.UserInfo;

public class EditCategoriesActivity extends AppCompatActivity {

    // 控件实在是太多了，就用一个列表好了。

    private Toolbar category_toolbar;
    private GridLayout category_main;
    private Button[] category_btns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_categories);

        // 初始化控件
        category_toolbar = findViewById(R.id.category_toolbar);
        category_main = findViewById(R.id.category_main);
        category_btns = new Button[category_main.getChildCount()];
        for (int i = 0; i < category_main.getChildCount(); i++) {
            category_btns[i] = (Button) category_main.getChildAt(i);
        }

        // 绑定工具栏的点击器
        category_toolbar.setNavigationOnClickListener(v -> finish());

        // 绑定分类按钮点击事件
        for (Button btn : category_btns) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 懒一点，直接更换字体颜色好了。
                    // 换成很浅的灰色
                    String catName = btn.getText().toString();
                    UserInfo userInfo = UserInfo.getUserinfo();
                    if (btn.getCurrentTextColor() == getResources().getColor(R.color.black)) {
                        if (userInfo == null) {
                            Toast.makeText(EditCategoriesActivity.this, "不能在未登录状态修改列表！请先登录", Toast.LENGTH_SHORT).show();
                        } else {
                            btn.setTextColor(getResources().getColor(R.color.grey));
                            userInfo.categories.remove(catName);
                        }
                    } else {
                        if (userInfo == null) {
                            Toast.makeText(EditCategoriesActivity.this, "不能在未登录状态修改列表！请先登录", Toast.LENGTH_SHORT).show();
                        } else {
                            btn.setTextColor(getResources().getColor(R.color.black));
                            userInfo.categories.add(catName);
                        }
                    }

                    // 此外还需要从列表中删除或添加。这个功能还需要分用户，好麻烦。
                }
            });
        }
    }
}