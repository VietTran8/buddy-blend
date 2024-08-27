package vn.edu.tdtu.buddyblend.ui.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.adapters.createpost.MediaAdapter;
import vn.edu.tdtu.buddyblend.adapters.homefragment.PostAdapter;
import vn.edu.tdtu.buddyblend.adapters.useraccount.FriendAdapter;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.components.MyDialog;
import vn.edu.tdtu.buddyblend.components.skeleton.Skeleton;
import vn.edu.tdtu.buddyblend.components.skeleton.SkeletonScreen;
import vn.edu.tdtu.buddyblend.databinding.ActivityUserAccountBinding;
import vn.edu.tdtu.buddyblend.dto.request.SharePostReqDTO;
import vn.edu.tdtu.buddyblend.dto.response.HandleFriendRequestResponse;
import vn.edu.tdtu.buddyblend.dto.response.UserDetailsResponse;
import vn.edu.tdtu.buddyblend.enums.EPostType;
import vn.edu.tdtu.buddyblend.enums.EPrivacy;
import vn.edu.tdtu.buddyblend.models.PostResponse;
import vn.edu.tdtu.buddyblend.models.User;
import vn.edu.tdtu.buddyblend.ui.components.BottomSheet;
import vn.edu.tdtu.buddyblend.ui.viewmodel.HomeViewModel;
import vn.edu.tdtu.buddyblend.ui.viewmodel.UserAccountViewModel;
import vn.edu.tdtu.buddyblend.ui.viewmodel.factories.HomeViewModelFactory;
import vn.edu.tdtu.buddyblend.utils.ImageViewUtils;
import vn.edu.tdtu.buddyblend.utils.LayoutManagerUtil;
import vn.edu.tdtu.buddyblend.utils.ScreenManager;
import vn.edu.tdtu.buddyblend.utils.SharedPreferenceKeys;

public class UserAccountActivity extends AppCompatActivity {
    private ActivityUserAccountBinding binding;
    private UserAccountViewModel userAccountViewModel;
    private FriendAdapter friendAdapter;
    private SharedPreferences sharedPreferences;
    private String token;
    private PostAdapter postAdapter;
    private SkeletonScreen postSkeleton;
    private HomeViewModel homeViewModel;
    private static final int COMMENT_REQUEST_CODE = 8734873;
    private static final int SELECT_SHARE_PRIVACY_RQ = 234359;
    private static final int EDIT_POST_RQ = 123412;
    private static final int PICK_IMAGE_FOR_AVATAR_RQ = 123123123;
    private static final int PICK_IMAGE_FOR_COVER_RQ = 123123124;
    private ProgressDialog progressDialog;
    private BottomSheet bottomSheet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserAccountBinding.inflate(getLayoutInflater());
        ScreenManager.enableFullScreen(this.getWindow());
        userAccountViewModel = new ViewModelProvider(this).get(UserAccountViewModel.class);
        sharedPreferences = getSharedPreferences(SharedPreferenceKeys.USER_SHARED_PREFS, MODE_PRIVATE);
        token = sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, "");
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        ViewModelProvider.Factory factory = new HomeViewModelFactory(sharedPreferences, new HomeViewModel.OnCurrentUserFetchedFail() {
            @Override
            public void onFailure(int statusCode, String message) {
            }
        });
        homeViewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);

        setContentView(binding.getRoot());

        init();
    }

    private void showPostSkeleton() {
        this.postSkeleton = Skeleton.bind(binding.rcvPosts)
                .adapter(postAdapter)
                .load(R.layout.skeleton_item_post)
                .count(10)
                .duration(1000)
                .show();
    }

    private void hidePostSkeleton(){
        postSkeleton.hide();
    }

    private User getCurrentUser(){
        User user = new User();
        user.setUserFullName(sharedPreferences.getString(SharedPreferenceKeys.USER_NAME, ""));
        user.setProfilePicture(sharedPreferences.getString(SharedPreferenceKeys.USER_IMAGE, ""));
        user.setId(sharedPreferences.getString(SharedPreferenceKeys.USER_ID, ""));

        return user;
    }

    private void initPosts() {
        postAdapter = new PostAdapter();
        postAdapter.setHomeViewModel(homeViewModel);
        postAdapter.setContext(UserAccountActivity.this);
        postAdapter.setFragmentManager(getSupportFragmentManager());
        postAdapter.setLayoutInflater(getLayoutInflater());
        postAdapter.setCurrentUser(getCurrentUser());
        postAdapter.setOnSharePostButtonClickListener((v, postId, status, bottomSheet) -> {
            SharePostReqDTO reqDTO = new SharePostReqDTO();
            reqDTO.setPostId(postId);
            reqDTO.setPrivacy(homeViewModel.getCurrentPrivacy());
            reqDTO.setStatus(status);

            ProgressDialog dialog = new ProgressDialog(UserAccountActivity.this);
            dialog.setCancelable(false);
            dialog.setMessage("Đang chia sẻ");
            dialog.show();

            homeViewModel.sharePost(
                    sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""),
                    reqDTO,
                    new ActionCallback<PostResponse>() {
                        @Override
                        public void onSuccess(PostResponse postResponse) {
                            Toast.makeText(UserAccountActivity.this, "Đã chia sẻ bài viết!", Toast.LENGTH_LONG).show();
                            bottomSheet.dismiss();
                            dialog.dismiss();
                        }

                        @Override
                        public void onFailure(String message) {
                            Toast.makeText(UserAccountActivity.this, message, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
            );
        });

        postAdapter.setOnActionButtonClickListener(new PostAdapter.OnActionButtonClickListener() {
            @Override
            public void onEditClick(PostResponse post, User user, int position) {
                Intent intent = new Intent(UserAccountActivity.this, EditPostActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("position", position);
                intent.putExtra("postId", post.getType().equals(EPostType.SHARE) ? post.getShareInfo().getId() : post.getId());
                intent.putExtra("taggedUsers", post.getType().equals(EPostType.SHARE) ? new ArrayList<>() : (ArrayList) post.getTaggedUsers());
                intent.putExtra("currentPrivacy", post.getType().equals(EPostType.SHARE) ? post.getShareInfo().getPrivacy().name() : post.getPrivacy().name());
                intent.putExtra("postType", post.getType().name());
                intent.putExtra("oldContent", post.getType().equals(EPostType.SHARE) ? post.getShareInfo().getStatus() : post.getContent());
                intent.putStringArrayListExtra("mediaUrls",
                        (ArrayList<String>) Stream.concat(post.getImageUrls().stream(), post.getVideoUrls().stream()).collect(Collectors.toList()));

                startActivityForResult(intent, EDIT_POST_RQ);
            }

            @Override
            public void onDeleteClick(String postId, int position) {
                MyDialog myDialog = new MyDialog(UserAccountActivity.this, getLayoutInflater(), R.layout.dialog_confirm, true, new MyDialog.Handler() {
                    @Override
                    public void handle(AlertDialog dialog, View contentView) {
                        TextView txtMessage = contentView.findViewById(R.id.txtMessage);
                        Button cancel = contentView.findViewById(R.id.btnCancel);
                        Button accept = contentView.findViewById(R.id.btnChange);

                        txtMessage.setText("Bạn có chắc là muốn xóa bài viết này?");

                        cancel.setOnClickListener(view -> dialog.dismiss());
                        accept.setOnClickListener(view -> {
                            homeViewModel.deletePost(sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""),
                                    postId,
                                    new ActionCallback<Object>() {
                                        @Override
                                        public void onSuccess() {
                                            postAdapter.deletePost(position);
                                            Snackbar.make(binding.getRoot(), "Xóa thành công!", BaseTransientBottomBar.LENGTH_LONG).show();
                                            dialog.dismiss();
                                        }

                                        @Override
                                        public void onFailure(String message) {
                                            Snackbar.make(binding.getRoot(), message, BaseTransientBottomBar.LENGTH_LONG).show();
                                            dialog.dismiss();
                                        }
                                    });
                        });
                    }
                });

                myDialog.show(getSupportFragmentManager(), "DELETE_POST_DIALOG");
            }
        });

        postAdapter.setOnPrivacyButtonClickListener(v -> {
            homeViewModel.setBtnPrivacy(v);
            Intent intent = new Intent(UserAccountActivity.this, SelectPrivacyActivity.class);
            intent.putExtra("currentPrivacy", homeViewModel.getCurrentPrivacy().name());
            startActivityForResult(intent, SELECT_SHARE_PRIVACY_RQ);
        });
        postAdapter.setOnCommentButtonClickListener((v, quantity, post, index) -> {
            Intent intent = new Intent(UserAccountActivity.this, CommentActivity.class);
            intent.putExtra("postId",
                    post.getType().equals(EPostType.SHARE) ? post.getShareInfo().getId() : post.getId());
            intent.putExtra("postIndex", index);
            intent.putExtra("userName", post.getUser().getUserFullName());
            intent.putExtra("reactCount", quantity);
            startActivityForResult(intent, COMMENT_REQUEST_CODE);
        });
        binding.rcvPosts.setAdapter(postAdapter);
        binding.rcvPosts.setLayoutManager(LayoutManagerUtil.disabledScrollLinearManager(UserAccountActivity.this, LinearLayoutManager.VERTICAL));
    }

    private void init() {
        String userId = getIntent().getStringExtra("userId");
        userAccountViewModel.setCurrentUserId(userId);

        initObjects();
        initPosts();

        showPostSkeleton();

        userAccountViewModel.getUserPosts(sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""), userAccountViewModel.getCurrentUserId())
                .observe(this, posts -> {
                    postAdapter.setPosts(posts);
                    hidePostSkeleton();
                });

        userAccountViewModel.getUserProfile(token, new ActionCallback<UserDetailsResponse>() {
            @Override
            public void onSuccess(UserDetailsResponse userDetailsResponse) {
                userAccountViewModel.setCurrentUser(userDetailsResponse);

                if(userDetailsResponse.getMutualFriends().isEmpty())
                    friendAdapter.setFriends(userDetailsResponse.getOtherFriends());
                else
                    friendAdapter.setFriends(userDetailsResponse.getMutualFriends());

                bindView();
            }

            @Override
            public void onFailure(String message) {
                Snackbar.make(binding.getRoot(), message, BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });

        initEvents();
    }

    private void initObjects() {
        friendAdapter = new FriendAdapter(this);

        binding.rcvFriends.setAdapter(friendAdapter);
        binding.rcvFriends.setLayoutManager(LayoutManagerUtil.disabledScrollGridManager(this, 3));
    }

    private void openGalleryForImage(int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"image/*"});
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        startActivityForResult(Intent.createChooser(intent, "Select Picture or Video"), requestCode);
    }

    private void initEvents() {
        binding.btnUploadCover.setOnClickListener(v -> {
            bottomSheet = new BottomSheet(UserAccountActivity.this, getLayoutInflater(), R.layout.dialog_upload_image, "Cập nhật ảnh");
            bottomSheet.setup(((dialogWindow, contentView) -> {
                Button btnAvatar = contentView.findViewById(R.id.btnAvatar);
                Button btnCover = contentView.findViewById(R.id.btnCover);

                btnAvatar.setOnClickListener(view -> {
                    openGalleryForImage(PICK_IMAGE_FOR_AVATAR_RQ);
                });

                btnCover.setOnClickListener(view -> {
                    openGalleryForImage(PICK_IMAGE_FOR_COVER_RQ);
                });
            }));

            bottomSheet.show();
        });

        binding.btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.userAvatarWrapper.setOnClickListener(v -> {
            Intent intent = new Intent(this, ViewProfilePictureActivity.class);
            intent.putExtra("fullName", userAccountViewModel.getCurrentUser().getUserFullName());
            intent.putExtra("imageUrl", userAccountViewModel.getCurrentUser().getProfilePicture());

            startActivity(intent);
        });

        binding.coverBackground.setOnClickListener(v -> {
            Intent intent = new Intent(this, ViewProfilePictureActivity.class);
            intent.putExtra("fullName", userAccountViewModel.getCurrentUser().getUserFullName());
            intent.putExtra("imageUrl", userAccountViewModel.getCurrentUser().getCoverPicture());

            startActivity(intent);
        });

        binding.btnFriendHandle.setOnClickListener(v -> {
            UserDetailsResponse currentUser = userAccountViewModel.getCurrentUser();

            if(currentUser.isFriend()) {
                MyDialog confirmDialog = new MyDialog(UserAccountActivity.this, getLayoutInflater(), R.layout.dialog_confirm, false, new MyDialog.Handler() {
                    @Override
                    public void handle(AlertDialog dialog, View contentView) {
                        TextView message = contentView.findViewById(R.id.txtMessage);
                        Button btnConfirm = contentView.findViewById(R.id.btnChange);
                        Button btnCancel = contentView.findViewById(R.id.btnCancel);

                        message.setText("Bạn có chắc là muốn hủy kết bạn với người này?");
                        btnConfirm.setOnClickListener(v -> {
                            startHandleFriendRequest(currentUser, dialog);
                        });

                        btnCancel.setOnClickListener(v -> {
                            dialog.dismiss();
                        });
                    }
                });

                confirmDialog.show(getSupportFragmentManager(), "CONFIRM_DIALOG");
            }else {
                startHandleFriendRequest(currentUser, null);
            }
        });
    }

    private void startHandleFriendRequest(UserDetailsResponse currentUser, AlertDialog dialog) {
        userAccountViewModel.handleFriendRequest(token, currentUser.getId(), new ActionCallback<HandleFriendRequestResponse>() {
            @Override
            public void onSuccess(HandleFriendRequestResponse handleFriendRequestResponse) {
                switch (handleFriendRequestResponse.getStatus()) {
                    case CANCELLED -> {
                        binding.btnFriendHandle.setText("+ Thêm bạn bè");
                        currentUser.setFriend(false);
                    }
                    case PENDING -> {
                        binding.btnFriendHandle.setText("Đã gửi lời mời");
                    }
                }

                if(dialog != null)
                    dialog.dismiss();
            }

            @Override
            public void onFailure(String message) {
                Snackbar.make(binding.getRoot(), message, BaseTransientBottomBar.LENGTH_LONG).show();
                if(dialog != null)
                    dialog.dismiss();
            }
        });
    }

    private void displayButtonLayout() {
        binding.btnActionLayout.setVisibility(View.VISIBLE);
        binding.btnEditProfile.setVisibility(View.GONE);
    }

    private void displayEditProfile() {
        binding.btnActionLayout.setVisibility(View.GONE);
        binding.btnEditProfile.setVisibility(View.VISIBLE);
    }

    private void bindView() {
        UserDetailsResponse currentUser = userAccountViewModel.getCurrentUser();

        switch (currentUser.getFriendStatus()) {
            case NOT_YET -> {
                binding.btnFriendHandle.setText("+ Thêm bạn bè");
            }
            case IS_FRIEND -> {
                binding.btnFriendHandle.setText("Đã là bạn bè");
            }
            case SENT_BY_YOU -> {
                binding.btnFriendHandle.setText("Đã gửi lời mời");
            }
            case SENT_TO_YOU -> {
                binding.btnFriendHandle.setText("Phản hồi");
            }
        }

        binding.userNameHeader.setText(currentUser.getUserFullName());
        ImageViewUtils.bindUserImage(this, currentUser.getProfilePicture(), binding.userAvatar);
        ImageViewUtils.bindCoverImage(this, currentUser.getCoverPicture(), binding.coverBackground);
        binding.userFullName.setText(currentUser.getUserFullName());
        binding.bio.setText(currentUser.getBio());

        displayAction(currentUser);

        binding.email.setText("Email: " + currentUser.getEmail());
        binding.gender.setText("Giới tính: " + currentUser.getGender());
        binding.createdAt.setText("Tham gia vào: " + currentUser.getCreatedAt());

        int mutualFriendCount = currentUser.getMutualFriends().size();
        if(mutualFriendCount > 0)
            binding.mutualFriendTitle.setText(mutualFriendCount + " bạn chung");
        else
            binding.mutualFriendTitle.setText(currentUser.getFriendsCount() + " bạn bè");
    }

    private void displayAction(UserDetailsResponse currentUser) {
        if(currentUser.isMyAccount()) {
            displayEditProfile();
            binding.btnUploadCover.setVisibility(View.VISIBLE);
        }else {
            displayButtonLayout();
            binding.btnUploadCover.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COMMENT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            int totalCmts = data.getIntExtra("totalCmts", 0);
            int postIndex = data.getIntExtra("postIndex", 0);

            postAdapter.getPosts()
                    .get(postIndex)
                    .setNoComments(totalCmts);
            postAdapter.notifyItemChanged(postIndex);
        } else if (requestCode == SELECT_SHARE_PRIVACY_RQ && resultCode == Activity.RESULT_OK) {
            ImageView privacyIcon = homeViewModel.getBtnPrivacy().findViewById(R.id.privacyIcon);
            TextView privacyText = homeViewModel.getBtnPrivacy().findViewById(R.id.privacyText);

            homeViewModel.setCurrentPrivacy(EPrivacy.valueOf(data.getStringExtra("privacy")));
            switch (homeViewModel.getCurrentPrivacy()) {
                case PUBLIC -> {
                    privacyIcon.setImageDrawable(UserAccountActivity.this.getDrawable(R.drawable.baseline_public_24));
                    privacyText.setText("Công khai");
                }
                case PRIVATE -> {
                    privacyIcon.setImageDrawable(UserAccountActivity.this.getDrawable(R.drawable.baseline_lock_24));
                    privacyText.setText("Chỉ mình tôi");
                }
                case ONLY_FRIENDS -> {
                    privacyIcon.setImageDrawable(UserAccountActivity.this.getDrawable(R.drawable.baseline_people_alt_24));
                    privacyText.setText("Bạn bè");
                }
            }
        } else if (requestCode == EDIT_POST_RQ && resultCode == Activity.RESULT_OK) {
            int position = data.getIntExtra("position", -1);
            PostResponse updatedPost = (PostResponse) data.getSerializableExtra("updatedPost");
            postAdapter.updatePost(updatedPost, position);
            Snackbar.make(binding.getRoot(), "Cập nhật bài viết thành công!", Snackbar.LENGTH_LONG).show();
        } else if (requestCode == PICK_IMAGE_FOR_AVATAR_RQ && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                progressDialog.setMessage("Đang tải ảnh lên...");
                progressDialog.show();
                Uri selectedImage = data.getData();
                userAccountViewModel.updateUserAvatar(UserAccountActivity.this, token, selectedImage, new ActionCallback<Object>() {
                    @Override
                    public void onSuccess() {
                        progressDialog.dismiss();
                        Glide.with(UserAccountActivity.this)
                                .load(selectedImage)
                                .into(binding.userAvatar);
                        bottomSheet.dismiss();
                        Snackbar.make(binding.getRoot(), "Cập nhật ảnh đại diện thành công", BaseTransientBottomBar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(String message) {
                        progressDialog.dismiss();
                        Snackbar.make(binding.getRoot(), message, BaseTransientBottomBar.LENGTH_LONG).show();
                    }
                });
            }
        } else if (requestCode == PICK_IMAGE_FOR_COVER_RQ && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                progressDialog.setMessage("Đang tải ảnh lên...");
                progressDialog.show();
                Uri selectedImage = data.getData();
                userAccountViewModel.updateCoverAvatar(UserAccountActivity.this, token, selectedImage, new ActionCallback<Object>() {
                    @Override
                    public void onSuccess() {
                        progressDialog.dismiss();
                        Glide.with(UserAccountActivity.this)
                                .load(selectedImage)
                                .into(binding.coverBackground);
                        bottomSheet.dismiss();
                        Snackbar.make(binding.getRoot(), "Cập nhật ảnh bìa thành công", BaseTransientBottomBar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(String message) {
                        progressDialog.dismiss();
                        Snackbar.make(binding.getRoot(), message, BaseTransientBottomBar.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}