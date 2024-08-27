package vn.edu.tdtu.buddyblend.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.databinding.ActivitySelectPrivacyBinding;
import vn.edu.tdtu.buddyblend.enums.EPrivacy;
import vn.edu.tdtu.buddyblend.utils.ScreenManager;

public class SelectPrivacyActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivitySelectPrivacyBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectPrivacyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ScreenManager.enableFullScreen(getWindow());

        init();
    }

    private void init(){
        Intent intent = getIntent();
        EPrivacy privacy = EPrivacy.valueOf(intent.getStringExtra("currentPrivacy"));
        switch (privacy) {
            case PRIVATE -> {
                binding.rdoOnlyMe.setChecked(true);
            }case PUBLIC -> {
                binding.rdoPublic.setChecked(true);
            }case ONLY_FRIENDS -> {
                binding.rdoFriends.setChecked(true);
            }
        }
        initEvents();
    }

    private void initEvents(){
        binding.btnBack.setOnClickListener(this);
        binding.btnAccept.setOnClickListener(this);
        binding.btnFriends.setOnClickListener(this);
        binding.rdoFriends.setOnClickListener(this);
        binding.btnOnlyMe.setOnClickListener(this);
        binding.rdoOnlyMe.setOnClickListener(this);
        binding.btnPublic.setOnClickListener(this);
        binding.rdoPublic.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if(view == binding.btnAccept){
            String privacy = "PUBLIC";

            if(binding.rdoOnlyMe.isChecked()){
                privacy = "PRIVATE";
            }else if(binding.rdoFriends.isChecked()){
                privacy = "ONLY_FRIENDS";
            }

            Intent intent = new Intent();
            intent.putExtra("privacy", privacy);
            setResult(RESULT_OK, intent);

            finish();
        }else if(view == binding.btnFriends || view == binding.rdoFriends){
            uncheckedRadio();
            binding.rdoFriends.setChecked(true);
        }else if(view == binding.btnOnlyMe || view == binding.rdoOnlyMe){
            uncheckedRadio();
            binding.rdoOnlyMe.setChecked(true);
        }else if(view == binding.btnPublic || view == binding.rdoPublic){
            uncheckedRadio();
            binding.rdoPublic.setChecked(true);
        }else if(view == binding.btnBack){
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void uncheckedRadio(){
        binding.rdoPublic.setChecked(false);
        binding.rdoFriends.setChecked(false);
        binding.rdoOnlyMe.setChecked(false);
    }
}