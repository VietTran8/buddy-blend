package vn.edu.tdtu.buddyblend.adapters.homefragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.databinding.ItemViewTaggingUserBinding;
import vn.edu.tdtu.buddyblend.models.MinimizedUser;

public class TaggingUserAdapter extends RecyclerView.Adapter<TaggingUserAdapter.MyViewHolder> {
    List<MinimizedUser> users;

    public List<MinimizedUser> getUsers() {
        return users;
    }

    public TaggingUserAdapter(){
        users = new ArrayList<>();
    }

    public void setUsers(List<MinimizedUser> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemViewTaggingUserBinding binding = ItemViewTaggingUserBinding.inflate(layoutInflater, parent, false);
        return new MyViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MinimizedUser currentUser = users.get(position);
        holder.bind(currentUser);
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemViewTaggingUserBinding binding;

        public MyViewHolder(@NonNull View itemView, ItemViewTaggingUserBinding binding) {
            super(itemView);
            this.binding = binding;
        }

        public void bind(MinimizedUser user){
            binding.userName.setText(user.getUserFullName());

            String profileUrl = user.getProfilePicture();
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
        }
    }
}
