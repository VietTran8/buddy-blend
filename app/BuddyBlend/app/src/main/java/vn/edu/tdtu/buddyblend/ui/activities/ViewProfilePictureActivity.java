package vn.edu.tdtu.buddyblend.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.List;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.adapters.mediaviewer.MediaAdapter;
import vn.edu.tdtu.buddyblend.databinding.ActivityViewProfilePictureBinding;
import vn.edu.tdtu.buddyblend.dto.PostMedia;

public class ViewProfilePictureActivity extends AppCompatActivity {
    private ActivityViewProfilePictureBinding binding;
    private MediaAdapter mediaAdapter;
    private String fullName;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewProfilePictureBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        initObject();
        fullName = getIntent().getStringExtra("fullName");
        imageUrl = getIntent().getStringExtra("imageUrl");

        mediaAdapter.setPostMedias(List.of(
                new PostMedia(imageUrl, true)
        ));

        binding.viewPager.setAdapter(mediaAdapter);
        binding.userName.setText(fullName);
        binding.createdAt.setText("2 giờ trước");
        binding.btnBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void initObject() {
        mediaAdapter = new MediaAdapter();
        mediaAdapter.setContext(this);
    }
}