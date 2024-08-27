package vn.edu.tdtu.buddyblend.ui.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.adapters.mediaacvitity.SinglePostAdapter;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.components.MyDialog;
import vn.edu.tdtu.buddyblend.databinding.ActivityMediaBinding;
import vn.edu.tdtu.buddyblend.dto.PostMedia;
import vn.edu.tdtu.buddyblend.dto.request.SharePostReqDTO;
import vn.edu.tdtu.buddyblend.enums.EPostType;
import vn.edu.tdtu.buddyblend.enums.EPrivacy;
import vn.edu.tdtu.buddyblend.enums.EReactionType;
import vn.edu.tdtu.buddyblend.models.PostResponse;
import vn.edu.tdtu.buddyblend.models.TopReacts;
import vn.edu.tdtu.buddyblend.models.User;
import vn.edu.tdtu.buddyblend.ui.components.BottomSheet;
import vn.edu.tdtu.buddyblend.ui.viewmodel.MediaViewModel;
import vn.edu.tdtu.buddyblend.utils.DateUtils;
import vn.edu.tdtu.buddyblend.utils.LayoutManagerUtil;
import vn.edu.tdtu.buddyblend.utils.PopupMenuUtils;
import vn.edu.tdtu.buddyblend.utils.ScreenManager;
import vn.edu.tdtu.buddyblend.utils.SharedPreferenceKeys;
import vn.edu.tdtu.buddyblend.utils.StringUtils;

public class MediaActivity extends AppCompatActivity {
    private ActivityMediaBinding binding;
    private SinglePostAdapter adapter;
    private PostResponse currentPost;
    private MediaViewModel mediaViewModel;
    private SharedPreferences sharedPreferences;
    private User currentUser;
    private static int COMMENT_REQUEST_CODE = 1283123;
    private static int SELECT_SHARE_PRIVACY_RQ = 1200293;
    private static int EDIT_POST_RQ = 12930293;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMediaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ScreenManager.enableFullScreen(getWindow());
        mediaViewModel = new ViewModelProvider(this).get(MediaViewModel.class);
        sharedPreferences = getSharedPreferences(SharedPreferenceKeys.USER_SHARED_PREFS, MODE_PRIVATE);

        init();
    }

    private void init() {
        Intent intent = getIntent();
        if (intent != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            currentPost = intent.getExtras()
                    .getSerializable("post", PostResponse.class);
        }

        initObjects();
        initRcv();
        bindView();
        initEvents();
    }

    private User getCurrentUser(){
        User user = new User();
        user.setUserFullName(sharedPreferences.getString(SharedPreferenceKeys.USER_NAME, ""));
        user.setProfilePicture(sharedPreferences.getString(SharedPreferenceKeys.USER_IMAGE, ""));
        user.setId(sharedPreferences.getString(SharedPreferenceKeys.USER_ID, ""));

        return user;
    }

    private void initObjects() {
        currentUser = getCurrentUser();

        adapter = new SinglePostAdapter();
        adapter.setContext(this);
        adapter.setCurrentPost(currentPost);

        List<PostMedia> mediaList = new ArrayList<>();
        mediaList = currentPost.getImageUrls().stream().map(
                url -> new PostMedia(url, true)
        ).collect(Collectors.toList());
        mediaList.addAll(
                currentPost.getVideoUrls().stream().map(
                        url -> new PostMedia(url, false)
                ).collect(Collectors.toList()
                ));

        adapter.setMedias(mediaList);
    }

    private void initRcv() {
        binding.rvcPosts.setAdapter(adapter);
        binding.rvcPosts.setLayoutManager(LayoutManagerUtil.disabledScrollLinearManager(this, LinearLayoutManager.VERTICAL));
    }

    private void initEvents() {
        String token = sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, "");
        PopupMenuUtils popupMenuUtils;
        if(currentPost.isMine() ||
                (currentPost.getType().equals(EPostType.SHARE) &&
                        currentPost.getShareInfo().getSharedUser().getId().equals(currentUser.getId()))){
            popupMenuUtils = new PopupMenuUtils(MediaActivity.this, R.menu.menu_my_post_action, menuItem -> {
                int id = menuItem.getItemId();
                if(id == R.id.itemEditPost){
                    Intent intent = new Intent(MediaActivity.this, EditPostActivity.class);
                    intent.putExtra("user", currentUser);
                    intent.putExtra("position", 0);
                    intent.putExtra("postId", currentPost.getType().equals(EPostType.SHARE) ? currentPost.getShareInfo().getId() : currentPost.getId());
                    intent.putExtra("taggedUsers", currentPost.getType().equals(EPostType.SHARE) ? new ArrayList<>() : (ArrayList) currentPost.getTaggedUsers());
                    intent.putExtra("currentPrivacy", currentPost.getType().equals(EPostType.SHARE) ? currentPost.getShareInfo().getPrivacy().name() : currentPost.getPrivacy().name());
                    intent.putExtra("postType", currentPost.getType().name());
                    intent.putExtra("oldContent", currentPost.getType().equals(EPostType.SHARE) ? currentPost.getShareInfo().getStatus() : currentPost.getContent());
                    intent.putStringArrayListExtra("mediaUrls",
                            (ArrayList<String>) Stream.concat(currentPost.getImageUrls().stream(), currentPost.getVideoUrls().stream()).collect(Collectors.toList()));

                    startActivityForResult(intent, EDIT_POST_RQ);
                }else if(id == R.id.itemDelPost){
                    MyDialog myDialog = new MyDialog(MediaActivity.this, getLayoutInflater(), R.layout.dialog_confirm, true, new MyDialog.Handler() {
                        @Override
                        public void handle(AlertDialog dialog, View contentView) {
                            TextView txtMessage = contentView.findViewById(R.id.txtMessage);
                            Button cancel = contentView.findViewById(R.id.btnCancel);
                            Button accept = contentView.findViewById(R.id.btnChange);

                            txtMessage.setText("Bạn có chắc là muốn xóa bài viết này?");

                            cancel.setOnClickListener(view -> dialog.dismiss());
                            accept.setOnClickListener(view -> {
                                mediaViewModel.deletePost(sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""),
                                        currentPost.getId(),
                                        new ActionCallback<Object>() {
                                            @Override
                                            public void onSuccess() {
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
        }else{
            popupMenuUtils = new PopupMenuUtils(MediaActivity.this, R.menu.menu_post_action, menuItem -> {
                int id = menuItem.getItemId();
                if(id == R.id.itemFavourite){
                    Toast.makeText(MediaActivity.this, "Favourite", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.itemReport){
                    Toast.makeText(MediaActivity.this, "Report", Toast.LENGTH_SHORT).show();
                }
            });
        }

        binding.btnAction.setOnClickListener(v -> {
            popupMenuUtils.setView(v);
            popupMenuUtils.show();
        });

        binding.btnShare.setOnClickListener(v -> {
            BottomSheet bottomSheet = new BottomSheet(MediaActivity.this, getLayoutInflater(), R.layout.bottom_sheet_share_post, "Chia sẻ bài viết");

            bottomSheet.setup(((dialogWindow, contentView) -> {
                ImageView userAvatar = contentView.findViewById(R.id.userAvatar);
                ImageView privacyIcon = contentView.findViewById(R.id.privacyIcon);
                TextView userFullName = contentView.findViewById(R.id.userFullName);
                TextView privacyText = contentView.findViewById(R.id.privacyText);
                LinearLayout btnPrivacy = contentView.findViewById(R.id.btnPrivacy);
                EditText edtContent = contentView.findViewById(R.id.edtContent);
                Button btnShare = contentView.findViewById(R.id.btnShare);

                String userAvatarUrl = currentUser.getProfilePicture();
                if(userAvatarUrl != null && !userAvatarUrl.isEmpty()) {
                    Glide.with(MediaActivity.this)
                            .load(userAvatarUrl)
                            .error(R.drawable.user)
                            .placeholder(R.color.light_gray)
                            .into(userAvatar);
                }else {
                    Glide.with(MediaActivity.this)
                            .load(R.drawable.user)
                            .error(R.drawable.user)
                            .placeholder(R.color.light_gray)
                            .into(userAvatar);
                }

                btnPrivacy.setOnClickListener(view -> {
                    mediaViewModel.setBtnPrivacy(view);
                    Intent intent = new Intent(MediaActivity.this, SelectPrivacyActivity.class);
                    intent.putExtra("currentPrivacy", mediaViewModel.getCurrentPrivacy().name());
                    startActivityForResult(intent, SELECT_SHARE_PRIVACY_RQ);
                });

                userFullName.setText(currentUser.getUserFullName());

                btnShare.setOnClickListener(view -> {
                    String status = edtContent.getText().toString();
                    String postId = currentPost.getId();

                    SharePostReqDTO reqDTO = new SharePostReqDTO();
                    reqDTO.setPostId(postId);
                    reqDTO.setPrivacy(mediaViewModel.getCurrentPrivacy());
                    reqDTO.setStatus(status);

                    ProgressDialog dialog = new ProgressDialog(MediaActivity.this);
                    dialog.setCancelable(false);
                    dialog.setMessage("Đang chia sẻ");
                    dialog.show();

                    mediaViewModel.sharePost(
                            sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""),
                            reqDTO,
                            new ActionCallback<PostResponse>() {
                                @Override
                                public void onSuccess(PostResponse postResponse) {
                                    Snackbar.make(binding.getRoot(), "Đã chia sẻ bài viết!", BaseTransientBottomBar.LENGTH_LONG).show();
                                    bottomSheet.dismiss();
                                    dialog.dismiss();
                                }

                                @Override
                                public void onFailure(String message) {
                                    Snackbar.make(binding.getRoot(), message, BaseTransientBottomBar.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            }
                    );
                });
            }));

            bottomSheet.show();
        });

        binding.btnComment.setOnClickListener(v -> {
            Intent intent = new Intent(this, CommentActivity.class);
            intent.putExtra("postId",
                    currentPost.getType().equals(EPostType.SHARE) ? currentPost.getShareInfo().getId() : currentPost.getId());
            intent.putExtra("postIndex", 0);
            intent.putExtra("userName", currentPost.getUser().getUserFullName());
            intent.putExtra("reactCount", binding.reactionCount.getText().toString());
            startActivityForResult(intent, COMMENT_REQUEST_CODE);
        });

        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.btnReact.setOnClickListener(view -> {
            ImageView[] icons = new ImageView[] {
                    binding.firstIcon,
                    binding.secondIcon,
                    binding.thirdIcon
            };

            ImageView icon = binding.btnReact.findViewById(R.id.reactIcon);
            TextView title = binding.btnReact.findViewById(R.id.reactTitle);
            EReactionType type = currentPost.getReacted();
            String postId = currentPost.getId();

            if(currentPost.getType().equals(EPostType.SHARE))
                postId = currentPost.getShareInfo().getId();

            if(type == null){
                mediaViewModel.doReact(token, postId, EReactionType.LIKE, new ActionCallback<List<TopReacts>>() {
                    @Override
                    public void onSuccess(List<TopReacts> topReacts) {
                        currentPost.setTopReacts(topReacts);
                        bindReactions(topReacts, binding.reactionCount, icons);

                        Glide.with(MediaActivity.this)
                                .load(R.drawable.like_icon)
                                .into(icon);
                        title.setTextColor(getColor(R.color.like));
                        currentPost.setReacted(EReactionType.LIKE);
                        increaseReactCount(binding.reactionCount, currentPost);
                    }
                });
            }else{
                mediaViewModel.doReact(token, postId, type, new ActionCallback<List<TopReacts>>() {
                    @Override
                    public void onSuccess(List<TopReacts> topReacts) {
                        currentPost.setTopReacts(topReacts);
                        bindReactions(topReacts, binding.reactionCount, icons);

                        Glide.with(MediaActivity.this)
                                .load(R.drawable.not_like)
                                .into(icon);
                        title.setTextColor(getColor(R.color.gray));
                        currentPost.setReacted(null);
                        decreaseReactCount(binding.reactionCount, currentPost);
                    }
                });
            }
            title.setText(R.string.like_vn);
        });

        binding.btnReact.setOnLongClickListener(view -> {
            ImageView[] icons = new ImageView[] {
                binding.firstIcon,
                binding.secondIcon,
                binding.thirdIcon
            };

            MyDialog myDialog = new MyDialog(this, getLayoutInflater(), R.layout.dialog_react, true, new MyDialog.Handler() {
                @Override
                public void handle(AlertDialog dialog, View contentView) {
                    ImageView btnLike = contentView.findViewById(R.id.btnLike);
                    ImageView btnWow = contentView.findViewById(R.id.btnWow);
                    ImageView btnAngry = contentView.findViewById(R.id.btnAngry);
                    ImageView btnHeart = contentView.findViewById(R.id.btnHeart);
                    ImageView btnHaha = contentView.findViewById(R.id.btnHaha);
                    ImageView btnSad = contentView.findViewById(R.id.btnSad);

                    ImageView[] buttons = new ImageView[]{
                            btnLike, btnWow, btnHeart, btnHaha, btnAngry, btnSad
                    };

                    EReactionType[] types = new EReactionType[]{
                            EReactionType.LIKE, EReactionType.WOW,
                            EReactionType.HEART, EReactionType.HAHA,
                            EReactionType.ANGRY, EReactionType.SAD
                    };

                    for (int i = 0; i < buttons.length; i++) {
                        ImageView currentBtn = buttons[i];
                        int tempI = i;
                        EReactionType type = types[tempI];
                        currentBtn.setOnClickListener(view -> {
                            String postId = currentPost.getId();
                            if (currentPost.getType().equals(EPostType.SHARE)) {
                                postId = currentPost.getShareInfo().getId();
                            }

                            mediaViewModel.doReact(token, postId, type, new ActionCallback<List<TopReacts>>() {
                                @Override
                                public void onSuccess(List<TopReacts> topReacts) {
                                    currentPost.setTopReacts(topReacts);
                                    bindReactions(topReacts, binding.reactionCount, icons);

                                    ImageView icon = binding.btnReact.findViewById(R.id.reactIcon);
                                    TextView title = binding.btnReact.findViewById(R.id.reactTitle);
                                    if (currentPost.getReacted() != type) {
                                        switch (type) {
                                            case LIKE -> {
                                                Glide.with(MediaActivity.this)
                                                        .load(R.drawable.like_icon)
                                                        .into(icon);
                                                title.setTextColor(getColor(R.color.like));
                                                title.setText(R.string.like_vn);
                                            }
                                            case HEART -> {
                                                Glide.with(MediaActivity.this)
                                                        .load(R.drawable.reaction_heart)
                                                        .into(icon);
                                                title.setTextColor(getColor(R.color.heart));
                                                title.setText(R.string.heart_vn);
                                            }
                                            case WOW -> {
                                                Glide.with(MediaActivity.this)
                                                        .load(R.drawable.react_wow)
                                                        .into(icon);
                                                title.setTextColor(getColor(R.color.wow));
                                                title.setText(R.string.wow_vn);
                                            }
                                            case HAHA -> {
                                                Glide.with(MediaActivity.this)
                                                        .load(R.drawable.react_haha)
                                                        .into(icon);
                                                title.setTextColor(getColor(R.color.haha));
                                                title.setText(R.string.haha_vn);
                                            }
                                            case SAD -> {
                                                Glide.with(MediaActivity.this)
                                                        .load(R.drawable.react_sad)
                                                        .into(icon);
                                                title.setTextColor(getColor(R.color.sad));
                                                title.setText(R.string.sad_vn);
                                            }
                                            case ANGRY -> {
                                                Glide.with(MediaActivity.this)
                                                        .load(R.drawable.react_angry)
                                                        .into(icon);
                                                title.setTextColor(getColor(R.color.angry));
                                                title.setText(R.string.angry_vn);
                                            }
                                        }
                                        if (currentPost.getReacted() == null) {
                                            increaseReactCount(binding.reactionCount, currentPost);
                                        }
                                        currentPost.setReacted(type);
                                    } else {
                                        currentPost.setReacted(null);
                                        title.setTextColor(getColor(R.color.gray));
                                        title.setText(R.string.like_vn);
                                        Glide.with(MediaActivity.this)
                                                .load(R.drawable.not_like)
                                                .into(icon);
                                        decreaseReactCount(binding.reactionCount, currentPost);
                                    }

                                    dialog.dismiss();
                                }
                            });
                        });
                    }
                }
            });
            myDialog.show(getSupportFragmentManager(), "asd");

            return true;
        });
    }

    private void bindReactBtn(){
        LinearLayout btn = binding.btnReact;
        EReactionType type = currentPost.getReacted();

        ImageView icon = btn.findViewById(R.id.reactIcon);
        TextView title = btn.findViewById(R.id.reactTitle);
        if(type != null){
            switch (type){
                case LIKE -> {
                    Glide.with(MediaActivity.this)
                            .load(R.drawable.like_icon)
                            .into(icon);
                    title.setTextColor(MediaActivity.this.getColor(R.color.like));
                    title.setText(R.string.like_vn);
                }case HEART  -> {
                    Glide.with(MediaActivity.this)
                            .load(R.drawable.reaction_heart)
                            .into(icon);
                    title.setTextColor(MediaActivity.this.getColor(R.color.heart));
                    title.setText(R.string.heart_vn);
                }case WOW -> {
                    Glide.with(MediaActivity.this)
                            .load(R.drawable.react_wow)
                            .into(icon);
                    title.setTextColor(MediaActivity.this.getColor(R.color.wow));
                    title.setText(R.string.wow_vn);
                }case HAHA -> {
                    Glide.with(MediaActivity.this)
                            .load(R.drawable.react_haha)
                            .into(icon);
                    title.setTextColor(MediaActivity.this.getColor(R.color.haha));
                    title.setText(R.string.haha_vn);
                }case SAD -> {
                    Glide.with(MediaActivity.this)
                            .load(R.drawable.react_sad)
                            .into(icon);
                    title.setTextColor(MediaActivity.this.getColor(R.color.sad));
                    title.setText(R.string.sad_vn);
                }case ANGRY -> {
                    Glide.with(MediaActivity.this)
                            .load(R.drawable.react_angry)
                            .into(icon);
                    title.setTextColor(MediaActivity.this.getColor(R.color.angry));
                    title.setText(R.string.angry_vn);
                }
            }
        }else{
            Glide.with(MediaActivity.this)
                    .load(R.drawable.not_like)
                    .into(icon);
            title.setTextColor(MediaActivity.this.getColor(R.color.gray));
            title.setText(R.string.like_vn);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COMMENT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            int totalCmts = data.getIntExtra("totalCmts", 0);

            currentPost.setNoComments(totalCmts);
            binding.commentCount.setText(totalCmts + " bình luận");
        }else if(requestCode == SELECT_SHARE_PRIVACY_RQ && resultCode == Activity.RESULT_OK) {
            ImageView privacyIcon = mediaViewModel.getBtnPrivacy().findViewById(R.id.privacyIcon);
            TextView privacyText = mediaViewModel.getBtnPrivacy().findViewById(R.id.privacyText);

            mediaViewModel.setCurrentPrivacy(EPrivacy.valueOf(data.getStringExtra("privacy")));
            switch (mediaViewModel.getCurrentPrivacy()) {
                case PUBLIC -> {
                    privacyIcon.setImageDrawable(MediaActivity.this.getDrawable(R.drawable.baseline_public_24));
                    privacyText.setText("Công khai");
                }case PRIVATE -> {
                    privacyIcon.setImageDrawable(MediaActivity.this.getDrawable(R.drawable.baseline_lock_24));
                    privacyText.setText("Chỉ mình tôi");
                }case ONLY_FRIENDS -> {
                    privacyIcon.setImageDrawable(MediaActivity.this.getDrawable(R.drawable.baseline_people_alt_24));
                    privacyText.setText("Bạn bè");
                }
            }
        }else if (requestCode == EDIT_POST_RQ && resultCode == Activity.RESULT_OK) {
            PostResponse updatedPost = (PostResponse) data.getSerializableExtra("updatedPost");
            if (!updatedPost.getType().equals(EPostType.SHARE)) {
                currentPost.setContent(updatedPost.getContent());
                currentPost.setPrivacy(updatedPost.getPrivacy());
                currentPost.setVideoUrls(updatedPost.getVideoUrls());
                currentPost.setImageUrls(updatedPost.getImageUrls());
                currentPost.setTaggedUsers(updatedPost.getTaggedUsers());
            }else{
                currentPost.getShareInfo().setStatus(updatedPost.getShareInfo().getStatus());
                currentPost.getShareInfo().setPrivacy(updatedPost.getShareInfo().getPrivacy());
            }

            init();
            Snackbar.make(binding.getRoot(), "Cập nhật bài viết thành công!", Snackbar.LENGTH_LONG).show();
        }
    }

    private void increaseReactCount(TextView totalReacted, PostResponse post){
        int total = post.getNoReactions();
        if(total == 0){
            totalReacted.setText("Bạn");
        }else{
            totalReacted.setText("Bạn và " + (total) + " người khác");
        }
        post.setNoReactions(total + 1);
    }

    private void decreaseReactCount(TextView totalReacted, PostResponse post){
        int total = post.getNoReactions();
        total--;
        totalReacted.setText(String.valueOf(total));
        post.setNoReactions(total >= 0 ? total : 0);
    }

    private void bindReactions(List<TopReacts> topReacts, TextView reactCount, ImageView... imageViews){
        Arrays.stream(imageViews).forEach(img -> {
            img.setVisibility(View.GONE);
        });

        if(imageViews.length == 3 && topReacts.size() <= 3){
            int index = 0;
            for(TopReacts react : topReacts){
                bindReactIcon(imageViews[index], react.getType());
                imageViews[index].setVisibility(View.VISIBLE);
                index++;
            }
        }

        if(topReacts.isEmpty()) {
            reactCount.setVisibility(View.INVISIBLE);
        }else{
            reactCount.setVisibility(View.VISIBLE);
        }
    }

    private void bindReactions(PostResponse post, ImageView... imageViews){
        Arrays.stream(imageViews).forEach(img -> {
            img.setVisibility(View.GONE);
        });

        if(imageViews.length == 3 && post.getTopReacts().size() <= 3){
            int index = 0;
            for(TopReacts react : post.getTopReacts()){
                bindReactIcon(imageViews[index], react.getType());
                imageViews[index].setVisibility(View.VISIBLE);
                index++;
            }
        }
    }

    private void bindReactIcon(ImageView icon, EReactionType type){
        switch (type){
            case LIKE -> {
                Glide.with(MediaActivity.this)
                        .load(R.drawable.like_icon)
                        .into(icon);
            }case HEART  -> {
                Glide.with(MediaActivity.this)
                        .load(R.drawable.reaction_heart)
                        .into(icon);
            }case WOW -> {
                Glide.with(MediaActivity.this)
                        .load(R.drawable.react_wow)
                        .into(icon);
            }case HAHA -> {
                Glide.with(MediaActivity.this)
                        .load(R.drawable.react_haha)
                        .into(icon);
            }case SAD -> {
                Glide.with(MediaActivity.this)
                        .load(R.drawable.react_sad)
                        .into(icon);
            }case ANGRY -> {
                Glide.with(MediaActivity.this)
                        .load(R.drawable.react_angry)
                        .into(icon);
            }
        }
    }

    private void bindView(){
        binding.postContent.setText(currentPost.getContent());
        binding.commentCount.setText(currentPost.getNoComments() + " bình luận");
        binding.shareCount.setText(currentPost.getNoShared() + " chia sẻ");

        bindReactBtn();

        switch (currentPost.getPrivacy()){
            case PRIVATE -> {
                binding.privacy.setImageResource(R.drawable.baseline_lock_24);
            }case PUBLIC -> {
                binding.privacy.setImageResource(R.drawable.baseline_public_24);
            }case ONLY_FRIENDS -> {
                binding.privacy.setImageResource(R.drawable.baseline_people_alt_24);
            }
        }

        if(currentPost.getNoReactions() == 1 && currentPost.getReacted() != null){
            binding.reactionCount.setText("Bạn");
        }else if(currentPost.getNoReactions() > 1 && currentPost.getReacted() != null){
            binding.reactionCount.setText("Bạn và " + (currentPost.getNoReactions() - 1) + " người khác");
        }else{
            if(currentPost.getNoReactions() > 0){
                binding.reactionCount.setVisibility(View.VISIBLE);
                binding.reactionCount.setText(currentPost.getNoReactions() + "");
            }else
                binding.reactionCount.setVisibility(View.INVISIBLE);
        }

        bindUserName();

        String profileUrl = currentPost.getUser().getProfilePicture();
        if(profileUrl == null || profileUrl.isEmpty()){
            Glide.with(MediaActivity.this)
                    .load(R.drawable.user)
                    .placeholder(R.color.light_gray)
                    .error(R.color.light_gray)
                    .into(binding.userAvatar);
        }else
            Glide.with(MediaActivity.this)
                    .load(profileUrl)
                    .placeholder(R.color.light_gray)
                    .error(R.color.light_gray)
                    .into(binding.userAvatar);

        binding.createdAt.setText(DateUtils.getTimeAgo(DateUtils.stringToDate(currentPost.getCreatedAt())));

        switch (currentPost.getPrivacy()){
            case PRIVATE -> {
                binding.privacy.setImageResource(R.drawable.baseline_lock_24);
            }case PUBLIC -> {
                binding.privacy.setImageResource(R.drawable.baseline_public_24);
            }case ONLY_FRIENDS -> {
                binding.privacy.setImageResource(R.drawable.baseline_people_alt_24);
            }
        }

        bindReactions(currentPost, binding.firstIcon, binding.secondIcon, binding.thirdIcon);
    }

    private void bindUserName(){
        String userNameText = currentPost.getUserFullName();
        SpannableString userName;

        ClickableSpan clickableUserName = StringUtils.getClickableSpan(MediaActivity.this, view -> {
            Toast.makeText(MediaActivity.this, "userName clicked", Toast.LENGTH_SHORT).show();
        });

        if (currentPost.getTaggedUsers() != null && !currentPost.getTaggedUsers().isEmpty()) {
            ClickableSpan clickableFirstTagUser = StringUtils.getClickableSpan(MediaActivity.this, view -> {
                Toast.makeText(MediaActivity.this, "first tag user clicked", Toast.LENGTH_SHORT).show();
            });

            ClickableSpan clickableOtherTagUsers = StringUtils.getClickableSpan(MediaActivity.this, view -> {
                Intent intent = new Intent(MediaActivity.this, ViewTaggingUsersActivity.class);
                Gson gson = new Gson();

                String encodedTaggingUsers = gson.toJson(currentPost.getTaggedUsers());
                intent.putExtra("users", encodedTaggingUsers);

                MediaActivity.this.startActivity(intent);
            });

            String with = " - cùng với ";
            String firstTagUserFullName = currentPost.getTaggedUsers().get(0).getUserFullName() != null ? currentPost.getTaggedUsers().get(0).getUserFullName() : "";
            userNameText += with + firstTagUserFullName;

            int startFirstTagIndex = currentPost.getUserFullName().length() + with.length();

            if (currentPost.getTaggedUsers().size() == 1) {
                userName = new SpannableString(userNameText);
                userName.setSpan(clickableFirstTagUser, startFirstTagIndex, startFirstTagIndex + firstTagUserFullName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                String and = " và ";
                String others = (currentPost.getTaggedUsers().size() - 1) + " người khác.";
                userNameText += and + others;
                userName = new SpannableString(userNameText);

                int endFirstTagIndex = startFirstTagIndex + firstTagUserFullName.length();
                int startOthersIndex = endFirstTagIndex + and.length();

                userName.setSpan(clickableFirstTagUser, startFirstTagIndex, endFirstTagIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                userName.setSpan(clickableOtherTagUsers, startOthersIndex, startOthersIndex + others.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            userName.setSpan(clickableUserName, 0, currentPost.getUserFullName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            userName = new SpannableString(userNameText);
            userName.setSpan(clickableUserName, 0, currentPost.getUserFullName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        binding.userName.setText(userName);
        binding.userName.setMovementMethod(LinkMovementMethod.getInstance());
        binding.userName.setHighlightColor(0x00000000);
    }
}