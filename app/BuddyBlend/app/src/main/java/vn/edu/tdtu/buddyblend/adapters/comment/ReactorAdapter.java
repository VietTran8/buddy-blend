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
import vn.edu.tdtu.buddyblend.databinding.ItemViewReactorBinding;
import vn.edu.tdtu.buddyblend.enums.EReactionType;
import vn.edu.tdtu.buddyblend.models.React;
import vn.edu.tdtu.buddyblend.ui.activities.UserAccountActivity;

public class ReactorAdapter extends RecyclerView.Adapter<ReactorAdapter.MyViewHolder> {
    private List<React> reacts;
    private Context context;

    public ReactorAdapter(Context context) {
        this.context = context;
    }

    public void setReacts(List<React> reacts) {
        this.reacts = reacts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemViewReactorBinding binding = ItemViewReactorBinding.inflate(layoutInflater, parent, false);
        return new MyViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        React currentReact = reacts.get(position);
        holder.bind(currentReact);
    }

    @Override
    public int getItemCount() {
        return reacts != null ? reacts.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemViewReactorBinding binding;

        public MyViewHolder(@NonNull View itemView, ItemViewReactorBinding binding) {
            super(itemView);
            this.binding = binding;
        }

        public void bind(React react){
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, UserAccountActivity.class);
                intent.putExtra("userId", react.getUser().getId());
                context.startActivity(intent);
            });

            String profileUrl = react.getUser().getProfilePicture();
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

            Picasso.get()
                    .load(getIconResource(react.getType()))
                    .error(R.color.light_gray)
                    .placeholder(R.color.light_gray)
                    .into(binding.reactIcon);

            binding.userName.setText(react.getUser().getUserFullName());
        }

        private int getIconResource(EReactionType type){
            switch (type) {
                case HAHA -> {
                    return (R.drawable.react_haha);
                }
                case SAD -> {
                    return (R.drawable.react_sad);
                }
                case LIKE -> {
                    return (R.drawable.react_like);
                }
                case HEART -> {
                    return (R.drawable.reaction_heart);
                }
                case WOW -> {
                    return (R.drawable.react_wow);
                }
                case ANGRY -> {
                    return (R.drawable.react_angry);
                }
            }
            return -1;
        }
    }
}
