package vn.edu.tdtu.buddyblend.adapters.homefragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.components.MyDialog;
import vn.edu.tdtu.buddyblend.databinding.ItemViewPostBinding;
import vn.edu.tdtu.buddyblend.databinding.ItemViewSharePostBinding;
import vn.edu.tdtu.buddyblend.enums.EPostType;
import vn.edu.tdtu.buddyblend.enums.EReactionType;
import vn.edu.tdtu.buddyblend.models.PostResponse;
import vn.edu.tdtu.buddyblend.models.TopReacts;
import vn.edu.tdtu.buddyblend.models.User;
import vn.edu.tdtu.buddyblend.ui.activities.UserAccountActivity;
import vn.edu.tdtu.buddyblend.ui.activities.ViewTaggingUsersActivity;
import vn.edu.tdtu.buddyblend.ui.components.BottomSheet;
import vn.edu.tdtu.buddyblend.ui.viewmodel.HomeViewModel;
import vn.edu.tdtu.buddyblend.utils.DateUtils;
import vn.edu.tdtu.buddyblend.utils.LayoutManagerUtil;
import vn.edu.tdtu.buddyblend.utils.PopupMenuUtils;
import vn.edu.tdtu.buddyblend.utils.StringUtils;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String TAG = "PostAdapter";
    private Context context;
    private List<PostResponse> posts = new ArrayList<>();
    private HomeViewModel homeViewModel;
    private FragmentManager fragmentManager;
    private LayoutInflater layoutInflater;
    private OnCommentButtonClickListener onCommentButtonClickListener;
    private OnPrivacyButtonClickListener onPrivacyButtonClickListener;
    private OnSharePostButtonClickListener onSharePostButtonClickListener;
    private OnActionButtonClickListener onActionButtonClickListener;
    private User currentUser;

    public interface OnCommentButtonClickListener{
        void onClick(View view, String reactQuantity, PostResponse post, int index);
    }

    public interface OnPrivacyButtonClickListener{
        void onClick(View view);
    }

    public void setOnActionButtonClickListener(OnActionButtonClickListener onActionButtonClickListener) {
        this.onActionButtonClickListener = onActionButtonClickListener;
    }

    public interface OnSharePostButtonClickListener{
        void onClick(View view, String postId, String status, BottomSheet bottomSheet);
    }

    public OnSharePostButtonClickListener getOnSharePostButtonClickListener() {
        return onSharePostButtonClickListener;
    }

    public void setOnSharePostButtonClickListener(OnSharePostButtonClickListener onSharePostButtonClickListener) {
        this.onSharePostButtonClickListener = onSharePostButtonClickListener;
    }

    public List<PostResponse> getPosts() {
        return posts;
    }

    public void setOnCommentButtonClickListener(OnCommentButtonClickListener onCommentButtonClickListener) {
        this.onCommentButtonClickListener = onCommentButtonClickListener;
    }

    public OnPrivacyButtonClickListener getOnPrivacyButtonClickListener() {
        return onPrivacyButtonClickListener;
    }

    public void setOnPrivacyButtonClickListener(OnPrivacyButtonClickListener onPrivacyButtonClickListener) {
        this.onPrivacyButtonClickListener = onPrivacyButtonClickListener;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void setHomeViewModel(HomeViewModel homeViewModel) {
        this.homeViewModel = homeViewModel;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setPosts(List<PostResponse> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    public void addPost(PostResponse postResponse){
        this.posts.add(postResponse);
        notifyDataSetChanged();
    }

    public LayoutInflater getLayoutInflater() {
        return layoutInflater;
    }

    public void setLayoutInflater(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void addAllPosts(List<PostResponse> posts) {
        this.posts.addAll(posts != null ? posts : new ArrayList<>());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType){
            case -1 -> {
                ItemViewSharePostBinding binding = ItemViewSharePostBinding.inflate(inflater, parent, false);
                return new PostShareViewHolder(binding.getRoot(), binding);
            }
            default -> {
                ItemViewPostBinding binding = ItemViewPostBinding.inflate(inflater, parent, false);
                return new PostViewHolder(binding.getRoot(), binding);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: " + posts.size());
        PostResponse currentPost = posts.get(position);
        int mediaCount = currentPost.getImageUrls().size() + currentPost.getVideoUrls().size();
        if(currentPost.getType().equals(EPostType.SHARE))
            mediaCount = -1;

        switch(mediaCount) {
            case -1:
                ((PostShareViewHolder) holder).bind(currentPost, position);
                break;
            default:
                ((PostViewHolder) holder).bind(currentPost, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return posts != null ? posts.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        PostResponse currentPost = posts.get(position);
        if(currentPost.getType().equals(EPostType.SHARE))
            return -1;

        int mediaCount = currentPost.getImageUrls().size() + currentPost.getVideoUrls().size();
        return mediaCount;
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        private ItemViewPostBinding binding;

        public PostViewHolder(@NonNull View itemView, ItemViewPostBinding binding) {
            super(itemView);
            this.binding = binding;
        }

        private void displayDivider(boolean isDisplay) {
            if(isDisplay) {
                binding.divider.setVisibility(View.VISIBLE);
            }
            binding.divider.setVisibility(View.GONE);
        }

        public void bind(PostResponse post, int position){
            int totalColumns = 2;
            PostPictureAdapter adapter = new PostPictureAdapter(post, context);

            GridLayoutManager gridLayoutManager = LayoutManagerUtil.disabledScrollGridManager(context, totalColumns);

            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int size = post.getImageUrls().size() + post.getVideoUrls().size();

                    switch (size) {
                        case 1:
                            return totalColumns;
                        case 3:
                            if (position < 2) return 1;
                            else return totalColumns;
                        default:
                            return 1;
                    }
                }
            });

            displayDivider((post.getVideoUrls().size() + post.getImageUrls().size()) > 0);
            setAvatarAndUsernameClick(binding.userAvatar, binding.userName, post.getUser().getId());
            binding.rcvPictures.setLayoutManager(gridLayoutManager);
            binding.rcvPictures.setAdapter(adapter);

            bindUserAvatar(post.getUser().getProfilePicture(), binding.userAvatar);

            binding.userName.setText(post.getUser().getUserFullName());
            binding.createdAt.setText(DateUtils.getTimeAgo(DateUtils.stringToDate(post.getCreatedAt())));

            binding.postContent.setText(post.getContent());

            switch (post.getPrivacy()){
                case PRIVATE -> {
                    binding.privacy.setImageResource(R.drawable.baseline_lock_24);
                }case PUBLIC -> {
                    binding.privacy.setImageResource(R.drawable.baseline_public_24);
                }case ONLY_FRIENDS -> {
                    binding.privacy.setImageResource(R.drawable.baseline_people_alt_24);
                }
            }

            if(post.getNoReactions() == 1 && post.getReacted() != null){
                binding.reactionCount.setText("Bạn");
            }else if(post.getNoReactions() > 1 && post.getReacted() != null){
                binding.reactionCount.setText("Bạn và " + (post.getNoReactions() - 1) + " người khác");
            }else{
                if(post.getNoReactions() > 0){
                    binding.reactionCount.setVisibility(View.VISIBLE);
                    binding.reactionCount.setText(post.getNoReactions() + "");
                }else
                    binding.reactionCount.setVisibility(View.INVISIBLE);
            }
            binding.commentCount.setText(post.getNoComments() + " bình luận");
            binding.shareCount.setText(post.getNoShared() + " lượt chia sẻ");

            bindReactions(post, binding.firstIcon, binding.secondIcon, binding.thirdIcon);

            setOnLongClick(binding.btnReact, binding.reactionCount, post, binding.firstIcon, binding.secondIcon, binding.thirdIcon);
            setShareBtnOnClick(binding.btnShare, post, position);
            setCommentBtnOnClick(binding.btnComment, binding.reactionCount, post, position);
            setOnActionClick(post, binding.btnAction, position);
            setBtnOnClick(binding.btnReact, binding.reactionCount, post, binding.firstIcon, binding.secondIcon, binding.thirdIcon);
            bindReactBtn(binding.btnReact, post.getReacted());
        }
    }

    class PostShareViewHolder extends RecyclerView.ViewHolder {
        private ItemViewSharePostBinding binding;

        public PostShareViewHolder(@NonNull View itemView, ItemViewSharePostBinding binding) {
            super(itemView);
            this.binding = binding;
        }

        public void bind(PostResponse post, int position){
            int totalColumns = 2;
            PostPictureAdapter adapter = new PostPictureAdapter(post, context);

            GridLayoutManager gridLayoutManager = LayoutManagerUtil.disabledScrollGridManager(context, totalColumns);

            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int size = post.getImageUrls().size() + post.getVideoUrls().size();

                    switch (size) {
                        case 1:
                            return totalColumns;
                        case 3:
                            if (position < 2) return 1;
                            else return totalColumns;
                        default:
                            return 1;
                    }
                }
            });

            setAvatarAndUsernameClick(binding.userAvatar, binding.userName, post.getShareInfo().getSharedUser().getId());
            binding.rcvPictures.setLayoutManager(gridLayoutManager);
            binding.rcvPictures.setAdapter(adapter);

            bindUserAvatar(post.getUser().getProfilePicture(), binding.shareUserAvatar);
            bindUserAvatar(post.getShareInfo().getSharedUser().getProfilePicture(), binding.userAvatar);

            bindUserName(binding.sharedUserName, post);
            binding.userName.setText(post.getShareInfo().getSharedUser().getUserFullName());
            binding.createdAt.setText(DateUtils.getTimeAgo(DateUtils.stringToDate(post.getCreatedAt())));

            binding.postContent.setText(post.getShareInfo().getStatus());
            binding.sharedPostContent.setText(post.getContent());

            binding.shareUserAvatar.setOnClickListener(v -> {
                Intent intent = new Intent(context, UserAccountActivity.class);
                intent.putExtra("userId", post.getUser().getId());
                context.startActivity(intent);
            });
            binding.sharedUserName.setOnClickListener(v -> {
                Intent intent = new Intent(context, UserAccountActivity.class);
                intent.putExtra("userId", post.getUser().getId());
                context.startActivity(intent);
            });

            switch (post.getPrivacy()){
                case PRIVATE -> {
                    binding.sharedPrivacy.setImageResource(R.drawable.baseline_lock_24);
                }case PUBLIC -> {
                    binding.sharedPrivacy.setImageResource(R.drawable.baseline_public_24);
                }case ONLY_FRIENDS -> {
                    binding.sharedPrivacy.setImageResource(R.drawable.baseline_people_alt_24);
                }
            }

            switch (post.getShareInfo().getPrivacy()){
                case PRIVATE -> {
                    binding.privacy.setImageResource(R.drawable.baseline_lock_24);
                }case PUBLIC -> {
                    binding.privacy.setImageResource(R.drawable.baseline_public_24);
                }case ONLY_FRIENDS -> {
                    binding.privacy.setImageResource(R.drawable.baseline_people_alt_24);
                }
            }

            if(post.getNoReactions() == 1 && post.getReacted() != null){
                binding.reactionCount.setText("Bạn");
            }else if(post.getNoReactions() > 1 && post.getReacted() != null){
                binding.reactionCount.setText("Bạn và " + (post.getNoReactions() - 1) + " người khác");
            }else{
                if(post.getNoReactions() > 0){
                    binding.reactionCount.setVisibility(View.VISIBLE);
                    binding.reactionCount.setText(post.getNoReactions() + "");
                }else
                    binding.reactionCount.setVisibility(View.INVISIBLE);
            }
            binding.commentCount.setText(post.getNoComments() + " bình luận");
            binding.shareCount.setText(post.getNoShared() + " lượt chia sẻ");

            bindReactions(post, binding.firstIcon, binding.secondIcon, binding.thirdIcon);

            setOnLongClick(binding.btnReact, binding.reactionCount, post, binding.firstIcon, binding.secondIcon, binding.thirdIcon);
            setShareBtnOnClick(binding.btnShare, post, position);
            setCommentBtnOnClick(binding.btnComment, binding.reactionCount, post, position);
            setOnActionClick(post, binding.btnAction, position);
            setBtnOnClick(binding.btnReact, binding.reactionCount, post, binding.firstIcon, binding.secondIcon, binding.thirdIcon);
            bindReactBtn(binding.btnReact, post.getReacted());
        }
    }
    private void bindUserAvatar(String profilePictureUrl, ImageView userAvatar) {
        if (profilePictureUrl == null || profilePictureUrl.isEmpty()) {
            Picasso.get()
                    .load(R.drawable.user)
                    .placeholder(R.color.light_gray)
                    .resize(1024, 768)
                    .centerCrop()
                    .into(userAvatar);
        } else {
            Picasso.get()
                    .load(profilePictureUrl)
                    .error(R.drawable.user)
                    .placeholder(R.color.light_gray)
                    .resize(1024, 768)
                    .centerCrop()
                    .into(userAvatar);
        }
    }

    public interface OnActionButtonClickListener {
        void onEditClick(PostResponse post, User user, int position);
        void onDeleteClick(String postId, int position);
    }

    public void deletePost(int position) {
        if(position >= 0) {
            this.posts.remove(position);

            notifyDataSetChanged();
        }
    }

    public void updatePost(PostResponse postResponse, int position) {
        if(position >= 0) {
            PostResponse selectedPost = posts.get(position);
            if (!postResponse.getType().equals(EPostType.SHARE)) {
                selectedPost.setContent(postResponse.getContent());
                selectedPost.setPrivacy(postResponse.getPrivacy());
                selectedPost.setVideoUrls(postResponse.getVideoUrls());
                selectedPost.setImageUrls(postResponse.getImageUrls());
                selectedPost.setTaggedUsers(postResponse.getTaggedUsers());
            }else{
                selectedPost.getShareInfo().setStatus(postResponse.getShareInfo().getStatus());
                selectedPost.getShareInfo().setPrivacy(postResponse.getShareInfo().getPrivacy());
            }

            notifyItemChanged(position);
        }
    }

    private void setOnActionClick(PostResponse post, View view, int position){
        PopupMenuUtils popupMenuUtils;
        Log.d(TAG, "bind: " + post.isMine());
        if((post.getType().equals(EPostType.SHARE) &&
                        post.getShareInfo().getSharedUser().getId().equals(currentUser.getId())) || !(post.getType().equals(EPostType.SHARE) && post.isMine())){
            popupMenuUtils = new PopupMenuUtils(context, R.menu.menu_my_post_action, menuItem -> {
                int id = menuItem.getItemId();
                if(id == R.id.itemEditPost){
                    onActionButtonClickListener.onEditClick(post, currentUser, position);
                }else if(id == R.id.itemDelPost){
                    onActionButtonClickListener.onDeleteClick(post.getType().equals(EPostType.SHARE) ? post.getShareInfo().getId() : post.getId(),
                            position);
                }
            });
        }else{
            popupMenuUtils = new PopupMenuUtils(context, R.menu.menu_post_action, menuItem -> {
                int id = menuItem.getItemId();
                if(id == R.id.itemFavourite){
                    Toast.makeText(context, "Favourite", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.itemReport){
                    Toast.makeText(context, "Report", Toast.LENGTH_SHORT).show();
                }
            });
        }

        view.setOnClickListener(v -> {
            popupMenuUtils.setView(v);
            popupMenuUtils.show();
        });
    }

    private void bindReactBtn(View btn, EReactionType type){
        ImageView icon = btn.findViewById(R.id.reactIcon);
        TextView title = btn.findViewById(R.id.reactTitle);
        if(type != null){
            switch (type){
                case LIKE -> {
                    Picasso.get()
                            .load(R.drawable.like_icon)
                            .into(icon);
                    title.setTextColor(context.getColor(R.color.like));
                    title.setText(R.string.like_vn);
                }case HEART  -> {
                    Picasso.get()
                            .load(R.drawable.reaction_heart)
                            .into(icon);
                    title.setTextColor(context.getColor(R.color.heart));
                    title.setText(R.string.heart_vn);
                }case WOW -> {
                    Picasso.get()
                            .load(R.drawable.react_wow)
                            .into(icon);
                    title.setTextColor(context.getColor(R.color.wow));
                    title.setText(R.string.wow_vn);
                }case HAHA -> {
                    Picasso.get()
                            .load(R.drawable.react_haha)
                            .into(icon);
                    title.setTextColor(context.getColor(R.color.haha));
                    title.setText(R.string.haha_vn);
                }case SAD -> {
                    Picasso.get()
                            .load(R.drawable.react_sad)
                            .into(icon);
                    title.setTextColor(context.getColor(R.color.sad));
                    title.setText(R.string.sad_vn);
                }case ANGRY -> {
                    Picasso.get()
                            .load(R.drawable.react_angry)
                            .into(icon);
                    title.setTextColor(context.getColor(R.color.angry));
                    title.setText(R.string.angry_vn);
                }
            }
        }else{
            Picasso.get()
                    .load(R.drawable.not_like)
                    .into(icon);
            title.setTextColor(context.getColor(R.color.gray));
            title.setText(R.string.like_vn);
        }
    }

    private void setBtnOnClick(View btn, TextView totalReacted, PostResponse postResponse, ImageView... icons){
        btn.setOnClickListener(view -> {
            ImageView icon = btn.findViewById(R.id.reactIcon);
            TextView title = btn.findViewById(R.id.reactTitle);
            EReactionType type = postResponse.getReacted();
            String postId = postResponse.getId();

            if(postResponse.getType().equals(EPostType.SHARE))
                postId = postResponse.getShareInfo().getId();

            if(type == null){
                homeViewModel.doReact(postId, EReactionType.LIKE, new ActionCallback<List<TopReacts>>() {
                    @Override
                    public void onSuccess(List<TopReacts> topReacts) {
                        postResponse.setTopReacts(topReacts);
                        bindReactions(topReacts, totalReacted, icons);

                        Picasso.get()
                                .load(R.drawable.like_icon)
                                .into(icon);
                        title.setTextColor(context.getColor(R.color.like));
                        postResponse.setReacted(EReactionType.LIKE);
                        increaseReactCount(totalReacted, postResponse);
                    }
                });
            }else{
                homeViewModel.doReact(postId, type, new ActionCallback<List<TopReacts>>() {
                    @Override
                    public void onSuccess(List<TopReacts> topReacts) {
                        postResponse.setTopReacts(topReacts);
                        bindReactions(topReacts, totalReacted, icons);

                        Picasso.get()
                                .load(R.drawable.not_like)
                                .into(icon);
                        title.setTextColor(context.getColor(R.color.gray));
                        postResponse.setReacted(null);
                        decreaseReactCount(totalReacted, postResponse);
                    }
                });
            }
            title.setText(R.string.like_vn);
        });
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

    private void setCommentBtnOnClick(View btn, TextView reactQuantity, PostResponse post, int index){
        btn.setOnClickListener(v -> {
            onCommentButtonClickListener.onClick(v, reactQuantity.getText().toString(), post, index);
        });
    }

    private void setOnCmtReactionLongClick() {

    }

    private void setOnLongClick(View btn, TextView totalReacted, PostResponse postResponse, ImageView... icons){
        btn.setOnLongClickListener(view -> {
            MyDialog myDialog = new MyDialog(context, LayoutInflater.from(context), R.layout.dialog_react, true, new MyDialog.Handler() {
                @Override
                public void handle(AlertDialog dialog, View contentView) {
                    ImageView btnLike = contentView.findViewById(R.id.btnLike);
                    ImageView btnWow = contentView.findViewById(R.id.btnWow);
                    ImageView btnAngry = contentView.findViewById(R.id.btnAngry);
                    ImageView btnHeart = contentView.findViewById(R.id.btnHeart);
                    ImageView btnHaha = contentView.findViewById(R.id.btnHaha);
                    ImageView btnSad = contentView.findViewById(R.id.btnSad);

                    ImageView[] buttons = new ImageView[] {
                            btnLike, btnWow, btnHeart, btnHaha, btnAngry, btnSad
                    };

                    EReactionType[] types = new EReactionType[] {
                            EReactionType.LIKE, EReactionType.WOW,
                            EReactionType.HEART, EReactionType.HAHA,
                            EReactionType.ANGRY, EReactionType.SAD
                    };

                    for(int i = 0; i < buttons.length; i++){
                        ImageView currentBtn = buttons[i];
                        int tempI = i;
                        EReactionType type = types[tempI];
                        currentBtn.setOnClickListener(view -> {
                            String postId = postResponse.getId();
                            if(postResponse.getType().equals(EPostType.SHARE)) {
                                postId = postResponse.getShareInfo().getId();
                            }

                            homeViewModel.doReact(postId, type, new ActionCallback<List<TopReacts>>() {
                                @Override
                                public void onSuccess(List<TopReacts> topReacts) {
                                    postResponse.setTopReacts(topReacts);
                                    bindReactions(topReacts, totalReacted, icons);

                                    ImageView icon = btn.findViewById(R.id.reactIcon);
                                    TextView title = btn.findViewById(R.id.reactTitle);
                                    if(postResponse.getReacted() != type){
                                        switch (type){
                                            case LIKE -> {
                                                Picasso.get()
                                                        .load(R.drawable.like_icon)
                                                        .into(icon);
                                                title.setTextColor(context.getColor(R.color.like));
                                                title.setText(R.string.like_vn);
                                            }case HEART  -> {
                                                Picasso.get()
                                                        .load(R.drawable.reaction_heart)
                                                        .into(icon);
                                                title.setTextColor(context.getColor(R.color.heart));
                                                title.setText(R.string.heart_vn);
                                            }case WOW -> {
                                                Picasso.get()
                                                        .load(R.drawable.react_wow)
                                                        .into(icon);
                                                title.setTextColor(context.getColor(R.color.wow));
                                                title.setText(R.string.wow_vn);
                                            }case HAHA -> {
                                                Picasso.get()
                                                        .load(R.drawable.react_haha)
                                                        .into(icon);
                                                title.setTextColor(context.getColor(R.color.haha));
                                                title.setText(R.string.haha_vn);
                                            }case SAD -> {
                                                Picasso.get()
                                                        .load(R.drawable.react_sad)
                                                        .into(icon);
                                                title.setTextColor(context.getColor(R.color.sad));
                                                title.setText(R.string.sad_vn);
                                            }case ANGRY -> {
                                                Picasso.get()
                                                        .load(R.drawable.react_angry)
                                                        .into(icon);
                                                title.setTextColor(context.getColor(R.color.angry));
                                                title.setText(R.string.angry_vn);
                                            }
                                        }
                                        if(postResponse.getReacted() == null){
                                            increaseReactCount(totalReacted, postResponse);
                                        }
                                        postResponse.setReacted(type);
                                    }else{
                                        postResponse.setReacted(null);
                                        title.setTextColor(context.getColor(R.color.gray));
                                        title.setText(R.string.like_vn);
                                        Picasso.get()
                                                .load(R.drawable.not_like)
                                                .into(icon);
                                        decreaseReactCount(totalReacted, postResponse);
                                    }

                                    dialog.dismiss();
                                }
                            });
                        });
                    }
                }
            });
            myDialog.show(fragmentManager, "asd");

            return true;
        });
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

    private void bindReactIcon(ImageView icon, EReactionType type){
        switch (type){
            case LIKE -> {
                Picasso.get()
                        .load(R.drawable.like_icon)
                        .into(icon);
            }case HEART  -> {
                Picasso.get()
                        .load(R.drawable.reaction_heart)
                        .into(icon);
            }case WOW -> {
                Picasso.get()
                        .load(R.drawable.react_wow)
                        .into(icon);
            }case HAHA -> {
                Picasso.get()
                        .load(R.drawable.react_haha)
                        .into(icon);
            }case SAD -> {
                Picasso.get()
                        .load(R.drawable.react_sad)
                        .into(icon);
            }case ANGRY -> {
                Picasso.get()
                        .load(R.drawable.react_angry)
                        .into(icon);
            }
        }
    }

    private void bindUserName(TextView txtUserName, PostResponse post) {
        String userNameText = post.getUserFullName();
        SpannableString userName;

        ClickableSpan clickableUserName = StringUtils.getClickableSpan(context, view -> {
            Toast.makeText(context, "userName clicked", Toast.LENGTH_SHORT).show();
        });

        if (post.getTaggedUsers() != null && !post.getTaggedUsers().isEmpty()) {
            ClickableSpan clickableFirstTagUser = StringUtils.getClickableSpan(context, view -> {
                Toast.makeText(context, "first tag user clicked", Toast.LENGTH_SHORT).show();
            });

            ClickableSpan clickableOtherTagUsers = StringUtils.getClickableSpan(context, view -> {
                Intent intent = new Intent(context, ViewTaggingUsersActivity.class);
                Gson gson = new Gson();

                String encodedTaggingUsers = gson.toJson(post.getTaggedUsers());
                intent.putExtra("users", encodedTaggingUsers);

                context.startActivity(intent);
            });

            String with = " - cùng với ";
            String firstTagUserFullName = post.getTaggedUsers().get(0).getUserFullName() != null ? post.getTaggedUsers().get(0).getUserFullName() : "";
            userNameText += with + firstTagUserFullName;

            int startFirstTagIndex = post.getUserFullName().length() + with.length();

            if (post.getTaggedUsers().size() == 1) {
                userName = new SpannableString(userNameText);
                userName.setSpan(clickableFirstTagUser, startFirstTagIndex, startFirstTagIndex + firstTagUserFullName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                String and = " và ";
                String others = (post.getTaggedUsers().size() - 1) + " người khác.";
                userNameText += and + others;
                userName = new SpannableString(userNameText);

                int endFirstTagIndex = startFirstTagIndex + firstTagUserFullName.length();
                int startOthersIndex = endFirstTagIndex + and.length();

                userName.setSpan(clickableFirstTagUser, startFirstTagIndex, endFirstTagIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                userName.setSpan(clickableOtherTagUsers, startOthersIndex, startOthersIndex + others.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            userName.setSpan(clickableUserName, 0, post.getUserFullName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            userName = new SpannableString(userNameText);
            userName.setSpan(clickableUserName, 0, post.getUserFullName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        txtUserName.setText(userName);
        txtUserName.setMovementMethod(LinkMovementMethod.getInstance());
        txtUserName.setHighlightColor(0x00000000);
    }

    private void setShareBtnOnClick(View shareButton, PostResponse post, int index){
        BottomSheet bottomSheet = new BottomSheet(context, getLayoutInflater(), R.layout.bottom_sheet_share_post, "Chia sẻ bài viết");

        shareButton.setOnClickListener(v -> {
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
                    Glide.with(context)
                            .load(userAvatarUrl)
                            .error(R.drawable.user)
                            .placeholder(R.color.light_gray)
                            .into(userAvatar);
                }else {
                    Glide.with(context)
                            .load(R.drawable.user)
                            .error(R.drawable.user)
                            .placeholder(R.color.light_gray)
                            .into(userAvatar);
                }

                setPrivacyOnClick(btnPrivacy);

                userFullName.setText(currentUser.getUserFullName());

                btnShare.setOnClickListener(view -> {
                    String status = edtContent.getText().toString();
                    String postId = post.getId();

                    onSharePostButtonClickListener.onClick(btnShare, postId, status, bottomSheet);
                });
            }));

            bottomSheet.show();
        });
    }
    private void setPrivacyOnClick(View btnPrivacy) {
        btnPrivacy.setOnClickListener(v -> {
            onPrivacyButtonClickListener.onClick(btnPrivacy);
        });
    }

    private void setAvatarAndUsernameClick(View avatar, View username, String userId) {
        avatar.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserAccountActivity.class);
            intent.putExtra("userId", userId);
            context.startActivity(intent);
        });

        username.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserAccountActivity.class);
            intent.putExtra("userId", userId);
            context.startActivity(intent);
        });
    }
}