package vn.edu.tdtu.buddyblend.ui.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import vn.edu.tdtu.buddyblend.adapters.mediaviewer.MediaAdapter;
import vn.edu.tdtu.buddyblend.databinding.ActivityMediaViewerBinding;
import vn.edu.tdtu.buddyblend.dto.PostMedia;
import vn.edu.tdtu.buddyblend.models.PostResponse;
import vn.edu.tdtu.buddyblend.utils.DateUtils;

public class MediaViewerActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMediaViewerBinding binding;
    private MediaAdapter mediaAdapter;
    private List<PostMedia> postMedias;
    private int itemIndex;
    private PostResponse post;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMediaViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void initObjects(){
        mediaAdapter = new MediaAdapter();
        mediaAdapter.setPostMedias(postMedias);
        mediaAdapter.setContext(this);
    }

    private void initEvents(){
        binding.btnBack.setOnClickListener(this);
    }

    private void init() {
        Intent intent = getIntent();
        if(intent != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
           Gson gson = new Gson();
           post = intent.getSerializableExtra("post", PostResponse.class);
           String mediasStr = intent.getExtras()
                    .getString("medias", "");
           Type listType = new TypeToken<List<PostMedia>>() {}.getType();
           postMedias = gson.fromJson(mediasStr, listType);
           itemIndex = intent.getIntExtra("itemIndex", 0);
        }

        initObjects();
        initEvents();
        bindView();

        binding.viewPager.setAdapter(mediaAdapter);
        binding.viewPager.setCurrentItem(itemIndex, false);
    }

    private void bindView(){
        binding.userName.setText(post.getUserFullName());
        binding.createdAt.setText(DateUtils.getTimeAgo(DateUtils.stringToDate(post.getCreatedAt())));
    }

    @Override
    public void onClick(View view) {
        if(view == binding.btnBack){
            onBackPressed();
        }
    }
}