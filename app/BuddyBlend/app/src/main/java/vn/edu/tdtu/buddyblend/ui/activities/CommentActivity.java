package vn.edu.tdtu.buddyblend.ui.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.SharedPreferencesKt;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.adapters.comment.CommentAdapter;
import vn.edu.tdtu.buddyblend.adapters.createpost.MediaAdapter;
import vn.edu.tdtu.buddyblend.databinding.ActivityCommentBinding;
import vn.edu.tdtu.buddyblend.enums.EReactionType;
import vn.edu.tdtu.buddyblend.models.React;
import vn.edu.tdtu.buddyblend.ui.viewmodel.CommentViewModel;
import vn.edu.tdtu.buddyblend.utils.KeyBoardUtils;
import vn.edu.tdtu.buddyblend.utils.LayoutManagerUtil;
import vn.edu.tdtu.buddyblend.utils.SharedPreferenceKeys;
import vn.edu.tdtu.buddyblend.utils.StringUtils;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityCommentBinding binding;
    private CommentViewModel viewModel;
    private CommentAdapter commentAdapter;
    private SharedPreferences sharedPreferences;
    private Transition transition;
    private ProgressDialog progressDialog;
    private static final int PICK_IMAGE_VIDEO_REQUEST_CODE = 123123123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(CommentViewModel.class);
        sharedPreferences = getSharedPreferences(SharedPreferenceKeys.USER_SHARED_PREFS, MODE_PRIVATE);

        init();
    }

    private void init(){
        viewModel.setPostId(getIntent().getStringExtra("postId"));
        viewModel.setUserName(getIntent().getStringExtra("userName"));
        viewModel.setPostIndex(getIntent().getIntExtra("postIndex", 0));
        viewModel.setReactCount(getIntent().getStringExtra("reactCount"));

        initObjects();

        binding.rcvComments.setAdapter(commentAdapter);
        binding.rcvComments.setLayoutManager(LayoutManagerUtil.disabledScrollLinearManager(this, LinearLayoutManager.VERTICAL));

        renderUI();
        initEvents();
    }

    private void renderUI(){
        viewModel.getComments(sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""), viewModel.getPostId(), message -> {
            Toast.makeText(CommentActivity.this, message, Toast.LENGTH_SHORT).show();
        }).observe(this, data -> {
            commentAdapter.setComments(data);
            if (!data.isEmpty()){
                binding.noCommentOverlay.setVisibility(View.GONE);
            }else{
                binding.noCommentOverlay.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getReactions(sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""), viewModel.getPostId(), msg -> {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }).observe(this, data -> {
            List<EReactionType> top3Reactions = getTop3Reacts(data);
            List<ImageView> top3Icons = List.of(binding.firstIcon, binding.secondIcon, binding.thirdIcon);

            Log.d("REACTION_TEST", "top 3 reactions: " + top3Reactions.size());

            IntStream.range(0, top3Reactions.size()).forEach(index -> {
                top3Icons.get(index).setVisibility(View.VISIBLE);
                bindTopReactIcon(top3Icons.get(index), top3Reactions.get(index));
            });
        });
        binding.edtContent.setHint(String.format("Bình luận dưới tên %s", sharedPreferences.getString(SharedPreferenceKeys.USER_NAME, "")));
        binding.quantity.setText(viewModel.getReactCount());
    }

    private List<EReactionType> getTop3Reacts(Map<EReactionType, List<React>> data){
        return data.entrySet()
                .stream()
                .sorted((e1, e2) -> Integer.compare(e1.getValue().size(), e2.getValue().size()))
                .map(Map.Entry::getKey)
                .limit(3)
                .collect(Collectors.toList());
    }

    private void bindTopReactIcon(ImageView icon, EReactionType reactionType){
        switch (reactionType){
            case ANGRY -> {
                loadIcon(icon, R.drawable.react_angry);
            }
            case HAHA -> {
                loadIcon(icon, R.drawable.react_haha);
            }
            case SAD -> {
                loadIcon(icon, R.drawable.react_sad);
            }
            case LIKE -> {
                loadIcon(icon, R.drawable.like_icon);
            }
            case HEART -> {
                loadIcon(icon, R.drawable.reaction_heart);
            }
            case WOW -> {
                loadIcon(icon, R.drawable.react_wow);
            }
        }
    }

    private void loadIcon(ImageView icon, int resource){
        Picasso.get()
                .load(resource)
                .error(R.color.light_gray)
                .placeholder(R.color.light_gray)
                .into(icon);
    }

    private void openGalleryForImageAndVideo() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"video/*", "image/*"});
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        startActivityForResult(Intent.createChooser(intent, "Select Picture or Video"), PICK_IMAGE_VIDEO_REQUEST_CODE);
    }

    private void initObjects(){
        transition = new AutoTransition();
        transition.setDuration(300);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        commentAdapter = new CommentAdapter();
        commentAdapter.setContext(this);
        commentAdapter.setOnReplyClickListener((userName, parentId, parentCommentIndex) -> {
            binding.replyingTips.setVisibility(View.VISIBLE);
            binding.replyingUserName.setText(userName);
            binding.edtContent.setText(StringUtils.getBoldString(userName + " "));

            viewModel.setParentCmtId(parentId);
            viewModel.setReplyCommentIndex(parentCommentIndex);

            KeyBoardUtils.showKeyboard(CommentActivity.this);
        });
    }

    private void initEvents(){
        binding.btnSend.setOnClickListener(this);
        binding.btnUploadImage.setOnClickListener(this);
        binding.delImage.setOnClickListener(this);
        binding.btnCancelReply.setOnClickListener(this);
        binding.reactions.setOnClickListener(this);
        binding.edtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(binding.edtContent.getText().toString().isEmpty()){
                    binding.btnSend.setImageDrawable(getDrawable(R.drawable.send));
                }else{
                    binding.btnSend.setImageDrawable(getDrawable(R.drawable.send_active));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == binding.btnSend){
            String content = binding.edtContent.getText().toString();
            if(!content.isEmpty()){
                progressDialog.setMessage("Đang đăng bình luận...");
                viewModel.addComment(
                        this,
                        sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""),
                        content,
                        progressDialog,
                        msg -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(),
                        cmt -> {
                            if(cmt.getParentId() == null)
                                commentAdapter.addComment(cmt);
                            else if(viewModel.getReplyCommentIndex() != null){
                                commentAdapter.getComments()
                                        .get(viewModel.getReplyCommentIndex())
                                        .getChildren()
                                        .add(cmt);
                                commentAdapter.notifyItemChanged(viewModel.getReplyCommentIndex());
                                binding.replyingTips.setVisibility(View.GONE);

                                viewModel.setReplyCommentIndex(null);
                                viewModel.setParentCmtId(null);
                            }

                            viewModel.setSelectedMediaUri(null);
                            binding.noCommentOverlay.setVisibility(View.GONE);
                            binding.edtContent.setText("");
                            binding.imageView.setImageDrawable(null);
                            binding.pictureWrapper.setVisibility(View.GONE);
                            KeyBoardUtils.hideKeyboard(CommentActivity.this);
                        }
                );
            }
        }else if(view == binding.btnUploadImage){
            openGalleryForImageAndVideo();
        }else if(view == binding.delImage){
            viewModel.setSelectedMediaUri(null);
            TransitionManager.beginDelayedTransition((ViewGroup) binding.commentInput.getParent(), transition);
            binding.pictureWrapper.setVisibility(View.GONE);
            binding.imageView.setImageDrawable(null);
        }else if(view == binding.btnCancelReply){
            binding.replyingTips.setVisibility(View.GONE);
            binding.edtContent.setText("");

            viewModel.setParentCmtId(null);
            viewModel.setReplyCommentIndex(null);
        }else if(view == binding.reactions){
            Intent intent = new Intent(this, ViewReactionsActivity.class);
            intent.putExtra("postId", viewModel.getPostId());
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("totalCmts", commentAdapter.getComments().size());
        intent.putExtra("postIndex", viewModel.getPostIndex());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_VIDEO_REQUEST_CODE && resultCode == RESULT_OK) {
            viewModel.setSelectedMediaUri(null);
            if (data.getData() != null) {
                viewModel.setSelectedMediaUri(data.getData());
            }

            TransitionManager.beginDelayedTransition((ViewGroup) binding.commentInput.getParent(), transition);
            if(viewModel.getSelectedMediaUri() != null){
                binding.pictureWrapper.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(viewModel.getSelectedMediaUri())
                        .error(R.color.light_gray)
                        .placeholder(R.color.light_gray)
                        .resize(300, 300)
                        .into(binding.imageView);
            }else{
                binding.pictureWrapper.setVisibility(View.GONE);
            }
        }
    }
}