package com.java.liyao;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.java.liyao.db.CatPrefDbHelper;
import com.java.liyao.db.UserDbHelper;
import com.java.liyao.entity.UserInfo;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEtrEmail;
    private EditText loginEtrPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        loginEtrEmail = findViewById(R.id.loginEtrEmail);
        loginEtrPassword = findViewById(R.id.loginEtrPassword);

        findViewById(R.id.inviteToRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = loginEtrEmail.getText().toString();
                String password = loginEtrPassword.getText().toString();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "信息缺失！", Toast.LENGTH_SHORT).show();
                } else {
                    UserInfo userInfo = UserDbHelper.getInstance(LoginActivity.this).login(email);
                    if (null != userInfo && userInfo.getPassword().equals(password)) {
                        UserInfo.setUserinfo(userInfo);
                        // 这段代码一般没必要加，是给我创建的存量用户的
                        if (!CatPrefDbHelper.getInstance(LoginActivity.this).searchCatPref(userInfo.getUser_email())) {
                            List<String> allCats = Arrays.asList(new String[]{"全部", "娱乐", "军事", "教育", "文化", "健康", "财经", "体育", "汽车", "科技", "社会"});
                            CatPrefDbHelper.getInstance(LoginActivity.this).addCatPref(email, allCats.toString());
                        }
                        Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "登录失败！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

}