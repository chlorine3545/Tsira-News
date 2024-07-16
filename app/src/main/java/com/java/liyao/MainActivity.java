package com.java.liyao;

import com.google.android.material.tabs.TabLayout;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
    private String[] cats = {"娱乐", "军事", "教育", "文化", "健康", "财经", "体育", "汽车", "科技", "社会"};
    private TabLayout catTab;

    private ViewPager2 viewPg2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // 初始化控件
        viewPg2 = findViewById(R.id.viewPg2);
        catTab = findViewById(R.id.catTab);
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

        //这几话不能少
        tabLayoutMediator.attach();
    }
}