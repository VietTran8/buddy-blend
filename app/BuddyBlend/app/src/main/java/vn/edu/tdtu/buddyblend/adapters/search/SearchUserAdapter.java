package vn.edu.tdtu.buddyblend.adapters.search;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.databinding.ItemViewSearchUser1Binding;
import vn.edu.tdtu.buddyblend.models.MinimizedUser;
import vn.edu.tdtu.buddyblend.ui.activities.UserAccountActivity;

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.MyViewHolder> {
    private List<MinimizedUser> users;
    private Context context;
    private String currentUserId;

    public SearchUserAdapter(Context context, String currentUserId) {
        this.context = context;
        this.currentUserId = currentUserId;
    }

    public List<MinimizedUser> getUsers() {
        return users;
    }

    public void setUsers(List<MinimizedUser> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemViewSearchUser1Binding binding = ItemViewSearchUser1Binding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MinimizedUser currentUser = users.get(position);
        holder.bind(currentUser, position);
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemViewSearchUser1Binding binding;

        public MyViewHolder(@NonNull View itemView, ItemViewSearchUser1Binding binding) {
            super(itemView);
            this.binding = binding;
        }

        public void bind(MinimizedUser user, int position){
            String userProfilePic = user.getProfilePicture();

            if(userProfilePic != null && !userProfilePic.isEmpty()) {
                Glide.with(context)
                        .load(userProfilePic)
                        .error(R.drawable.user)
                        .placeholder(R.color.light_gray)
                        .into(binding.userAvatar);
            }else{
                Glide.with(context)
                        .load(R.drawable.user)
                        .error(R.drawable.user)
                        .placeholder(R.color.light_gray)
                        .into(binding.userAvatar);
            }

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, UserAccountActivity.class);
                intent.putExtra("userId", user.getId());
                context.startActivity(intent);
            });

            binding.userFullName.setText(user.getUserFullName());
            if(user.isFriend()) {
                binding.description.setText("Bạn bè");
            }else if(user.getId().equals(currentUserId)){
                binding.description.setText("Bạn");
            }else {
                binding.description.setText(user.getFriendsCount() + " bạn bè");
            }
        }
    }
}