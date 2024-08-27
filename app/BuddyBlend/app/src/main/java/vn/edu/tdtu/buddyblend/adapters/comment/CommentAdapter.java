package vn.edu.tdtu.buddyblend.adapters.comment;

import android.content.Context;
import android.content.Intent;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.databinding.ItemViewPostCommentBinding;
import vn.edu.tdtu.buddyblend.models.Comment;
import vn.edu.tdtu.buddyblend.models.MinimizedUser;
import vn.edu.tdtu.buddyblend.ui.activities.UserAccountActivity;
import vn.edu.tdtu.buddyblend.utils.DateUtils;
import vn.edu.tdtu.buddyblend.utils.LayoutManagerUtil;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {
    private Context context;
    private List<Comment> comments;
    private OnReplyClickListener listener;

    public void setComments(List<Comment> comments){
        this.comments = comments;
        notifyDataSetChanged();
    }

    public List<Comment> getComments() {
        return comments != null ? comments : new ArrayList<>();
    }

    public void addComment(Comment comment){
        this.comments.add(0, comment);
        notifyItemInserted(0);
        notifyDataSetChanged();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemViewPostCommentBinding binding = ItemViewPostCommentBinding.inflate(layoutInflater, parent, false);
        return new MyViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Comment currentComment = comments.get(position);
        holder.bind(currentComment, position);
    }

    @Override
    public int getItemCount() {
        return comments != null ? comments.size() : 0;
    }

    public interface OnReplyClickListener{
        void onClick(String userName, String parentId, int index);
    }
    public void setOnReplyClickListener(OnReplyClickListener listener){
        this.listener = listener;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemViewPostCommentBinding binding;
        public MyViewHolder(@NonNull View itemView, ItemViewPostCommentBinding binding) {
            super(itemView);
            this.binding = binding;
        }
        public void bind(Comment comment, int parentCommentIndex) {
            bindChildrenComments(comment.getChildren(), comment.getId(), parentCommentIndex);
            bindComment(comment, parentCommentIndex);
        }
        private void bindChildrenComments(List<Comment> childrenComments, String parentCommentId, int parentCommentIndex){
            String viewReplyTextContent = String.format("Xem tất cả %d phản hồi", childrenComments.size());

            ChildrenCommentAdapter adapter = new ChildrenCommentAdapter();

            adapter.setOnChildReplyClickListener(userName -> {
                listener.onClick(userName, parentCommentId, parentCommentIndex);
            });

            binding.rcvChildComments.setAdapter(adapter);
            binding.rcvChildComments.setLayoutManager(LayoutManagerUtil.disabledScrollLinearManager(context, LinearLayoutManager.VERTICAL));
            binding.childComments.setVisibility(View.GONE);

            Transition transition = new AutoTransition();
            transition.setDuration(100);
            transition.setStartDelay(0);
            binding.btnViewReply.setOnClickListener(view -> {
                TransitionManager.beginDelayedTransition((ViewGroup) binding.childComments.getParent(), transition);
                if(binding.childComments.getVisibility() == View.GONE){
                    binding.childComments.setVisibility(View.VISIBLE);
                    binding.btnViewReply.setText("Đóng");
                    adapter.setComments(childrenComments);
                }else{
                    binding.childComments.setVisibility(View.GONE);
                    binding.btnViewReply.setText(viewReplyTextContent);
                }
            });
        }
        private void bindComment(Comment comment, int parentCommentIndex){
            if(comment.getChildren().size() > 0){
                binding.btnViewReply.setVisibility(View.VISIBLE);
                String btnViewReplyTextContent = String.format("Xem tất cả %d phản hồi", comment.getChildren().size());
                binding.btnViewReply.setText(btnViewReplyTextContent);
            }else{
                binding.btnViewReply.setVisibility(View.GONE);
            }

            if(comment.getImageUrls() != null && !comment.getImageUrls().isEmpty()){
                binding.imgWrapper.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(comment.getImageUrls().get(0))
                        .error(R.color.light_gray)
                        .placeholder(R.color.light_gray)
                        .into(binding.commentImg);
            }else{
                binding.imgWrapper.setVisibility(View.GONE);
            }

            binding.userName.setOnClickListener(v -> {
                Intent intent = new Intent(context, UserAccountActivity.class);
                intent.putExtra("userId", comment.getUser().getId());
                context.startActivity(intent);
            });

            binding.imgAvatar.setOnClickListener(v -> {
                Intent intent = new Intent(context, UserAccountActivity.class);
                intent.putExtra("userId", comment.getUser().getId());
                context.startActivity(intent);
            });

            MinimizedUser commentedMinimizedUser = comment.getUser();
            binding.userName.setText(commentedMinimizedUser.getUserFullName());

            String profileUrl = commentedMinimizedUser.getProfilePicture();
            if(profileUrl == null || profileUrl.isEmpty()){
                Picasso.get()
                        .load(R.drawable.user)
                        .placeholder(R.color.light_gray)
                        .error(R.color.light_gray)
                        .into(binding.imgAvatar);
            }else
                Picasso.get()
                        .load(profileUrl)
                        .placeholder(R.color.light_gray)
                        .error(R.color.light_gray)
                        .into(binding.imgAvatar);

            binding.createdAt.setText(DateUtils.getTimeAgo(DateUtils.stringToDate(comment.getCreatedAt())));
            binding.content.setText(comment.getContent());
            binding.btnReply.setOnClickListener(view -> {
                listener.onClick(comment.getUser().getUserFullName(), comment.getId(), parentCommentIndex);
            });
        }
    }
}
