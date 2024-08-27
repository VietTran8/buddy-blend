package vn.edu.tdtu.buddyblend.adapters.search;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vn.edu.tdtu.buddyblend.dto.response.SearchResponse;
import vn.edu.tdtu.buddyblend.ui.fragments.SearchResultAllFragment;
import vn.edu.tdtu.buddyblend.ui.fragments.SearchResultPostsFragment;
import vn.edu.tdtu.buddyblend.ui.fragments.SearchResultUsersFragment;

public class SearchResultAdapter extends FragmentStateAdapter {
    private SearchResponse searchResponse;

    public SearchResultAdapter(@NonNull FragmentActivity fragmentActivity, SearchResponse searchResponse) {
        super(fragmentActivity);
        this.searchResponse = searchResponse;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new SearchResultAllFragment(searchResponse);
            case 1:
                return new SearchResultUsersFragment(searchResponse);
            default:
                return new SearchResultPostsFragment(searchResponse);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
