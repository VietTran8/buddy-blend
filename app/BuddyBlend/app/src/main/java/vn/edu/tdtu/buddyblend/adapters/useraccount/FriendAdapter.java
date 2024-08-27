package vn.edu.tdtu.buddyblend.adapters.useraccount;

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
import vn.edu.tdtu.buddyblend.databinding.ItemViewFriendBinding;
import vn.edu.tdtu.buddyblend.dto.response.MutualFriend;
import vn.edu.tdtu.buddyblend.ui.activities.UserAccountActivity;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.MyViewHolder> {
    private List<MutualFriend> friends;
    private Context context;

    public FriendAdapter(Context context) {
        this.context = context;
    }

    public List<MutualFriend> getFriends() {
        return friends;
    }

    public void setFriends(List<MutualFriend> friends) {
        this.friends = friends;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemViewFriendBinding binding = ItemViewFriendBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MutualFriend currentUser = friends.get(position);
        holder.bind(currentUser, position);
    }

    @Override
    public int getItemCount() {
        return friends != null ? friends.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemViewFriendBinding binding;

        public MyViewHolder(@NonNull View itemView, ItemViewFriendBinding binding) {
            super(itemView);
            this.binding = binding;
        }

        public void bind(MutualFriend user, int position) {
            String userProfilePic = user.getProfileImage();

            if(userProfilePic != null && !userProfilePic.isEmpty()) {
                Glide.with(context)
                        .load(userProfilePic)
                        .placeholder(R.color.light_gray)
                        .error(R.drawable.user)
                        .into(binding.userAvatar);
            }else {
                Glide.with(context)
                        .load(R.drawable.user)
                        .placeholder(R.color.light_gray)
                        .error(R.drawable.user)
                        .into(binding.userAvatar);
            }

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, UserAccountActivity.class);
                intent.putExtra("userId", user.getId());
                context.startActivity(intent);
            });

            binding.userFullName.setText(user.getFullName());
        }
    }
}
