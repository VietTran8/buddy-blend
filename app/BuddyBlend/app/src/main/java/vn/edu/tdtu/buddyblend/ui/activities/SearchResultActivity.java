package vn.edu.tdtu.buddyblend.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayoutMediator;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.adapters.search.SearchResultAdapter;
import vn.edu.tdtu.buddyblend.business.SearchBusiness;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.databinding.ActivitySearchResultBinding;
import vn.edu.tdtu.buddyblend.dto.response.SearchResponse;
import vn.edu.tdtu.buddyblend.utils.ScreenManager;
import vn.edu.tdtu.buddyblend.utils.SharedPreferenceKeys;

public class SearchResultActivity extends AppCompatActivity {
    private SearchBusiness searchService;
    private ActivitySearchResultBinding binding;
    private SearchResultAdapter searchResultAdapter;
    private String key;
    private SearchResponse searchResponse;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences(SharedPreferenceKeys.USER_SHARED_PREFS, MODE_PRIVATE);
        searchService = SearchBusiness.getInstance();
        ScreenManager.enableFullScreen(this.getWindow());

        init();
    }

    private void init(){
        this.key = getIntent().getStringExtra("key");
        binding.edtSearch.setText(key);

        searchService.doSearch(sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""), key, new ActionCallback<SearchResponse>() {
            @Override
            public void onSuccess(SearchResponse searchResponse) {
                SearchResultActivity.this.searchResponse = searchResponse;
                renderUI();
            }

            @Override
            public void onFailure(String message) {
                ActionCallback.super.onFailure(message);
            }
        });

        initEvents();
    }

    private void initEvents() {
        binding.edtSearch.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.btnBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void renderUI() {
        searchResultAdapter = new SearchResultAdapter(this, this.searchResponse);
        binding.vpResults.setAdapter(searchResultAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.vpResults, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Tất cả");
                    break;
                case 1:
                    tab.setText("Mọi người");
                    break;
                case 2:
                    tab.setText("Bài viết");
                    break;
            }
        }).attach();
    }
}