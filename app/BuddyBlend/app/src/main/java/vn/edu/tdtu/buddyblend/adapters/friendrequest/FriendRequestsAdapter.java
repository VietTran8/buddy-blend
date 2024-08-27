package vn.edu.tdtu.buddyblend.adapters.friendrequest;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.databinding.ItemViewFriendRequestBinding;
import vn.edu.tdtu.buddyblend.dto.response.FriendRequestResponse;
import vn.edu.tdtu.buddyblend.ui.activities.UserAccountActivity;
import vn.edu.tdtu.buddyblend.utils.DateUtils;

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.MyViewHolder> {
    private List<FriendRequestResponse> friendRequests;
    private Context context;
    private OnButtonClickListener onButtonClickListener;

    public interface OnButtonClickListener {
        void onAcceptClick(Button button, FriendRequestResponse request, int position);
        void onRejectClick(Button button, FriendRequestResponse request, int position);
    }

    public FriendRequestsAdapter(Context context) {
        this.context = context;
    }

    public List<FriendRequestResponse> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(List<FriendRequestResponse> friendRequests) {
        this.friendRequests = friendRequests;
        notifyDataSetChanged();
    }

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
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
        ItemViewFriendRequestBinding binding = ItemViewFriendRequestBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new MyViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FriendRequestResponse currentRequest = friendRequests.get(position);
        holder.bind(currentRequest, position);
    }



    @Override
    public int getItemCount() {
        return friendRequests != null ? friendRequests.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemViewFriendRequestBinding binding;

        public MyViewHolder(@NonNull View itemView, ItemViewFriendRequestBinding binding) {
            super(itemView);
            this.binding = binding;
        }

        public void bind(FriendRequestResponse request, int position) {
            displayButtonLayout();

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, UserAccountActivity.class);
                intent.putExtra("userId", request.getFromUser().getId());
                context.startActivity(intent);
            });

            String profileUrl = request.getFromUser().getProfilePicture();
            if(profileUrl != null && !profileUrl.isEmpty()) {
                Glide.with(context)
                        .load(profileUrl)
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

            binding.userFullName.setText(request.getFromUser().getUserFullName());
            int mutualCount = request.getFromUser().getMutualFriends().size();
            int friendCount = request.getFromUser().getFriendsCount();

            if(mutualCount > 0)
                binding.smallInfo.setText(mutualCount + " bạn chung");
            else
                binding.smallInfo.setText(friendCount + " bạn bè");

            binding.requestAt.setText(
                    DateUtils.getTimeAgo(DateUtils.stringToDate(request.getCreatedAt()))
            );

            binding.btnAccept.setOnClickListener(v -> {
                onButtonClickListener.onAcceptClick(binding.btnAccept, request, position);
                displayAcceptMessage();
            });

            binding.btnReject.setOnClickListener(v -> {
                onButtonClickListener.onRejectClick(binding.btnAccept, request, position);
                displayRejectMessage();
            });
        }

        private void displayAcceptMessage(){
            binding.message.setText("Đã chấp nhận lời mời kết bạn.");
            binding.buttonLayout.setVisibility(View.GONE);
            binding.message.setVisibility(View.VISIBLE);
        }

        private void displayRejectMessage(){
            binding.message.setText("Đã từ chối lời mời kết bạn.");
            binding.buttonLayout.setVisibility(View.GONE);
            binding.message.setVisibility(View.VISIBLE);
        }

        private void displayButtonLayout(){
            binding.buttonLayout.setVisibility(View.VISIBLE);
            binding.message.setVisibility(View.GONE);
        }
    }
}
