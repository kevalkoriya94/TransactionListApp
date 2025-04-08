package com.example.task1.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.room.Room;

import com.example.task1.R;
import com.example.task1.database.AppDatabase;
import com.example.task1.database.ThemeModeEntity;
import com.example.task1.model.LoginRequest;
import com.example.task1.model.LoginResponse;
import com.example.task1.network.ApiClient;
import com.example.task1.network.ApiService;
import com.example.task1.utils.TokenManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private ApiService apiService;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        apiService = ApiClient.getRetrofit().create(ApiService.class);
        tokenManager = new TokenManager(this);

        btnLogin.setOnClickListener(v -> {
            LoginRequest request = new LoginRequest(
                    etEmail.getText().toString(),
                    etPassword.getText().toString()
            );

            apiService.login(request).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        tokenManager.saveToken(response.body().getToken());
                        Toast.makeText(LoginActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, BiometricActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });


        ExecutorService executor = Executors.newSingleThreadExecutor();
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app-db").build();

        executor.execute(() -> {
            ThemeModeEntity mode = db.themeModeDao().getThemeMode();
            if (mode != null && mode.isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
    }
}
