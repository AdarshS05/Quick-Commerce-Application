package com.example.quickcommerceapplication.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quickcommerceapplication.utils.Constants;
import com.example.quickcommerceapplication.R;
import com.example.quickcommerceapplication.utils.SecurityUtils;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvGoRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoRegister = findViewById(R.id.tvGoRegister);

        btnLogin.setOnClickListener(v ->{
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            String inputHash = SecurityUtils.hashPassword(password);

            SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME,MODE_PRIVATE);
            String savedUser = prefs.getString(Constants.KEY_USERNAME, "");
            String savedHash = prefs.getString(Constants.KEY_PASSWORD, "");

            if(username.equals(savedUser) && inputHash.equals(savedHash)){
                prefs.edit().putBoolean(Constants.KEY_LOGIN, true).apply();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });

        tvGoRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }
}
