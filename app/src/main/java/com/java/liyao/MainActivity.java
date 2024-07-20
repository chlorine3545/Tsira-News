package com.java.liyao;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.java.liyao.db.CatPrefDbHelper;
import com.java.liyao.entity.CatPrefInfo;
import com.java.liyao.entity.UserInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final List<String> allCats = Arrays.asList(new String[]{"全部", "娱乐", "军事", "教育", "文化", "健康", "财经", "体育", "汽车", "科技", "社会"});
    private static final Logger log = LoggerFactory.getLogger(MainActivity.class);
    UserInfo userInfo = UserInfo.getUserinfo();
    private List<String> cats = allCats;
    // 得先获取用户的分类，如果没有登录，就用默认的分类。
    // 这个列表不能是 Final 的，因为要编辑。
    // 循序渐进。先把 cats 过渡为 List

    private TabLayout catTab;
    private ViewPager2 viewPg2;

    private Toolbar main_toolbar;
    private TextView search;
    private NavigationView nav_view;
    private TextView tv_nickname;
    private TextView tv_user_email;
    private ImageButton search_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // CatPrefDbHelper.getInstance(this).resetAllUserPreferences(allCats);

        if (userInfo != null) {
            cats = CatPrefDbHelper.getInstance(this).getCatPrefList(userInfo.getUser_email());
        }
        else {
            cats = CatPrefDbHelper.getInstance(this).getCatPrefList("null"); // 未登录
            // 先用 "null" 作为未登录的邮箱，可以避免直接使用 null 带来的亿点啸问题
            // Log.d("未登录的偏好情况", "onCreate: " + cats.toString());
        }

        // 初始化控件
        viewPg2 = findViewById(R.id.viewPg2);
        catTab = findViewById(R.id.catTab);
        nav_view = findViewById(R.id.nav_view);
        main_toolbar = findViewById(R.id.main_toolbar);
        // 主打一个严谨
        search = main_toolbar.findViewById(R.id.search_layout).findViewById(R.id.search_box);
        View headerView = nav_view.getHeaderView(0);
        tv_nickname = headerView.findViewById(R.id.tv_nickname);
        tv_user_email = headerView.findViewById(R.id.tv_user_email);
        search_icon = main_toolbar.findViewById(R.id.search_icon);

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.nav_history) {
                    Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.nav_like) {
                    Intent intent = new Intent(MainActivity.this, LikeActivity.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.nav_category) {
                    Intent intent = new Intent(MainActivity.this, EditCategoriesActivity.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.nav_logout) {
                    userInfo.logOut();
                    Toast.makeText(MainActivity.this, "已退出登录！", Toast.LENGTH_SHORT).show();
                    onResume();
                } else if (item.getItemId() == R.id.nav_account) {
                    Intent intent;
                    if (userInfo == null) {
                        intent = new Intent(MainActivity.this, LoginActivity.class);
                    } else {
                        intent = new Intent(MainActivity.this, AccountActivity.class);
                    }
                    startActivity(intent);
                } else if (item.getItemId() == R.id.nav_about) {
                    Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                    startActivity(intent);
                }
                // 剩下的一会再写
                return true;
            }
        });

        main_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.open();
            }
        });

        // 设置adapter
        viewPg2.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                //创建 NewsTabFragment页面
                String title = cats.get(position);
                CatTabFragment ctf = CatTabFragment.newInstance(title);
                return ctf;
            }

            @Override
            public int getItemCount() {
                return cats.size();
            }
        });

        // tab_layout点击事件
        catTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 设置viewPager选中当前页
                viewPg2.setCurrentItem(tab.getPosition(), true);
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
                tab.setText(cats.get(position));
            }
        });

        tabLayoutMediator.attach();

        // 搜索框输入事件。要不还是不做搜索框，直接点击跳转到搜索页面吧。
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        
        // 搞个怪
        search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "前面的区域，以后再来探索吧！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        userInfo = UserInfo.getUserinfo();
        if (userInfo != null) {
            tv_nickname.setText(userInfo.getNickname());
            tv_user_email.setText(userInfo.getUser_email());
            tv_user_email.setVisibility(View.VISIBLE);
            cats = CatPrefDbHelper.getInstance(this).getCatPrefList(userInfo.getUser_email());
            // CatPrefDbHelper.getInstance(this).updateCatPref("chlorine@kawaii,com", allCats);
            viewPg2.getAdapter().notifyDataSetChanged();
            new TabLayoutMediator(catTab, viewPg2, new TabLayoutMediator.TabConfigurationStrategy() {
                @Override
                public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                    tab.setText(cats.get(position));
                }
            }).attach();
        } else {
            tv_nickname.setText("未登录");
            tv_user_email.setVisibility(View.GONE);
            cats = CatPrefDbHelper.getInstance(this).getCatPrefList("null");
            viewPg2.getAdapter().notifyDataSetChanged();
            new TabLayoutMediator(catTab, viewPg2, new TabLayoutMediator.TabConfigurationStrategy() {
                @Override
                public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                    tab.setText(cats.get(position));
                }
            }).attach();
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