package vn.edu.tdtu.buddyblend.adapters.tagusers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.databinding.ItemViewSelectedUserBinding;
import vn.edu.tdtu.buddyblend.models.MinimizedUser;
import vn.edu.tdtu.buddyblend.ui.activities.TagUserActivity;

public class SelectedUserAdapter extends RecyclerView.Adapter<SelectedUserAdapter.MyViewHolder> {
    private List<MinimizedUser> selectedMinimizedUser;
    private Context context;
    private TagUserActivity activity;
    private UserFriendsAdapter userFriendsAdapter;

    public SelectedUserAdapter() {
        selectedMinimizedUser = new ArrayList<>();
    }

    public void setSelectedUser(List<MinimizedUser> selectedMinimizedUser){
        this.selectedMinimizedUser = selectedMinimizedUser;
        notifyDataSetChanged();
    }

    public List<MinimizedUser> getSelectedUser() {
        return selectedMinimizedUser;
    }

    public void addSelectedUser(MinimizedUser minimizedUser){
        this.selectedMinimizedUser.add(minimizedUser);
        notifyItemInserted(selectedMinimizedUser.size() - 1);
    }

    public void removeSelectedUser(MinimizedUser minimizedUser){
        this.selectedMinimizedUser.remove(minimizedUser);
        notifyDataSetChanged();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemViewSelectedUserBinding binding = ItemViewSelectedUserBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MinimizedUser minimizedUser = selectedMinimizedUser.get(position);
        holder.bind(minimizedUser, position);
    }

    @Override
    public int getItemCount() {
        return selectedMinimizedUser != null ? selectedMinimizedUser.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private ItemViewSelectedUserBinding binding;
        public MyViewHolder(@NonNull View itemView, ItemViewSelectedUserBinding binding) {
            super(itemView);
            this.binding = binding;
        }

        public void bind(MinimizedUser minimizedUser, int position){
            String profileUrl = minimizedUser.getProfilePicture();
            if(profileUrl == null || profileUrl.isEmpty()){
                Picasso.get()
                        .load(R.drawable.user)
                        .placeholder(R.color.light_gray)
                        .error(R.color.light_gray)
                        .into(binding.selectedAvatar);
            }else
                Picasso.get()
                        .load(profileUrl)
                        .placeholder(R.color.light_gray)
                        .error(R.color.light_gray)
                        .into(binding.selectedAvatar);

            binding.btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeSelectedUser(minimizedUser);
                    userFriendsAdapter.removeSelectedUser(minimizedUser);
                    if(isEmpty()){
                        activity.disableSelectedRcv();
                    }
                }
            });
        }
    }

    public void setUserFriendsAdapter(UserFriendsAdapter userFriendsAdapter) {
        this.userFriendsAdapter = userFriendsAdapter;
    }

    public void setActivity(TagUserActivity activity) {
        this.activity = activity;
    }

    public boolean isEmpty(){
        return selectedMinimizedUser.isEmpty();
    }
}
