package vn.edu.tdtu.buddyblend.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.adapters.search.SearchHistoryAdapter;
import vn.edu.tdtu.buddyblend.adapters.search.SearchUserAdapter;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.databinding.ActivitySearchingBinding;
import vn.edu.tdtu.buddyblend.dto.response.SearchHistory;
import vn.edu.tdtu.buddyblend.dto.response.SearchResponse;
import vn.edu.tdtu.buddyblend.ui.viewmodel.SearchViewModel;
import vn.edu.tdtu.buddyblend.utils.DebounceUtil;
import vn.edu.tdtu.buddyblend.utils.KeyBoardUtils;
import vn.edu.tdtu.buddyblend.utils.LayoutManagerUtil;
import vn.edu.tdtu.buddyblend.utils.ScreenManager;
import vn.edu.tdtu.buddyblend.utils.SharedPreferenceKeys;

public class SearchingActivity extends AppCompatActivity {
    private ActivitySearchingBinding binding;
    private SharedPreferences sharedPreferences;
    private SearchViewModel searchViewModel;
    private SearchHistoryAdapter searchHistoryAdapter;
    private SearchUserAdapter searchUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ScreenManager.enableFullScreen(this.getWindow());
        sharedPreferences = getSharedPreferences(SharedPreferenceKeys.USER_SHARED_PREFS, MODE_PRIVATE);
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        init();
    }

    private void init() {
        initObjects();
        initEvents();

        displaySearchHistory();
    }

    private void initObjects() {
        searchHistoryAdapter = new SearchHistoryAdapter(this);
        searchHistoryAdapter.setOnHistoryClickLister(((position, history) -> {
            String key = history.getQuery();

            if(!key.isEmpty()) {
                Intent intent = new Intent(SearchingActivity.this, SearchResultActivity.class);
                intent.putExtra("key", key);

                startActivity(intent);
            }
        }));

        searchHistoryAdapter.setOnDeleteHistoryClickListener(((position, history) -> {
            searchViewModel.deleteSearchHistory(
                    sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""),
                    history.getId(),
                    new ActionCallback<Object>() {
                        @Override
                        public void onSuccess() {
                            Snackbar.make(binding.getRoot(), "Đã xóa thành công!", BaseTransientBottomBar.LENGTH_LONG).show();
                            searchHistoryAdapter.deleteHistory(position);
                        }

                        @Override
                        public void onFailure(String message) {
                            Snackbar.make(binding.getRoot(), message, BaseTransientBottomBar.LENGTH_LONG).show();
                        }
                    });
        }));
        searchUserAdapter = new SearchUserAdapter(this, sharedPreferences.getString(SharedPreferenceKeys.USER_ID, ""));

        binding.rcvSearchUserResult.setAdapter(searchUserAdapter);
        binding.rcvSearchUserResult.setLayoutManager(new LinearLayoutManager(this));

        binding.rcvSearchHistory.setAdapter(searchHistoryAdapter);
        binding.rcvSearchHistory.setLayoutManager(LayoutManagerUtil.disabledScrollLinearManager(this, LinearLayoutManager.VERTICAL));
    }

    private void initEvents() {
        binding.btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    KeyBoardUtils.hideKeyboard(SearchingActivity.this);

                    String key = binding.edtSearch.getText().toString();

                    if(!key.isEmpty()) {
                        Intent intent = new Intent(SearchingActivity.this, SearchResultActivity.class);
                        intent.putExtra("key", key);

                        startActivity(intent);
                    }else {
                        Snackbar.make(binding.getRoot(), "Vui lòng nhập thông tin cần tìm!", BaseTransientBottomBar.LENGTH_LONG).show();
                    }

                    return true;
                }
                return false;
            }
        });

        binding.edtSearch.addTextChangedListener(new DebounceUtil(new DebounceUtil.OnDebouncedListener() {
            @Override
            public void onDebouncedTextChanged(CharSequence text) {
                handleSearch(text.toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence) {
                String key = charSequence.toString();
                if(key.isEmpty())
                    binding.btnClearText.setVisibility(View.GONE);
                else
                    binding.btnClearText.setVisibility(View.VISIBLE);
            }
        }));
        binding.btnClearText.setOnClickListener(v -> {
            binding.edtSearch.setText("");
            displaySearchHistory();
        });
        binding.btnClearHistory.setOnClickListener(v -> {
            String token = sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, "");
            searchViewModel.clearSearchHistory(token, new ActionCallback<Object>() {
                @Override
                public void onSuccess() {
                    searchHistoryAdapter.clearHistory();
                    Snackbar.make(binding.getRoot(), "Đã xóa tất cả!", BaseTransientBottomBar.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(String message) {
                    Snackbar.make(binding.getRoot(), message, BaseTransientBottomBar.LENGTH_LONG).show();
                }
            });
        });
    }

    private void handleSearch(String key) {
        String token = sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, "");
        if(key.isEmpty()) {
            displaySearchHistory();
        }else {
            doSearch(token, key);
        }
    }

    private void displaySearchHistory() {
        binding.searchHistoryLayout.setVisibility(View.VISIBLE);
        binding.rcvSearchUserResult.setVisibility(View.GONE);
        binding.noResultFound.setVisibility(View.GONE);

        fetchSearchHistory(sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""));
    }

    private void displaySearchUserResult() {
        binding.searchHistoryLayout.setVisibility(View.GONE);
        binding.rcvSearchUserResult.setVisibility(View.VISIBLE);
        binding.noResultFound.setVisibility(View.GONE);
    }

    private void displayNoResultFound() {
        binding.searchHistoryLayout.setVisibility(View.GONE);
        binding.rcvSearchUserResult.setVisibility(View.GONE);
        binding.noResultFound.setVisibility(View.VISIBLE);
    }

    private void fetchSearchHistory(String token) {
        searchViewModel.getSearchHistory(token, new ActionCallback<List<SearchHistory>>() {
            @Override
            public void onSuccess(List<SearchHistory> searchHistories) {
                searchHistoryAdapter.setSearchHistories(searchHistories);
            }

            @Override
            public void onFailure(String message) {
                Snackbar.make(binding.getRoot(), message, BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });
    }

    private void doSearch(String token, String key) {
        searchViewModel.doSearch(token, key, new ActionCallback<SearchResponse>() {
            @Override
            public void onSuccess(SearchResponse searchResponse) {
                if(searchResponse.getUsers().isEmpty()){
                    displayNoResultFound();
                }else{
                    displaySearchUserResult();
                    searchUserAdapter.setUsers(searchResponse.getUsers());
                }
            }

            @Override
            public void onFailure(String message) {
                Snackbar.make(binding.getRoot(), message, BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });
    }
}