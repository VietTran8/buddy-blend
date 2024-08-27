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

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.adapters.createpost.MediaAdapter;
import vn.edu.tdtu.buddyblend.business.FileBusiness;
import vn.edu.tdtu.buddyblend.business.PostBusiness;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.databinding.ActivityEditPostBinding;
import vn.edu.tdtu.buddyblend.dto.request.UpdatePostContentReqDTO;
import vn.edu.tdtu.buddyblend.enums.EMediaType;
import vn.edu.tdtu.buddyblend.enums.EPostType;
import vn.edu.tdtu.buddyblend.enums.EPrivacy;
import vn.edu.tdtu.buddyblend.models.MinimizedUser;
import vn.edu.tdtu.buddyblend.models.PostResponse;
import vn.edu.tdtu.buddyblend.models.User;
import vn.edu.tdtu.buddyblend.utils.ScreenManager;
import vn.edu.tdtu.buddyblend.utils.SharedPreferenceKeys;
import vn.edu.tdtu.buddyblend.utils.UriUtils;

public class EditPostActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityEditPostBinding binding;
    private User currentUser;
    private EPrivacy currentPrivacy;
    private List<MinimizedUser> taggedUser;
    private MediaAdapter mediaAdapter;
    private FileBusiness fileBusiness;
    private PostBusiness postBusiness;
    private SharedPreferences sharedPreferences;
    List<Uri> selectedMediaUris, oldMediaUris;
    private static final int PICK_IMAGE_VIDEO_REQUEST_CODE = 123123123;
    private Transition transition;
    private String currentContent;
    private EPostType postType;
    private String postId;
    private int currentPosition;

    private ActivityResultLauncher<Intent> mStartPrivacyForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        currentPrivacy = EPrivacy.valueOf(intent.getStringExtra("privacy"));
                        updatePrivacyUI();
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
                        taggedUser = gson.fromJson(jsonStr, typeToken);
                        updateTaggedUserUI();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ScreenManager.enableFullScreen(getWindow());
        init();
    }

    private void updatePrivacyUI() {
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

    private void init() {
        transition = new AutoTransition();
        transition.setDuration(300);

        oldMediaUris = new ArrayList<>();
        selectedMediaUris = new ArrayList<>();
        fileBusiness = FileBusiness.getInstance();
        postBusiness = PostBusiness.getInstance();
        sharedPreferences = getSharedPreferences(SharedPreferenceKeys.USER_SHARED_PREFS, MODE_PRIVATE);
        taggedUser = new ArrayList<>();

        binding.rcvMedia.hasFixedSize();
        binding.rcvMedia.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        Intent intent = getIntent();
        if (intent != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                currentUser = intent.getExtras().getSerializable("user", User.class);
                currentContent = intent.getStringExtra("oldContent");
                postId = intent.getStringExtra("postId");
                currentPosition = intent.getIntExtra("position", -1);
                currentPrivacy = EPrivacy.valueOf(intent.getStringExtra("currentPrivacy"));
                taggedUser = (ArrayList<MinimizedUser>) intent.getSerializableExtra("taggedUsers");
                postType = EPostType.valueOf(intent.getStringExtra("postType"));
                List<String> mediaUrls = intent.getStringArrayListExtra("mediaUrls");
                mediaUrls.forEach(url -> {
                    oldMediaUris.add(Uri.parse(url));
                });

                mediaAdapter = new MediaAdapter(this, oldMediaUris, new MediaAdapter.OnEmptyListListener() {
                    @Override
                    public void onEmptyList() {
                        TransitionManager.beginDelayedTransition((ViewGroup) binding.rcvMediaContainer.getParent(), transition);
                        binding.rcvMediaContainer.setVisibility(View.GONE);
                    }
                });

                binding.rcvMedia.setAdapter(mediaAdapter);

                bindView();
            }
        }

        initEvents();
    }
    private void initEvents(){
        binding.privacy.setOnClickListener(this);
        binding.btnUpload.setOnClickListener(this);
        binding.btnTag.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
        binding.btnPost.setOnClickListener(this);
    }
    private void bindView(){
        String profileUrl = currentUser.getProfilePicture();
        if(profileUrl == null || profileUrl.isEmpty()){
            Glide.with(this)
                    .load(R.drawable.user)
                    .placeholder(R.color.light_gray)
                    .error(R.color.light_gray)
                    .into(binding.userAvatar);
        }else
            Glide.with(this)
                    .load(profileUrl)
                    .placeholder(R.color.light_gray)
                    .error(R.color.light_gray)
                    .into(binding.userAvatar);

        TransitionManager.beginDelayedTransition((ViewGroup) binding.rcvMediaContainer.getParent(), transition);

        if(!oldMediaUris.isEmpty() && !postType.equals(EPostType.SHARE)) {
            binding.rcvMediaContainer.setVisibility(View.VISIBLE);
        }else{
            binding.rcvMediaContainer.setVisibility(View.GONE);
            if(postType.equals(EPostType.SHARE))
                binding.actionWrapper.setVisibility(View.GONE);
        }

        binding.edtContent.setText(currentContent);
        binding.userName.setText(currentUser.getUserFullName());
        updateTaggedUserUI();
        updatePrivacyUI();
    }

    private void updateTaggedUserUI(){
        if(!taggedUser.isEmpty()){
            String userNameText = "";
            if(taggedUser.size() > 1){
                userNameText = currentUser.getUserFullName()
                        + " - cùng với "
                        + taggedUser.get(0).getUserFullName()
                        + " và "
                        + (taggedUser.size() - 1)
                        + " người khác";
            }else {
                userNameText = currentUser.getUserFullName()
                        + " - cùng với "
                        + taggedUser.get(0).getUserFullName();
            }

            binding.userName.setText(userNameText);
        }else{
            binding.userName.setText(currentUser.getUserFullName());
        }
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
            intent.putExtra("currentTaggedUser", gson.toJson(taggedUser));
            mStartTagForResult.launch(intent);
        }else if(view == binding.btnBack){
            onBackPressed();
        }else if(view == binding.btnPost){
            UpdatePostContentReqDTO requestBody = prepareRequestBody();
            List<Uri> imageUris = UriUtils.groupByMediaType(EditPostActivity.this, mediaAdapter.getMediaList()).get(EMediaType.TYPE_IMAGE);
            List<Uri> videoUris = UriUtils.groupByMediaType(EditPostActivity.this, mediaAdapter.getMediaList()).get(EMediaType.TYPE_VIDEO);

            ProgressDialog progressDialog = new ProgressDialog(EditPostActivity.this);
            progressDialog.setMessage("Đang chỉnh sửa bài viết...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            fileBusiness.uploadAll(EditPostActivity.this, imageUris, "img", new ActionCallback<List<String>>() {
                @Override
                public void onSuccess(List<String> strings) {
                    requestBody.setImageUrls(strings);
                    fileBusiness.uploadAll(EditPostActivity.this, videoUris, "video", new ActionCallback<List<String>>() {
                        @Override
                        public void onSuccess(List<String> strings) {
                            requestBody.setVideoUrls(strings);

                            postBusiness.updatePost(sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""), requestBody, new ActionCallback<PostResponse>() {
                                @Override
                                public void onSuccess(PostResponse postResponse) {
                                    progressDialog.dismiss();
                                    Intent intent = new Intent();
                                    intent.putExtra("position", currentPosition);
                                    intent.putExtra("updatedPost", postResponse);

                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                }

                                @Override
                                public void onFailure(String message) {
                                    Toast.makeText(EditPostActivity.this, message, Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onError(Exception e) {
                                    Log.d("asdasd", "onError: " + e.getMessage());
                                    Toast.makeText(EditPostActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            });
                        }

                        @Override
                        public void onFailure(String message) {
                            Log.d("asdasd", "onError: " + message);
                            Toast.makeText(EditPostActivity.this, message, Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(EditPostActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.d("asdasd", "onError: " + e.getMessage());
                            progressDialog.dismiss();
                        }
                    });
                }

                @Override
                public void onFailure(String message) {
                    Log.d("asdasd", "onError: " + message);
                    Toast.makeText(EditPostActivity.this, message, Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }

                @Override
                public void onError(Exception e) {
                    Log.d("asdasd", "onError: " + e.getMessage());
                    Toast.makeText(EditPostActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_VIDEO_REQUEST_CODE && resultCode == RESULT_OK) {
            selectedMediaUris = new ArrayList<>();
            if (data.getData() != null) {
                selectedMediaUris.add(data.getData());
            } else if (data.getClipData() != null) {
                ClipData clipData = data.getClipData();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    selectedMediaUris.add(item.getUri());
                }
            }

            transition.setDuration(300);
            TransitionManager.beginDelayedTransition((ViewGroup) binding.rcvMediaContainer.getParent(), transition);
            if(!selectedMediaUris.isEmpty()){
                binding.rcvMediaContainer.setVisibility(View.VISIBLE);
                mediaAdapter.addMediaList(selectedMediaUris);
            }else{
                binding.rcvMediaContainer.setVisibility(View.GONE);
            }
        }
    }
    private UpdatePostContentReqDTO prepareRequestBody(){
        UpdatePostContentReqDTO requestBody = new UpdatePostContentReqDTO();
        requestBody.setId(postId);
        requestBody.setContent(binding.edtContent.getText().toString());
        requestBody.setTaggingUsers(taggedUser.stream().map(user -> user.getId()).collect(Collectors.toList()));
        requestBody.setPrivacy(currentPrivacy);
        requestBody.setImageUrls(new ArrayList<>());
        requestBody.setVideoUrls(new ArrayList<>());

        return requestBody;
    }

    private void openGalleryForImageAndVideo() {
        Intent intent = new Intent();
        intent.setType("video/*, image/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"video/*", "image/*"});
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture or Video"), PICK_IMAGE_VIDEO_REQUEST_CODE);
    }
}