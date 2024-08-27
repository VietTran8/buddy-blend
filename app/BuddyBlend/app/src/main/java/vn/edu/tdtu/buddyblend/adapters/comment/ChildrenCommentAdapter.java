package vn.edu.tdtu.buddyblend.adapters.comment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.databinding.ItemViewChildCommentBinding;
import vn.edu.tdtu.buddyblend.models.Comment;
import vn.edu.tdtu.buddyblend.models.MinimizedUser;
import vn.edu.tdtu.buddyblend.ui.activities.UserAccountActivity;
import vn.edu.tdtu.buddyblend.utils.DateUtils;

public class ChildrenCommentAdapter extends RecyclerView.Adapter<ChildrenCommentAdapter.MyViewHolder> {
    private Context context;
    private List<Comment> comments;
    private OnChildReplyClickListener listener;

    //Interfaces
    public interface OnChildReplyClickListener{
        void onClick(String userName);
    }
    //Interfaces

    public void setComments(List<Comment> comments){
        this.comments = comments;
        notifyDataSetChanged();
    }

    public void addComment(Comment comment){
        this.comments.add(0, comment);
        notifyItemInserted(0);
    }

    public void setContext(Context context) {
        this.context = context;
    }
    public void setOnChildReplyClickListener(OnChildReplyClickListener listener){
        this.listener = listener;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemViewChildCommentBinding binding = ItemViewChildCommentBinding.inflate(layoutInflater, parent, false);
        return new MyViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Comment currentComment = comments.get(position);
        holder.bind(currentComment);
    }

    @Override
    public int getItemCount() {
        return comments != null ? comments.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ItemViewChildCommentBinding binding;
        public MyViewHolder(@NonNull View itemView, ItemViewChildCommentBinding binding) {
            super(itemView);
            this.binding = binding;
        }

        public void bind(Comment comment){
            MinimizedUser commentedMinimizedUser = comment.getUser();
            binding.userName.setText(commentedMinimizedUser.getUserFullName());

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
                listener.onClick(commentedMinimizedUser.getUserFullName());
            });

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
        }
    }
}
