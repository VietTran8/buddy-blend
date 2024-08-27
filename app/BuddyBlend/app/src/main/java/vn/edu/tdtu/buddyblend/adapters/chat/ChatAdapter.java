package vn.edu.tdtu.buddyblend.adapters.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.databinding.ItemViewMyChatBinding;
import vn.edu.tdtu.buddyblend.databinding.ItemViewOpponentChatBinding;
import vn.edu.tdtu.buddyblend.models.Message;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Message> messages;
    private String opponentUserImage;

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    public void addMessage(Message message) {
        this.messages.add(message);
        notifyItemInserted(messages.size());
    }

    public ChatAdapter(String opponentUserImage) {
        this.opponentUserImage = opponentUserImage;
        this.messages = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 1 -> {
                ItemViewOpponentChatBinding binding = ItemViewOpponentChatBinding.inflate(layoutInflater, parent, false);
                return new OpponentChatViewHolder(binding.getRoot(), binding);
            }
            default -> {
                ItemViewMyChatBinding binding = ItemViewMyChatBinding.inflate(layoutInflater, parent, false);
                return new MyChatViewHolder(binding.getRoot(), binding);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if(message.isSentByYou()) {
            ((MyChatViewHolder) holder).bind(message);
        }else {
            ((OpponentChatViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        Message currentMessage = messages.get(position);
        return (currentMessage.isSentByYou()) ? 0 : 1;
    }

    class MyChatViewHolder extends RecyclerView.ViewHolder {
        private ItemViewMyChatBinding binding;

        public MyChatViewHolder(@NonNull View itemView, ItemViewMyChatBinding binding) {
            super(itemView);
            this.binding = binding;
        }

        public void bind(Message message) {
            binding.content.setText(message.getContent());
        }
    }

    class OpponentChatViewHolder extends RecyclerView.ViewHolder {
        private ItemViewOpponentChatBinding binding;

        public OpponentChatViewHolder(@NonNull View itemView, ItemViewOpponentChatBinding binding) {
            super(itemView);
            this.binding = binding;
        }

        public void bind(Message message) {
            binding.content.setText(message.getContent());


            if(opponentUserImage == null || opponentUserImage.isEmpty()){
                Picasso.get()
                        .load(R.drawable.user)
                        .error(R.color.light_gray)
                        .placeholder(R.color.light_gray)
                        .into(binding.imgAvatar);
            }else{
                Picasso.get()
                        .load(opponentUserImage)
                        .error(R.color.light_gray)
                        .placeholder(R.color.light_gray)
                        .into(binding.imgAvatar);
            }
        }
    }
}
