package vn.edu.tdtu.buddyblend.adapters.tagusers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.databinding.ItemViewTagFriendBinding;
import vn.edu.tdtu.buddyblend.models.MinimizedUser;

public class UserFriendsAdapter extends RecyclerView.Adapter<UserFriendsAdapter.MyViewHolder> {
    private List<MinimizedUser> friends;
    private Context context;
    private Set<MinimizedUser> selectedMinimizedUser;
    private OnUserCheckedListener listener;
    private List<CheckBox> checkBoxes;
    private List<MinimizedUser> taggingMinimizedUsers;

    public interface OnUserCheckedListener{
        void onCheck(MinimizedUser minimizedUser, boolean checked);
    }

    public UserFriendsAdapter(List<MinimizedUser> taggingMinimizedUsers) {
        selectedMinimizedUser = new HashSet<>();
        checkBoxes = new ArrayList<>();
        this.taggingMinimizedUsers = taggingMinimizedUsers;
    }

    public void setTaggingUsers(List<MinimizedUser> taggingMinimizedUsers) {
        this.taggingMinimizedUsers = taggingMinimizedUsers;
    }

    public void setOnUserCheckedListener(OnUserCheckedListener listener){
        this.listener = listener;
    }

    public void setFriends(List<MinimizedUser> friends){
        this.friends = friends;
        notifyDataSetChanged();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemViewTagFriendBinding binding = ItemViewTagFriendBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MinimizedUser minimizedUser = friends.get(position);
        holder.bind(minimizedUser, position);
    }

    @Override
    public int getItemCount() {
        return friends != null ? friends.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemViewTagFriendBinding binding;
        public MyViewHolder(@NonNull View itemView, ItemViewTagFriendBinding binding) {
            super(itemView);
            this.binding = binding;
        }

        public void bind(MinimizedUser minimizedUser, int position){
            checkBoxes.add(binding.ckbTagged);
            binding.userName.setText(String.join(" ", minimizedUser.getFirstName(), minimizedUser.getMiddleName(), minimizedUser.getLastName()));

            String profileUrl = minimizedUser.getProfilePicture();
            if(profileUrl == null || profileUrl.isEmpty()){
                Picasso.get()
                        .load(R.drawable.user)
                        .placeholder(R.color.light_gray)
                        .error(R.color.light_gray)
                        .into(binding.userAvatar);
            }else
                Picasso.get()
                        .load(profileUrl)
                        .placeholder(R.color.light_gray)
                        .error(R.color.light_gray)
                        .into(binding.userAvatar);

            binding.ckbTagged.setOnCheckedChangeListener(((compoundButton, b) -> {
                if(b){
                    selectedMinimizedUser.add(minimizedUser);
                }else{
                    selectedMinimizedUser.remove(minimizedUser);
                }
                listener.onCheck(minimizedUser, b);
            }));

            itemView.setOnClickListener(view -> {
                if(!binding.ckbTagged.isChecked()){
                    binding.ckbTagged.setChecked(true);
                }else{
                    binding.ckbTagged.setChecked(false);
                }
            });

            if (taggingMinimizedUsers.stream().map(MinimizedUser::getId).collect(Collectors.toList()).contains(minimizedUser.getId())){
                binding.ckbTagged.setChecked(true);
            }
        }
    }

    public Set<MinimizedUser> getSelectedUser(){
        return selectedMinimizedUser;
    }

    public void removeSelectedUser(MinimizedUser minimizedUser){
        selectedMinimizedUser.remove(minimizedUser);
        int userIndex = friends.indexOf(minimizedUser);
        Log.d("TESSSST", "removeSelectedUser: " + userIndex);
        checkBoxes.get(userIndex).setChecked(false);
    }
}
