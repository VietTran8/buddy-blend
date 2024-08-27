package vn.edu.tdtu.buddyblend.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.adapters.friendrequest.FriendRequestsAdapter;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.databinding.FragmentFriendsBinding;
import vn.edu.tdtu.buddyblend.dto.response.FriendRequestResponse;
import vn.edu.tdtu.buddyblend.dto.response.HandleFriendRequestResponse;
import vn.edu.tdtu.buddyblend.enums.EFriendReqStatus;
import vn.edu.tdtu.buddyblend.ui.viewmodel.FriendViewModel;
import vn.edu.tdtu.buddyblend.utils.LayoutManagerUtil;
import vn.edu.tdtu.buddyblend.utils.SharedPreferenceKeys;

public class FriendsFragment extends Fragment {
    private FragmentFriendsBinding binding;
    private FriendRequestsAdapter friendRequestsAdapter;
    private FriendViewModel friendViewModel;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentFriendsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);
        friendViewModel.setOnCurrentUserFetched(new FriendViewModel.OnCurrentUserFetchedFail() {
            @Override
            public void onFailure(int statusCode, String message) {

            }
        });

        sharedPreferences = getActivity().getSharedPreferences(SharedPreferenceKeys.USER_SHARED_PREFS, Context.MODE_PRIVATE);

        init();
    }

    private void init() {
        initObjects();
        binding.rcvFriendRequests.setLayoutManager(LayoutManagerUtil.disabledScrollLinearManager(getContext(), LinearLayoutManager.VERTICAL));
        binding.rcvFriendRequests.setAdapter(friendRequestsAdapter);

        friendViewModel.getFriendRequest()
                .observe(getViewLifecycleOwner(), result -> {
                    binding.friendRequestCount.setText(result.size() + "");
                    if(result.isEmpty()) {
                        displayNoRequest();
                    }else {
                        displayFriendRequests();
                        friendRequestsAdapter.setFriendRequests(result);
                    }
                });

        friendViewModel.getFriendRequest(sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""));
    }

    private void displayNoRequest() {
        binding.noRequestLayout.setVisibility(View.VISIBLE);
        binding.rcvFriendRequests.setVisibility(View.GONE);
    }

    private void displayFriendRequests() {
        binding.noRequestLayout.setVisibility(View.GONE);
        binding.rcvFriendRequests.setVisibility(View.VISIBLE);
    }

    private void initObjects() {
        friendRequestsAdapter = new FriendRequestsAdapter(getContext());
        friendRequestsAdapter.setOnButtonClickListener(new FriendRequestsAdapter.OnButtonClickListener() {
            @Override
            public void onAcceptClick(Button button, FriendRequestResponse request, int position) {
                friendViewModel.acceptFriendRequest(
                        sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""),
                        request.getId(),
                        true,
                        new ActionCallback<HandleFriendRequestResponse>() {
                            @Override
                            public void onSuccess(HandleFriendRequestResponse handleFriendRequestResponse) {
                                Snackbar.make(binding.getRoot(), "Đã chấp nhận lời mời kết bạn", BaseTransientBottomBar.LENGTH_LONG).show();
                            }

                            @Override
                            public void onFailure(String message) {
                                Snackbar.make(binding.getRoot(), message, BaseTransientBottomBar.LENGTH_LONG).show();
                            }
                        }
                );
            }

            @Override
            public void onRejectClick(Button button, FriendRequestResponse request, int position) {
                friendViewModel.acceptFriendRequest(
                        sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""),
                        request.getId(),
                        false,
                        new ActionCallback<HandleFriendRequestResponse>() {
                            @Override
                            public void onSuccess(HandleFriendRequestResponse handleFriendRequestResponse) {
                                Snackbar.make(binding.getRoot(), "Đã từ chối lời mời kết bạn", BaseTransientBottomBar.LENGTH_LONG).show();
                            }

                            @Override
                            public void onFailure(String message) {
                                Snackbar.make(binding.getRoot(), message, BaseTransientBottomBar.LENGTH_LONG).show();
                            }
                        }
                );
            }
        });
    }
}