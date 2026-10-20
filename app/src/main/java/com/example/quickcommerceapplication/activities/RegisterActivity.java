package com.example.quickcommerceapplication.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quickcommerceapplication.R;
import com.example.quickcommerceapplication.utils.Constants;
import com.example.quickcommerceapplication.utils.SecurityUtils;

public class RegisterActivity extends AppCompatActivity {
    private EditText etFullName, etEmail, etUsername, etPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsernameReg);
        etPassword = findViewById(R.id.etPasswordReg);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString();
            String email = etEmail.getText().toString();
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            String hashedPassword = SecurityUtils.hashPassword(password);

            if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()){
                Toast.makeText(this,"Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString(Constants.KEY_USERNAME,username);
            editor.putString(Constants.KEY_PASSWORD,hashedPassword);
            editor.putString("full_name",fullName);
            editor.putString("email",email);

            editor.apply();
            Toast.makeText(this,"Registration Successful",Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
