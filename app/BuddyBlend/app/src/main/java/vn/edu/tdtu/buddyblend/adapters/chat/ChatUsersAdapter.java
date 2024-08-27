package vn.edu.tdtu.buddyblend.adapters.chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.databinding.ItemViewChatUserBinding;
import vn.edu.tdtu.buddyblend.models.Room;
import vn.edu.tdtu.buddyblend.ui.activities.ChatActivity;
import vn.edu.tdtu.buddyblend.utils.DateUtils;

public class ChatUsersAdapter extends RecyclerView.Adapter<ChatUsersAdapter.MyViewHolder> {
    private List<Room> rooms;
    private Context context;

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
        notifyDataSetChanged();
    }

    public ChatUsersAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemViewChatUserBinding binding = ItemViewChatUserBinding.inflate(layoutInflater, parent, false);

        return new MyViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Room currentRoom = rooms.get(position);
        holder.bind(currentRoom);
    }

    @Override
    public int getItemCount() {
        return rooms != null ? rooms.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ItemViewChatUserBinding binding;
        public MyViewHolder(@NonNull View itemView, ItemViewChatUserBinding binding) {
            super(itemView);
            this.binding = binding;
        }

        public void bind(Room room){
            String latestContent = room.getLatestMessage().getContent();
            if (room.getLatestMessage().isSentByYou()) {
                latestContent = "Bạn: " + latestContent;
            }
            binding.latestContent.setText(latestContent);
            binding.sentAt.setText(DateUtils.getTimeAgo(DateUtils.stringToDate(room.getLatestMessage().getCreatedAt())));

            String image = room.getRoomImage();
            if(image == null || image.isEmpty()){
                Picasso.get()
                        .load(R.drawable.user)
                        .error(R.color.light_gray)
                        .placeholder(R.color.light_gray)
                        .into(binding.userAvatar);
            }else{
                Picasso.get()
                        .load(image)
                        .error(R.color.light_gray)
                        .placeholder(R.color.light_gray)
                        .into(binding.userAvatar);
            }

            binding.userName.setText(room.getRoomName());
            itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("toUserId", room.getOpponentUserId());
                intent.putExtra("toUserImage", room.getRoomImage());
                intent.putExtra("toUserName", room.getRoomName());
                intent.putExtra("roomId", room.getId());

                context.startActivity(intent);
            });
        }
    }
}
