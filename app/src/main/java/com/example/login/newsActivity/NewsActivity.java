package com.example.login.newsActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.MainActivity;
import com.example.login.R;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // Toolbar setup
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.news_toolbar);
        setSupportActionBar(toolbar);

        // Nút quay lại
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(NewsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // RecyclerView setup
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load default news
        fetchNews("finance");
    }

    private void fetchNews(String query) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://newsapi.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Khởi tạo NewsAPI
        NewsAPI newsAPI = retrofit.create(NewsAPI.class);

        // Gọi API lấy dữ liệu
        Call<NewsResponse> call = newsAPI.getNews(query, "e5a3545260d34caaa6aa6c4430892a52"); // Thay API key của em
        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Cập nhật dữ liệu vào RecyclerView
                    List<NewsResponse.Article> articles = response.body().articles;
                    if (articles.isEmpty()) {
                        Toast.makeText(NewsActivity.this, "No results found", Toast.LENGTH_SHORT).show();
                    } else {
                        newsAdapter = new NewsAdapter(articles);
                        recyclerView.setAdapter(newsAdapter);
                    }
                } else {
                    Toast.makeText(NewsActivity.this, "Failed to load news: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                Toast.makeText(NewsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_news, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("Search news...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.trim().isEmpty()) {
                    fetchNews(query); // Tìm kiếm tin tức theo query
                }
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false; // Không xử lý thay đổi văn bản
            }
        });

        return true;
    }
}