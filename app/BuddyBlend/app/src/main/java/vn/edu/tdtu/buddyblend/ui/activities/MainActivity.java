package vn.edu.tdtu.buddyblend.ui.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.time.LocalDateTime;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.business.NotificationBusiness;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.databinding.ActivityMainBinding;
import vn.edu.tdtu.buddyblend.models.InteractNotification;
import vn.edu.tdtu.buddyblend.models.User;
import vn.edu.tdtu.buddyblend.ui.fragments.FriendsFragment;
import vn.edu.tdtu.buddyblend.ui.fragments.HomeFragment;
import vn.edu.tdtu.buddyblend.ui.fragments.MyAccountFragment;
import vn.edu.tdtu.buddyblend.ui.fragments.NotiFragment;
import vn.edu.tdtu.buddyblend.utils.ScreenManager;
import vn.edu.tdtu.buddyblend.utils.SharedPreferenceKeys;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private HomeFragment homeFragment;
    private FriendsFragment friendsFragment;
    private NotiFragment notiFragment;
    private MyAccountFragment myAccountFragment;
    private SharedPreferences sharedPreferences;
    private NotificationBusiness notificationBusiness;
    BadgeDrawable badgeDrawable;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String json = intent.getStringExtra("notification_data");
            InteractNotification notification = new Gson().fromJson(json, InteractNotification.class);
            updateUI(notification);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences(SharedPreferenceKeys.USER_SHARED_PREFS, MODE_PRIVATE);
        notificationBusiness = NotificationBusiness.getInstance();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("NEW_NOTIFICATION"));
        badgeDrawable = binding.bottomNavigationView.getOrCreateBadge(R.id.notiFragment);

        ScreenManager.enableFullScreen(getWindow());

        init();
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {});

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void init(){
        homeFragment = new HomeFragment();
        myAccountFragment = new MyAccountFragment();
        friendsFragment = new FriendsFragment();
        notiFragment = new NotiFragment();

        replaceFragment(homeFragment);

        notificationBusiness.fetchNotifications(sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""), new ActionCallback<Object>() {
            @Override
            public void onFailure(String message) {
                Log.d("MainActivity", "onFailure: " + message);
            }
        }).observe(this, notifications -> {
            badgeDrawable.setNumber(notifications.size() <= 99 ? notifications.size() : 99);
            badgeDrawable.setBackgroundColor(Color.RED);
        });

        askNotificationPermission();

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.homeFragment){
                replaceFragment(homeFragment);
            }else if(item.getItemId() == R.id.friendsFragment){
                replaceFragment(friendsFragment);
            }else if(item.getItemId() == R.id.notiFragment){
                replaceFragment(notiFragment);
            }else if(item.getItemId() == R.id.myAccountFragment){
                replaceFragment((myAccountFragment));
            }
            return true;
        });

        binding.btnChat.setOnClickListener(view -> {
            Intent intent = new Intent(this, ViewChatUsersActivity.class);
            startActivity(intent);
        });

        binding.btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchingActivity.class);
            startActivity(intent);
        });
    }

    private void updateUI(InteractNotification notification){
        int nextNumber = badgeDrawable.getNumber() + 1;
        badgeDrawable.setNumber(nextNumber <= 99 ? nextNumber : 99);
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.navHostFragmentContainer, fragment);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }
}