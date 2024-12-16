package com.example.login.newsActivity;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.login.R;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private final List<NewsResponse.Article> articles;

    public NewsAdapter(List<NewsResponse.Article> articles) {
        this.articles = articles;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsResponse.Article article = articles.get(position);

        // Hiển thị tiêu đề và mô tả bài báo
        holder.title.setText(article.title);
        holder.description.setText(article.description);

        // Hiển thị ảnh bài báo
        Glide.with(holder.itemView.getContext())
                .load(article.urlToImage)
                .into(holder.imageView);

        // Xử lý sự kiện click mở bài báo
        holder.itemView.setOnClickListener(v -> {
            if (article.url != null && !article.url.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(article.url)); // URL bài báo
                holder.itemView.getContext().startActivity(intent);
            } else {
                Toast.makeText(holder.itemView.getContext(), "URL not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageView imageView;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.news_title);
            description = itemView.findViewById(R.id.news_description);
            imageView = itemView.findViewById(R.id.news_image);
        }
    }
}