package vn.edu.tdtu.buddyblend.adapters.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.edu.tdtu.buddyblend.databinding.ItemViewSearchHistoryBinding;
import vn.edu.tdtu.buddyblend.dto.response.SearchHistory;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.MyViewHolder> {
    private List<SearchHistory> searchHistories;
    private Context context;
    private OnDeleteHistoryClickListener onDeleteHistoryClickListener;
    private OnHistoryClickLister onHistoryClickLister;

    public SearchHistoryAdapter(Context context) {
        this.context = context;
    }

    public List<SearchHistory> getSearchHistories() {
        return searchHistories;
    }

    public void setOnHistoryClickLister(OnHistoryClickLister onHistoryClickLister) {
        this.onHistoryClickLister = onHistoryClickLister;
    }

    public void setSearchHistories(List<SearchHistory> searchHistories) {
        this.searchHistories = searchHistories;
        notifyDataSetChanged();
    }

    public void deleteHistory(int position) {
        if(searchHistories != null) {
            searchHistories.remove(position);
            notifyDataSetChanged();
        }
    }

    public void clearHistory() {
        if(searchHistories != null){
            searchHistories.clear();
            notifyDataSetChanged();
        }
    }

    public void setOnDeleteHistoryClickListener(OnDeleteHistoryClickListener onDeleteHistoryClickListener) {
        this.onDeleteHistoryClickListener = onDeleteHistoryClickListener;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public interface OnDeleteHistoryClickListener {
        void onClick(int position, SearchHistory history);
    }

    public interface OnHistoryClickLister {
        void onClick(int position, SearchHistory history);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemViewSearchHistoryBinding binding = ItemViewSearchHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SearchHistory currentHistory = searchHistories.get(position);

        holder.bind(currentHistory, position);
    }

    @Override
    public int getItemCount() {
        return searchHistories != null ? searchHistories.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemViewSearchHistoryBinding binding;

        public MyViewHolder(@NonNull View itemView, ItemViewSearchHistoryBinding binding) {
            super(itemView);
            this.binding = binding;
        }

        public void bind(SearchHistory history, int position) {
            binding.query.setText(history.getQuery());
            binding.btnDelete.setOnClickListener(v -> onDeleteHistoryClickListener.onClick(position, history));

            itemView.setOnClickListener(v -> {
                onHistoryClickLister.onClick(position, history);
            });
        }
    }
}
