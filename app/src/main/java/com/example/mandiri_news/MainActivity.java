package com.example.mandiri_news;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.Article;
import com.kwabenaberko.newsapilib.models.request.EverythingRequest;
import com.kwabenaberko.newsapilib.models.request.TopHeadlinesRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView recyclerTopView;
    List<Article> searchArticles = new ArrayList<>();
    List<Article> topArticles = new ArrayList<>();
    NewsRecyclerAdapter adapter;
    NewsRecyclerTopAdapter topAdapter;
    LinearProgressIndicator progressIndicator;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = findViewById(R.id.new_recycler_view);
        recyclerTopView = findViewById(R.id.new_recycler_top);
        progressIndicator = findViewById(R.id.progress_bar);
        searchView = findViewById(R.id.search_view);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getNews(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        setupRecyclerView();
        getNews("GENERAL");
        getTopHeadlines();
    }

    void setupRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsRecyclerAdapter(searchArticles);
        recyclerView.setAdapter(adapter);

        // Inisialisasi LayoutManager untuk RecyclerView top
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerTopView.setLayoutManager(layoutManager);
        topAdapter = new NewsRecyclerTopAdapter(topArticles);
        recyclerTopView.setAdapter(topAdapter);

        SearchView searchView;

    }

    void changeInProgress(boolean show){
        if(show)
            progressIndicator.setVisibility(View.VISIBLE);
        else
            progressIndicator.setVisibility(View.INVISIBLE);
    }

    void getNews(String query){
        changeInProgress(true);
        NewsApiClient newsApiClient = new NewsApiClient("b3bb2f0fb97d45ee925d8ddb1a71443e");
        newsApiClient.getEverything(
                new EverythingRequest.Builder()
                        .language("id")
                        .q("bank")
                        .q(query)
                        .build(),
                new NewsApiClient.ArticlesResponseCallback() {
                    @Override
                    public void onSuccess(ArticleResponse response) {

                        runOnUiThread(()->{
                            changeInProgress(false);
                            searchArticles.clear();
                            searchArticles.addAll(response.getArticles());
                            adapter.notifyDataSetChanged();
                        });
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.i("GOT Failure", throwable.getMessage());
                    }
                }
        );
    }

    void getTopHeadlines(){
        changeInProgress(true);
        NewsApiClient newsApiClient = new NewsApiClient("b3bb2f0fb97d45ee925d8ddb1a71443e");
        newsApiClient.getTopHeadlines(
                new TopHeadlinesRequest.Builder()
                        .language("en")
                        .build(),
                new NewsApiClient.ArticlesResponseCallback() {
                    @Override
                    public void onSuccess(ArticleResponse response) {
                        runOnUiThread(()->{
                            changeInProgress(false);
                            topArticles.clear();
                            topArticles.addAll(response.getArticles());
                            topAdapter.notifyDataSetChanged();
                        });
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.i("GOT Failure", throwable.getMessage());
                    }
                }
        );
    }
}
