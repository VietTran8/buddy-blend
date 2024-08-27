package vn.edu.tdtu.buddyblend.ui.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.adapters.createpost.MediaAdapter;
import vn.edu.tdtu.buddyblend.business.FileBusiness;
import vn.edu.tdtu.buddyblend.business.PostBusiness;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.databinding.ActivityCreatePostBinding;
import vn.edu.tdtu.buddyblend.dto.request.CreatePostReqDTO;
import vn.edu.tdtu.buddyblend.dto.request.PostTagReqDTO;
import vn.edu.tdtu.buddyblend.enums.EMediaType;
import vn.edu.tdtu.buddyblend.enums.EPostType;
import vn.edu.tdtu.buddyblend.enums.EPrivacy;
import vn.edu.tdtu.buddyblend.models.PostResponse;
import vn.edu.tdtu.buddyblend.models.MinimizedUser;
import vn.edu.tdtu.buddyblend.models.User;
import vn.edu.tdtu.buddyblend.utils.ScreenManager;
import vn.edu.tdtu.buddyblend.utils.SharedPreferenceKeys;
import vn.edu.tdtu.buddyblend.utils.UriUtils;

public class CreatePostActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityCreatePostBinding binding;
    private User currentUser;
    private EPrivacy currentPrivacy;
    private MediaAdapter mediaAdapter;
    private List<MinimizedUser> taggedMinimizedUser;
    private FileBusiness fileBusiness;
    private PostBusiness postBusiness;
    private SharedPreferences sharedPreferences;
    List<Uri> selectedMediaUris;
    private static final int PICK_IMAGE_VIDEO_REQUEST_CODE = 123123123;
    private ActivityResultLauncher<Intent> mStartPrivacyForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        currentPrivacy = EPrivacy.valueOf(intent.getStringExtra("privacy"));
                        switch (currentPrivacy) {
                            case PUBLIC -> {
                                binding.privacyIcon.setImageDrawable(getDrawable(R.drawable.baseline_public_24));
                                binding.privacyText.setText("Công khai");
                            }case PRIVATE -> {
                                binding.privacyIcon.setImageDrawable(getDrawable(R.drawable.baseline_lock_24));
                                binding.privacyText.setText("Chỉ mình tôi");
                            }case ONLY_FRIENDS -> {
                                binding.privacyIcon.setImageDrawable(getDrawable(R.drawable.baseline_people_alt_24));
                                binding.privacyText.setText("Bạn bè");
                            }
                        }
                    }
                }
            });

    private ActivityResultLauncher<Intent> mStartTagForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        Gson gson = new Gson();
                        String jsonStr = intent.getStringExtra("taggedUsers");
                        Type typeToken = new TypeToken<List<MinimizedUser>>() {}.getType();
                        taggedMinimizedUser = gson.fromJson(jsonStr, typeToken);
                        updateTaggedUserUI();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ScreenManager.enableFullScreen(getWindow());
        init();
    }

    private void updateTaggedUserUI(){
        if(!taggedMinimizedUser.isEmpty()){
            String userNameText = "";
            if(taggedMinimizedUser.size() > 1){
                userNameText = currentUser.getUserFullName()
                        + " - cùng với "
                        + taggedMinimizedUser.get(0).getUserFullName()
                        + " và "
                        + (taggedMinimizedUser.size() - 1)
                        + " người khác";
            }else {
                userNameText = currentUser.getUserFullName()
                        + " - cùng với "
                        + taggedMinimizedUser.get(0).getUserFullName();
            }

            binding.userName.setText(userNameText);
        }else{
            binding.userName.setText(currentUser.getUserFullName());
        }
    }

    private void init() {
        selectedMediaUris = new ArrayList<>();
        fileBusiness = FileBusiness.getInstance();
        postBusiness = PostBusiness.getInstance();
        sharedPreferences = getSharedPreferences(SharedPreferenceKeys.USER_SHARED_PREFS, MODE_PRIVATE);
        taggedMinimizedUser = new ArrayList<>();
        Intent intent = getIntent();
        if (intent != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                currentUser = intent.getExtras().getSerializable("user", User.class);
                bindView();
            }
        }
        currentPrivacy = EPrivacy.PUBLIC;
        initEvents();
    }

    private void openGalleryForImageAndVideo() {
        Intent intent = new Intent();
        intent.setType("video/*, image/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"video/*", "image/*"});
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture or Video"), PICK_IMAGE_VIDEO_REQUEST_CODE);
    }

    private void bindView(){
        String profileUrl = currentUser.getProfilePicture();
        if(profileUrl == null || profileUrl.isEmpty()){
            Picasso.get()
                    .load(R.drawable.user)
                    .placeholder(R.color.light_gray)
                    .error(R.color.light_gray)
                    .into(binding.userAvatar);
        }else
            Picasso.get()
                    .load(profileUrl)
                    .placeholder(R.color.light_gray)
                    .error(R.color.light_gray)
                    .into(binding.userAvatar);

        binding.userName.setText(currentUser.getUserFullName());
    }

    private void initEvents(){
        binding.privacy.setOnClickListener(this);
        binding.btnUpload.setOnClickListener(this);
        binding.btnTag.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
        binding.btnPost.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == binding.privacy){
            Intent intent = new Intent(this, SelectPrivacyActivity.class);
            intent.putExtra("currentPrivacy", currentPrivacy.name());
            mStartPrivacyForResult.launch(intent);
        }else if(view == binding.btnUpload){
            openGalleryForImageAndVideo();
        }else if(view == binding.btnTag){
            Intent intent = new Intent(this, TagUserActivity.class);
            Gson gson = new Gson();
            intent.putExtra("currentTaggedUser", gson.toJson(taggedMinimizedUser));
            mStartTagForResult.launch(intent);
        }else if(view == binding.btnBack){
            onBackPressed();
        }else if(view == binding.btnPost){
            CreatePostReqDTO requestBody = prepareRequestBody();
            List<Uri> imageUris = UriUtils.groupByMediaType(CreatePostActivity.this, selectedMediaUris).get(EMediaType.TYPE_IMAGE);
            List<Uri> videoUris = UriUtils.groupByMediaType(CreatePostActivity.this, selectedMediaUris).get(EMediaType.TYPE_VIDEO);

            ProgressDialog progressDialog = new ProgressDialog(CreatePostActivity.this);
            progressDialog.setMessage("Đang tạo bài viết...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            fileBusiness.uploadAll(CreatePostActivity.this, imageUris, "img", new ActionCallback<List<String>>() {
                @Override
                public void onSuccess(List<String> strings) {
                    requestBody.setImageUrls(strings);
                    fileBusiness.uploadAll(CreatePostActivity.this, videoUris, "video", new ActionCallback<List<String>>() {
                        @Override
                        public void onSuccess(List<String> strings) {
                            requestBody.setVideoUrls(strings);

                            postBusiness.createPost(sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""), requestBody, new ActionCallback<PostResponse>() {
                                @Override
                                public void onSuccess(PostResponse postResponse) {
                                    progressDialog.dismiss();
                                    Intent intent = new Intent();
                                    intent.putExtra("createdPost", postResponse);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }

                                @Override
                                public void onFailure(String message) {
                                    Log.d("asdasd", "onError: " + message);

                                    Toast.makeText(CreatePostActivity.this, message, Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onError(Exception e) {
                                    Log.d("asdasd", "onError: " + e.getMessage());
                                    Toast.makeText(CreatePostActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            });
                        }

                        @Override
                        public void onFailure(String message) {
                            Log.d("asdasd", "onError: " + message);
                            Toast.makeText(CreatePostActivity.this, message, Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(CreatePostActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.d("asdasd", "onError: " + e.getMessage());
                            progressDialog.dismiss();
                        }
                    });
                }

                @Override
                public void onFailure(String message) {
                    Log.d("asdasd", "onError: " + message);
                    Toast.makeText(CreatePostActivity.this, message, Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }

                @Override
                public void onError(Exception e) {
                    Log.d("asdasd", "onError: " + e.getMessage());
                    Toast.makeText(CreatePostActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });

        }
    }

    private CreatePostReqDTO prepareRequestBody(){
        CreatePostReqDTO requestBody = new CreatePostReqDTO();
        requestBody.setContent(binding.edtContent.getText().toString());
        requestBody.setPostTags(taggedMinimizedUser.stream().map(user -> {
            PostTagReqDTO dto = new PostTagReqDTO();
            dto.setTaggedUserId(user.getId());
            return dto;
        }).collect(Collectors.toList()));
        requestBody.setPrivacy(currentPrivacy);
        requestBody.setType(EPostType.NORMAL);
        requestBody.setActive(true);
        requestBody.setImageUrls(new ArrayList<>());
        requestBody.setVideoUrls(new ArrayList<>());

        return requestBody;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_VIDEO_REQUEST_CODE && resultCode == RESULT_OK) {
            selectedMediaUris = new ArrayList<>();
            if (data.getData() != null) {
                // Người dùng chọn một ảnh/video
                selectedMediaUris.add(data.getData());
            } else if (data.getClipData() != null) {
                // Người dùng chọn nhiều ảnh/video
                ClipData clipData = data.getClipData();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    selectedMediaUris.add(item.getUri());
                }
            }

            Transition transition = new AutoTransition();
            transition.setDuration(300);
            TransitionManager.beginDelayedTransition((ViewGroup) binding.rcvMediaContainer.getParent(), transition);
            if(!selectedMediaUris.isEmpty()){
                binding.rcvMediaContainer.setVisibility(View.VISIBLE);
                mediaAdapter = new MediaAdapter(this, selectedMediaUris, new MediaAdapter.OnEmptyListListener() {
                    @Override
                    public void onEmptyList() {
                        TransitionManager.beginDelayedTransition((ViewGroup) binding.rcvMediaContainer.getParent(), transition);
                        binding.rcvMediaContainer.setVisibility(View.GONE);
                    }
                });
                binding.rcvMedia.hasFixedSize();
                binding.rcvMedia.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                binding.rcvMedia.setAdapter(mediaAdapter);
            }else{
                binding.rcvMediaContainer.setVisibility(View.GONE);
            }
        }
    }
}