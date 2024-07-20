package com.java.liyao;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.java.liyao.db.CatPrefDbHelper;
import com.java.liyao.entity.UserInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EditCategoriesActivity extends AppCompatActivity {

    private Toolbar category_toolbar;
    private GridLayout category_main;
    private Button[] category_btns;
    private static final List<String> allCats = Arrays.asList(new String[]{"全部", "娱乐", "军事", "教育", "文化", "健康", "财经", "体育", "汽车", "科技", "社会"});

    public static void addAndSort(List<String> tmpCatList, String newCategory) {
        if (!tmpCatList.contains(newCategory)) {
            tmpCatList.add(newCategory);
            // 根据correctOrderList的顺序对tmpCatList进行排序
            Collections.sort(tmpCatList, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    int index1 = allCats.indexOf(o1);
                    int index2 = allCats.indexOf(o2);
                    return Integer.compare(index1, index2);
                }
            });
        }
    }

    public static void removeAndSort(List<String> tmpCatList, String newCategory) {
        if (tmpCatList.contains(newCategory)) {
            tmpCatList.remove(newCategory);
            // 根据correctOrderList的顺序对tmpCatList进行排序
            Collections.sort(tmpCatList, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    int index1 = allCats.indexOf(o1);
                    int index2 = allCats.indexOf(o2);
                    return Integer.compare(index1, index2);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        // 获取用户的分类偏好，来设置按钮的颜色。
        UserInfo userInfo = UserInfo.getUserinfo();
        String eml = userInfo != null ? userInfo.getUser_email() : "null";
        List<String> tmpCatList = CatPrefDbHelper.getInstance(this).getCatPrefList(eml);

        // Log.d("UserCatPreference", "onCreate: " + tmpCatList.toString());

        for (Button btn : category_btns) {
            String catName = btn.getText().toString();
            if (tmpCatList.contains(catName)) {
                btn.setTextColor(getResources().getColor(R.color.black));
            } else {
                btn.setTextColor(getResources().getColor(R.color.grey));
            }
        }

        // 绑定分类按钮点击事件
        for (Button btn : category_btns) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String catName = btn.getText().toString();
                    boolean isBlack = btn.getCurrentTextColor() == getResources().getColor(R.color.black);
                    // boolean isLoggedIn = userInfo != null;

                    if (isBlack) {
                        btn.setTextColor(getResources().getColor(R.color.grey));
                        removeAndSort(tmpCatList, catName);
                        CatPrefDbHelper.getInstance(EditCategoriesActivity.this).updateCatPref(eml, tmpCatList);

                    } else {
                        btn.setTextColor(getResources().getColor(R.color.black));
                        addAndSort(tmpCatList, catName);
                        CatPrefDbHelper.getInstance(EditCategoriesActivity.this).updateCatPref(eml, tmpCatList);
                    }
                    // Log.d("CurrentCatPref", "onClick: " + eml + tmpCatList.toString());
                }
            });
        }
    }
}