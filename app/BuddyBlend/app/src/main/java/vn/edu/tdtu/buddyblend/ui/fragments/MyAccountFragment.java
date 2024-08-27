package vn.edu.tdtu.buddyblend.ui.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.components.MyDialog;
import vn.edu.tdtu.buddyblend.databinding.FragmentMyAccountBinding;
import vn.edu.tdtu.buddyblend.ui.activities.LoginActivity;
import vn.edu.tdtu.buddyblend.ui.activities.UserAccountActivity;
import vn.edu.tdtu.buddyblend.ui.viewmodel.MyAccountViewModel;
import vn.edu.tdtu.buddyblend.utils.ImageViewUtils;
import vn.edu.tdtu.buddyblend.utils.SharedPreferenceKeys;

public class MyAccountFragment extends Fragment {
    private FragmentMyAccountBinding binding;
    private MyAccountViewModel myAccountViewModel;
    private SharedPreferences sharedPreferences;
    private String token;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMyAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myAccountViewModel = new ViewModelProvider(requireActivity()).get(MyAccountViewModel.class);
        sharedPreferences = getActivity().getSharedPreferences(SharedPreferenceKeys.USER_SHARED_PREFS, Context.MODE_PRIVATE);

        init();
    }

    public void init() {
        initObjects();
        initEvents();

        myAccountViewModel.fetchUser(sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""), new MyAccountViewModel.OnCurrentUserFetchedFail() {
            @Override
            public void onFailure(int statusCode, String message) {
                if(statusCode == 401){
                    MyDialog myDialog = new MyDialog(getContext(), getLayoutInflater(), R.layout.dialog_confirm, true, new MyDialog.Handler() {
                        @Override
                        public void handle(AlertDialog dialog, View contentView) {
                            TextView messageTv = contentView.findViewById(R.id.txtMessage);
                            Button cancel = contentView.findViewById(R.id.btnCancel);
                            Button accept = contentView.findViewById(R.id.btnChange);

                            messageTv.setText(message);

                            cancel.setVisibility(View.GONE);
                            accept.setOnClickListener(view -> {
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                sharedPreferences.edit().clear().apply();
                                getActivity().startActivity(intent);
                            });
                        }
                    });

                    myDialog.show(getParentFragmentManager(), "EXPIRED_DIALOG");
                }
            }
        });

        myAccountViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            myAccountViewModel.setUser(user);
            ImageViewUtils.bindUserImage(getContext(), user.getProfilePicture(), binding.userAvatar);
            binding.txtUserName.setText(user.getUserFullName());
        });
    }

    public void initObjects() {
        token = sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, "");
    }

    public void initEvents() {
        binding.btnSignOut.setOnClickListener(v -> {
            MyDialog myDialog = new MyDialog(getContext(), getLayoutInflater(), R.layout.dialog_confirm, false, new MyDialog.Handler() {
                @Override
                public void handle(AlertDialog dialog, View contentView) {
                    TextView txtMessage = contentView.findViewById(R.id.txtMessage);
                    Button btnAccept = contentView.findViewById(R.id.btnChange);
                    Button btnCancel = contentView.findViewById(R.id.btnCancel);

                    txtMessage.setText("Bạn có chắc là muốn đăng xuất?");
                    btnAccept.setOnClickListener(v -> {
                        startLogout();
                    });

                    btnCancel.setOnClickListener(v -> {
                        dialog.dismiss();
                    });
                }
            });

            myDialog.show(getParentFragmentManager(), "LOGOUT_DIALOG");
        });

        binding.btnPersonalInfo.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), UserAccountActivity.class);
            intent.putExtra("userId", myAccountViewModel.getUser().getId());
            startActivity(intent);
        });
    }

    private void startLogout() {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Đang đăng xuất...");
        progressDialog.show();

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w("MyAccountFragment", "Fetching FCM registration token failed", task.getException());
                    progressDialog.dismiss();
                    return;
                }
                String registrationId = task.getResult();
                myAccountViewModel.removeRegistrationId(token, registrationId, new ActionCallback<Object>() {
                    @Override
                    public void onSuccess() {
                        progressDialog.dismiss();
                        logout();
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

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();

        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
        requireActivity().finish();
    }
}