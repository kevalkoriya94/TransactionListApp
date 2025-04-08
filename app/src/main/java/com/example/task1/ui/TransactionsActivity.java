package com.example.task1.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.task1.R;
import com.example.task1.adapter.TransactionAdapter;
import com.example.task1.database.AppDatabase;
import com.example.task1.database.ThemeModeEntity;
import com.example.task1.model.Transaction;
import com.example.task1.network.ApiClient;
import com.example.task1.network.ApiService;
import com.example.task1.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText searchEditText;
    private TokenManager tokenManager;
    private ApiService apiService;
    private Button logoutBtn;
    private TransactionAdapter adapter;
    private List<Transaction> fullList = new ArrayList<>();
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app-db").build();

        recyclerView = findViewById(R.id.rvTransactions);
        searchEditText = findViewById(R.id.etSearch);
        logoutBtn = findViewById(R.id.btnLogout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tokenManager = new TokenManager(this);
        apiService = ApiClient.getRetrofit().create(ApiService.class);

        fetchTransactions();

        logoutBtn.setOnClickListener(v -> {
            tokenManager.clearToken();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (adapter != null) {
                    adapter.filter(s.toString());
                }
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });


        SwitchCompat switchTheme = findViewById(R.id.switchTheme);

        executor.execute(() -> {
            ThemeModeEntity mode = db.themeModeDao().getThemeMode();
            runOnUiThread(() -> {
                if (mode != null) switchTheme.setChecked(mode.isDarkMode);
            });
        });

        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            executor.execute(() -> {
                db.themeModeDao().setThemeMode(new ThemeModeEntity() {{
                    isDarkMode = isChecked;
                }});
            });

            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });
    }

    private void fetchTransactions() {
        String token = "Bearer " + tokenManager.getToken();
        apiService.getTransactions(token).enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fullList = response.body();
                    adapter = new TransactionAdapter(fullList);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                Toast.makeText(TransactionsActivity.this, "Failed to load", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
