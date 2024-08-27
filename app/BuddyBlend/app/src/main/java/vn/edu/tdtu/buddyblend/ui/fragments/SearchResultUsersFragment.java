package vn.edu.tdtu.buddyblend.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.adapters.search.SearchUser2Adapter;
import vn.edu.tdtu.buddyblend.databinding.FragmentSearchResultUsersBinding;
import vn.edu.tdtu.buddyblend.dto.response.SearchResponse;
import vn.edu.tdtu.buddyblend.models.User;
import vn.edu.tdtu.buddyblend.utils.LayoutManagerUtil;
import vn.edu.tdtu.buddyblend.utils.SharedPreferenceKeys;

public class SearchResultUsersFragment extends Fragment {
    private FragmentSearchResultUsersBinding binding;
    private SearchResponse searchResponse;
    private SearchUser2Adapter searchUser2Adapter;
    private SharedPreferences sharedPreferences;



    public SearchResultUsersFragment(SearchResponse searchResponse) {
        this.searchResponse = searchResponse;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchResultUsersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(SharedPreferenceKeys.USER_SHARED_PREFS, Context.MODE_PRIVATE);

        searchUser2Adapter = new SearchUser2Adapter(getContext(), getCurrentUser().getId());
        searchUser2Adapter.setUsers(searchResponse.getUsers());

        binding.rcvPeople.setAdapter(searchUser2Adapter);
        binding.rcvPeople.setLayoutManager(LayoutManagerUtil.disabledScrollGridManager(getContext(), LinearLayoutManager.VERTICAL));
    }

    private User getCurrentUser(){
        User user = new User();
        user.setUserFullName(sharedPreferences.getString(SharedPreferenceKeys.USER_NAME, ""));
        user.setProfilePicture(sharedPreferences.getString(SharedPreferenceKeys.USER_IMAGE, ""));
        user.setId(sharedPreferences.getString(SharedPreferenceKeys.USER_ID, ""));

        return user;
    }
}