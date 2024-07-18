package com.java.liyao;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.java.liyao.entity.UserInfo;

import java.util.Objects;

public class AccountActivity extends AppCompatActivity {

    private Toolbar account_toolbar;
    private TextView account_email_text;
    // private TextView account_password_text;
    private Button account_change_email;
    private Button account_change_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);

        account_toolbar = findViewById(R.id.account_toolbar);
        account_email_text = findViewById(R.id.account_email).findViewById(R.id.account_email_text);
        // account_password_text = findViewById(R.id.account_password).findViewById(R.id.account_password_text);
        account_change_email = findViewById(R.id.account_change_email);
        account_change_password = findViewById(R.id.account_change_password);

        UserInfo userInfo = UserInfo.getUserinfo();
        // 由于在主活动中已经判断过 userInfo 是否为空，所以这里不再判断。
        account_email_text.setText(userInfo.getUser_email());
        // account_password_text.setText(userInfo.getPassword());

        account_toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        account_change_email.setOnClickListener(v -> {
            // 跳转到修改邮箱的活动
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_change_email, null);
            builder.setView(dialogView);

            EditText changeEmailEtrEmail = dialogView.findViewById(R.id.changeEmailLlEmail).findViewById(R.id.changeEmailEtrEmail);
            EditText changeEmailEtrPassword = dialogView.findViewById(R.id.changeEmailLlPassword).findViewById(R.id.changeEmailEtrPassword);
            Button changeEmailBtnChange = dialogView.findViewById(R.id.changeEmailBtnChange);

            AlertDialog dialog = builder.create();

            changeEmailBtnChange.setOnClickListener(v1 -> {
                // 修改邮箱
                String email = changeEmailEtrEmail.getText().toString();
                String password = changeEmailEtrPassword.getText().toString();
                UserInfo userInfo1 = UserInfo.getUserinfo();
                if (Objects.equals(userInfo1.getPassword(), password)) {
                    userInfo1.setUser_email(email);
                    Toast.makeText(this, "邮箱修改成功！请重新登录", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    // 跳转到登录活动
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    // 修改失败
                    Toast.makeText(this, "密码错误！", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            });
        });

        account_change_password.setOnClickListener(v -> {
            // 跳转到修改密码的活动
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_change_password, null);
            builder.setView(dialogView);

            EditText changePasswordEtrOld = dialogView.findViewById(R.id.changePasswordLlOld).findViewById(R.id.changePasswordEtrOld);
            EditText changePasswordEtrNew = dialogView.findViewById(R.id.changePasswordLlNew).findViewById(R.id.changePasswordEtrNew);
            EditText changePasswordEtrNewAgain = dialogView.findViewById(R.id.changePasswordLlNewAgain).findViewById(R.id.changePasswordEtrNewAgain);
            Button changePasswordBtnChange = dialogView.findViewById(R.id.changePasswordBtnChange);

            AlertDialog dialog = builder.create();

            changePasswordBtnChange.setOnClickListener(v1 -> {
                // 修改密码
                String oldPassword = changePasswordEtrOld.getText().toString();
                String newPassword = changePasswordEtrNew.getText().toString();
                String newPasswordAgain = changePasswordEtrNewAgain.getText().toString();
                UserInfo userInfo1 = UserInfo.getUserinfo();
                if (Objects.equals(userInfo1.getPassword(), oldPassword)) {
                    if (Objects.equals(newPassword, newPasswordAgain)) {
                        userInfo1.setPassword(newPassword);
                        Toast.makeText(this, "密码修改成功！请重新登录", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        // 跳转到登录活动
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        // 修改失败
                        Toast.makeText(this, "两次输入的密码不一致！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 修改失败
                    Toast.makeText(this, "原密码错误！", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            });
        });
    }
}