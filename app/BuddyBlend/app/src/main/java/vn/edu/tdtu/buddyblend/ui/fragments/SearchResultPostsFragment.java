package vn.edu.tdtu.buddyblend.ui.fragments;

import android.app.Activity;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.adapters.homefragment.PostAdapter;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.components.MyDialog;
import vn.edu.tdtu.buddyblend.databinding.FragmentSearchResultPostsBinding;
import vn.edu.tdtu.buddyblend.dto.request.SharePostReqDTO;
import vn.edu.tdtu.buddyblend.dto.response.SearchResponse;
import vn.edu.tdtu.buddyblend.enums.EPostType;
import vn.edu.tdtu.buddyblend.enums.EPrivacy;
import vn.edu.tdtu.buddyblend.models.PostResponse;
import vn.edu.tdtu.buddyblend.models.User;
import vn.edu.tdtu.buddyblend.ui.activities.CommentActivity;
import vn.edu.tdtu.buddyblend.ui.activities.EditPostActivity;
import vn.edu.tdtu.buddyblend.ui.activities.SelectPrivacyActivity;
import vn.edu.tdtu.buddyblend.ui.viewmodel.HomeViewModel;
import vn.edu.tdtu.buddyblend.ui.viewmodel.factories.HomeViewModelFactory;
import vn.edu.tdtu.buddyblend.utils.LayoutManagerUtil;
import vn.edu.tdtu.buddyblend.utils.SharedPreferenceKeys;

public class SearchResultPostsFragment extends Fragment {
    private FragmentSearchResultPostsBinding binding;
    private SearchResponse searchResponse;
    private PostAdapter postAdapter;
    private HomeViewModel homeViewModel;
    private SharedPreferences sharedPreferences;
    private static final int COMMENT_REQUEST_CODE = 8734873;
    private static final int SELECT_SHARE_PRIVACY_RQ = 234359;
    private static final int EDIT_POST_RQ = 123412;
    public SearchResultPostsFragment(SearchResponse searchResponse) {
        this.searchResponse = searchResponse;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchResultPostsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(SharedPreferenceKeys.USER_SHARED_PREFS, Context.MODE_PRIVATE);

        ViewModelProvider.Factory factory = new HomeViewModelFactory(sharedPreferences, new HomeViewModel.OnCurrentUserFetchedFail() {
            @Override
            public void onFailure(int statusCode, String message) {
            }
        });
        homeViewModel = new ViewModelProvider(getActivity(), factory).get(HomeViewModel.class);

        init();
    }

    private void init() {
        postAdapter = new PostAdapter();
        postAdapter.setHomeViewModel(homeViewModel);
        postAdapter.setContext(getContext());
        postAdapter.setFragmentManager(getParentFragmentManager());
        postAdapter.setLayoutInflater(getLayoutInflater());
        postAdapter.setCurrentUser(getCurrentUser());
        postAdapter.setOnSharePostButtonClickListener((v, postId, status, bottomSheet) -> {
            SharePostReqDTO reqDTO = new SharePostReqDTO();
            reqDTO.setPostId(postId);
            reqDTO.setPrivacy(homeViewModel.getCurrentPrivacy());
            reqDTO.setStatus(status);

            ProgressDialog dialog = new ProgressDialog(getContext());
            dialog.setCancelable(false);
            dialog.setMessage("Đang chia sẻ");
            dialog.show();

            homeViewModel.sharePost(
                    sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""),
                    reqDTO,
                    new ActionCallback<PostResponse>() {
                        @Override
                        public void onSuccess(PostResponse postResponse) {
                            Toast.makeText(getContext(), "Đã chia sẻ bài viết!", Toast.LENGTH_LONG).show();
                            bottomSheet.dismiss();
                            dialog.dismiss();
                        }

                        @Override
                        public void onFailure(String message) {
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
            );
        });

        postAdapter.setOnActionButtonClickListener(new PostAdapter.OnActionButtonClickListener() {
            @Override
            public void onEditClick(PostResponse post, User user, int position) {
                Intent intent = new Intent(getContext(), EditPostActivity.class);
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
                MyDialog myDialog = new MyDialog(getContext(), getLayoutInflater(), R.layout.dialog_confirm, true, new MyDialog.Handler() {
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

                myDialog.show(getParentFragmentManager(), "DELETE_POST_DIALOG");
            }
        });

        postAdapter.setOnPrivacyButtonClickListener(v -> {
            homeViewModel.setBtnPrivacy(v);
            Intent intent = new Intent(getContext(), SelectPrivacyActivity.class);
            intent.putExtra("currentPrivacy", homeViewModel.getCurrentPrivacy().name());
            startActivityForResult(intent, SELECT_SHARE_PRIVACY_RQ);
        });
        postAdapter.setOnCommentButtonClickListener((v, quantity, post, index) -> {
            Intent intent = new Intent(getContext(), CommentActivity.class);
            intent.putExtra("postId",
                    post.getType().equals(EPostType.SHARE) ? post.getShareInfo().getId() : post.getId());
            intent.putExtra("postIndex", index);
            intent.putExtra("userName", post.getUser().getUserFullName());
            intent.putExtra("reactCount", quantity);
            startActivityForResult(intent, COMMENT_REQUEST_CODE);
        });
        binding.rcvPosts.setAdapter(postAdapter);
        binding.rcvPosts.setLayoutManager(LayoutManagerUtil.disabledScrollLinearManager(getContext(), LinearLayoutManager.VERTICAL));

        postAdapter.setPosts(searchResponse.getPosts());
    }

    private User getCurrentUser(){
        User user = new User();
        user.setUserFullName(sharedPreferences.getString(SharedPreferenceKeys.USER_NAME, ""));
        user.setProfilePicture(sharedPreferences.getString(SharedPreferenceKeys.USER_IMAGE, ""));
        user.setId(sharedPreferences.getString(SharedPreferenceKeys.USER_ID, ""));

        return user;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == COMMENT_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            int totalCmts = data.getIntExtra("totalCmts", 0);
            int postIndex = data.getIntExtra("postIndex", 0);

            postAdapter.getPosts()
                    .get(postIndex)
                    .setNoComments(totalCmts);
            postAdapter.notifyItemChanged(postIndex);
        }else if(requestCode == SELECT_SHARE_PRIVACY_RQ && resultCode == Activity.RESULT_OK) {
            ImageView privacyIcon = homeViewModel.getBtnPrivacy().findViewById(R.id.privacyIcon);
            TextView privacyText = homeViewModel.getBtnPrivacy().findViewById(R.id.privacyText);

            homeViewModel.setCurrentPrivacy(EPrivacy.valueOf(data.getStringExtra("privacy")));
            switch (homeViewModel.getCurrentPrivacy()) {
                case PUBLIC -> {
                    privacyIcon.setImageDrawable(getContext().getDrawable(R.drawable.baseline_public_24));
                    privacyText.setText("Công khai");
                }case PRIVATE -> {
                    privacyIcon.setImageDrawable(getContext().getDrawable(R.drawable.baseline_lock_24));
                    privacyText.setText("Chỉ mình tôi");
                }case ONLY_FRIENDS -> {
                    privacyIcon.setImageDrawable(getContext().getDrawable(R.drawable.baseline_people_alt_24));
                    privacyText.setText("Bạn bè");
                }
            }
        }else if (requestCode == EDIT_POST_RQ && resultCode == Activity.RESULT_OK) {
            int position = data.getIntExtra("position", -1);
            PostResponse updatedPost = (PostResponse) data.getSerializableExtra("updatedPost");
            postAdapter.updatePost(updatedPost, position);
            Snackbar.make(binding.getRoot(), "Cập nhật bài viết thành công!", Snackbar.LENGTH_LONG).show();
        }
    }
}