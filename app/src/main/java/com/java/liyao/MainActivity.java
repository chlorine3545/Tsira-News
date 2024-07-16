package com.java.liyao;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.java.liyao.entity.UserInfo;

public class MainActivity extends AppCompatActivity {
    private String[] cats = {"娱乐", "军事", "教育", "文化", "健康", "财经", "体育", "汽车", "科技", "社会"};

    private TabLayout catTab;
    private ViewPager2 viewPg2;

    private NavigationView nav_view;
    private TextView tv_nickname;
    private TextView tv_user_email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化控件
        viewPg2 = findViewById(R.id.viewPg2);
        catTab = findViewById(R.id.catTab);
        nav_view = findViewById(R.id.nav_view);
        View headerView = nav_view.getHeaderView(0);
        tv_nickname = headerView.findViewById(R.id.tv_nickname);
        tv_user_email = headerView.findViewById(R.id.tv_user_email);

        // 设置adapter
        viewPg2.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                //创建 NewsTabFragment页面
                String title = cats[position];
                CatTabFragment ctf = CatTabFragment.newInstance(title);
                return ctf;
            }

            @Override
            public int getItemCount() {
                return cats.length;
            }
        });

        // tab_layout点击事件
        catTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 设置viewPager选中当前页
                viewPg2.setCurrentItem(tab.getPosition(),false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // viewPager和tab_layout关联在一起
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(catTab, viewPg2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(cats[position]);
            }
        });

        tabLayoutMediator.attach();
    }

    @Override
    protected void onResume() {
        super.onResume();

        UserInfo userInfo = UserInfo.getUserinfo();
        if (userInfo != null) {
            tv_nickname.setText(userInfo.getNickname());
            tv_user_email.setText(userInfo.getUser_email());
            tv_user_email.setVisibility(View.VISIBLE);
        }
        else {
            tv_nickname.setText("未登录");
            tv_user_email.setVisibility(View.GONE);
            tv_nickname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
}