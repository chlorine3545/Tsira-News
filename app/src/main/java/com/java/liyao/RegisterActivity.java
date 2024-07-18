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

import java.util.Arrays;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerEtrNickname;
    private EditText registerEtrEmail;
    private EditText registerEtrPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        registerEtrNickname = findViewById(R.id.registerEtrNickname);
        registerEtrEmail = findViewById(R.id.registerEtrEmail);
        registerEtrPassword = findViewById(R.id.registerEtrPassword);

        findViewById(R.id.backToLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 血的教训：不要去跳转，不然会出现多个Activity
                finish();
            }
        });

        findViewById(R.id.registerBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nickname = registerEtrNickname.getText().toString();
                String email = registerEtrEmail.getText().toString();
                String password = registerEtrPassword.getText().toString();

                if (TextUtils.isEmpty(nickname) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "信息缺失！", Toast.LENGTH_SHORT).show();
                } else {
                    long ret = UserDbHelper.getInstance(RegisterActivity.this).register(nickname, email, password);
                    if (ret > 0) {
                        List<String> allCats = Arrays.asList(new String[]{"全部", "娱乐", "军事", "教育", "文化", "健康", "财经", "体育", "汽车", "科技", "社会"});
                        CatPrefDbHelper.getInstance(RegisterActivity.this).addCatPref(email, allCats.toString());
                        Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });
    }
}