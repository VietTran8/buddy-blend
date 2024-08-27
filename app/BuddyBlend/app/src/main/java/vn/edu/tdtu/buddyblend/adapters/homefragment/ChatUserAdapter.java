package vn.edu.tdtu.buddyblend.adapters.homefragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.databinding.ItemViewOnlineUserBinding;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.MyViewHolder> {
    private List<String> imgUrls;

    public void setImgUrls(List<String> imgUrls){
        this.imgUrls = imgUrls;
        notifyDataSetChanged();
    }

    public ChatUserAdapter(List<String> imgUrls){
        this.imgUrls = imgUrls;
    }

    public ChatUserAdapter(){
        this.imgUrls = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemViewOnlineUserBinding binding = ItemViewOnlineUserBinding.inflate(layoutInflater, parent, false);

        return new MyViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get()
                .load(imgUrls.get(position))
                .placeholder(R.color.light_gray)
                .error(R.color.light_gray)
                .into(holder.binding.imgUserAvatar);
    }

    @Override
    public int getItemCount() {
        return imgUrls != null ? imgUrls.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private final ItemViewOnlineUserBinding binding;

        public MyViewHolder(@NonNull View itemView, ItemViewOnlineUserBinding binding) {
            super(itemView);
            this.binding = binding;
        }

        public ItemViewOnlineUserBinding getBinding(){
            return binding;
        }
    }
}
