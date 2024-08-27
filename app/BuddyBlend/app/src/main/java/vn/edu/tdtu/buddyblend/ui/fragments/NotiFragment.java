package vn.edu.tdtu.buddyblend.ui.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.adapters.notification.NotificationAdapter;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.databinding.FragmentNotiBinding;
import vn.edu.tdtu.buddyblend.models.InteractNotification;
import vn.edu.tdtu.buddyblend.models.PostResponse;
import vn.edu.tdtu.buddyblend.ui.activities.MediaActivity;
import vn.edu.tdtu.buddyblend.ui.components.BottomSheet;
import vn.edu.tdtu.buddyblend.ui.viewmodel.NotiViewModel;
import vn.edu.tdtu.buddyblend.utils.LayoutManagerUtil;
import vn.edu.tdtu.buddyblend.utils.SharedPreferenceKeys;

public class NotiFragment extends Fragment {
    private FragmentNotiBinding binding;
    private NotiViewModel notiViewModel;
    private NotificationAdapter notificationAdapter;
    private SharedPreferences sharedPreferences;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String json = intent.getStringExtra("notification_data");
            InteractNotification notification = new Gson().fromJson(json, InteractNotification.class);
            updateUI(notification);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotiBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        notiViewModel = new ViewModelProvider(requireActivity()).get(NotiViewModel.class);
        sharedPreferences = getActivity().getSharedPreferences(SharedPreferenceKeys.USER_SHARED_PREFS, Context.MODE_PRIVATE);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("NEW_NOTIFICATION"));

        init();
    }

    public void init() {
        initObjects();

        notiViewModel.getNotifications(sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""))
                .observe(getViewLifecycleOwner(), result -> {
                    notificationAdapter.setNotifications(result);
                });

        binding.rcvNotifications.setAdapter(notificationAdapter);
        binding.rcvNotifications.setLayoutManager(LayoutManagerUtil.disabledScrollLinearManager(getContext(), LinearLayoutManager.VERTICAL));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
    }

    public void initObjects() {
        notificationAdapter = new NotificationAdapter(getContext());
        notificationAdapter.setOnActionButtonClickListener(new NotificationAdapter.OnClick() {
            @Override
            public void onActionClick(InteractNotification notification, int position) {
                BottomSheet bottomSheet = new BottomSheet(getContext(), getLayoutInflater(), R.layout.bottom_sheet_noti_action, "");
                bottomSheet.setup(((dialogWindow, contentView) -> {
                    LinearLayout btnDetach = contentView.findViewById(R.id.btnDetach);
                    TextView txtInfo = contentView.findViewById(R.id.notiInfo);
                    ImageView imgUserAvatar = contentView.findViewById(R.id.userAvatar);

                    String content = notification.getContent().replace(notification.getUserFullName(), " ");
                    content = "<b>" + notification.getUserFullName() + "</b>" + content;

                    if(notification.getAvatarUrl() != null && !notification.getAvatarUrl().isEmpty()){
                        Glide.with(getContext())
                                .load(notification.getAvatarUrl())
                                .placeholder(R.color.light_gray)
                                .into(imgUserAvatar);
                    }else{
                        Glide.with(getContext())
                                .load(R.drawable.user)
                                .placeholder(R.color.light_gray)
                                .into(imgUserAvatar);
                    }

                    txtInfo.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY));

                    btnDetach.setOnClickListener(v -> {
                        String token = sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, "");
                        notiViewModel.detachNotification(token, notification.getId(), new ActionCallback<Object>() {
                            @Override
                            public void onSuccess() {
                                notificationAdapter.removeNotification(position);
                                dialogWindow.dismiss();
                            }

                            @Override
                            public void onFailure(String message) {
                                Snackbar.make(binding.getRoot(), message, BaseTransientBottomBar.LENGTH_LONG).show();
                                dialogWindow.dismiss();
                            }
                        });
                    });
                }));
                bottomSheet.show();
            }

            @Override
            public void onItemClick(InteractNotification notification, int position) {
                String token = sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, "");

                ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Đang tải");
                progressDialog.show();

                notiViewModel.getPostById(token, notification.getPostId(), new ActionCallback<PostResponse>() {
                    @Override
                    public void onSuccess(PostResponse postResponse) {
                        progressDialog.dismiss();
                        Intent intent = new Intent(getContext(), MediaActivity.class);
                        intent.putExtra("post", postResponse);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(String message) {
                        progressDialog.dismiss();
                        Snackbar.make(binding.getRoot(), message, BaseTransientBottomBar.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public void initEvents(){

    }

    public void updateUI(InteractNotification interactNotification) {
        notificationAdapter.addNotification(interactNotification);
    }
}