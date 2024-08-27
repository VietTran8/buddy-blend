package vn.edu.tdtu.buddyblend.adapters.notification;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Date;
import java.util.List;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.databinding.ItemViewNotificationBinding;
import vn.edu.tdtu.buddyblend.models.InteractNotification;
import vn.edu.tdtu.buddyblend.utils.DateUtils;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {
    private List<InteractNotification> notifications;
    private Context context;
    private int lastInsertedIndex = -1;
    private OnClick onClickListener;

    public NotificationAdapter(Context context) {
        this.context = context;
    }

    public void setNotifications(List<InteractNotification> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }

    public void setOnActionButtonClickListener(OnClick onActionButtonClickListener) {
        this.onClickListener = onActionButtonClickListener;
    }

    public interface OnClick {
        void onActionClick(InteractNotification notification, int position);
        void onItemClick(InteractNotification notification, int position);
    }

    public void addNotification(InteractNotification interactNotification) {
        this.notifications.add(0, interactNotification);
        lastInsertedIndex = 0;
        notifyItemInserted(0);
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
        ItemViewNotificationBinding binding = ItemViewNotificationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        InteractNotification interactNotification = notifications.get(position);
        holder.bind(interactNotification, position);
    }

    @Override
    public int getItemCount() {
        return notifications != null ? notifications.size() : 0;
    }

    public void removeNotification(int position) {
        if(lastInsertedIndex == position)
            lastInsertedIndex = -1;
        this.notifications.remove(position);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemViewNotificationBinding binding;

        public MyViewHolder(@NonNull View itemView, ItemViewNotificationBinding binding) {
            super(itemView);
            this.binding = binding;
        }

        public void bind(InteractNotification notification, int position){
            if(position == lastInsertedIndex) {
                itemView.setBackgroundColor(context.getColor(R.color.background_highlight));
            }

            itemView.setOnClickListener(v -> {
                onClickListener.onItemClick(notification, position);
            });

            String content = notification.getContent().replace(notification.getUserFullName(), " ");
            content = String.format("<b>%s</b>", notification.getUserFullName()) + content;

            binding.notiInfo.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY));
            if(notification.getCreateAt() != null && !notification.getCreateAt().isEmpty())
                binding.createdAt.setText(DateUtils.getTimeAgo(new Date(Long.parseLong(notification.getCreateAt()))));
            else
                binding.createdAt.setText("Vừa xong");
            String userProfile = notification.getAvatarUrl();

            if(userProfile != null && !userProfile.isEmpty()) {
                Glide.with(context)
                        .load(userProfile)
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

            binding.btnAction.setOnClickListener(v -> {
                onClickListener.onActionClick(notification, position);
            });

            bindNotiIcon(notification);
        }

        private void bindNotiIcon(InteractNotification notification) {
            switch (notification.getType()) {
                case SHARE -> {
                    binding.notificationType.setImageResource(R.drawable.share_noti);
                }
                case COMMENT -> {
                    binding.notificationType.setImageResource(R.drawable.comment_noti);
                }
                case HEART -> {
                    binding.notificationType.setImageResource(R.drawable.reaction_heart);
                }
                case HAHA -> {
                    binding.notificationType.setImageResource(R.drawable.react_haha);
                }
                case SAD -> {
                    binding.notificationType.setImageResource(R.drawable.react_sad);
                }
                case ANGRY -> {
                    binding.notificationType.setImageResource(R.drawable.react_angry);
                }
                case LIKE -> {
                    binding.notificationType.setImageResource(R.drawable.react_like);
                }
                case WOW -> {
                    binding.notificationType.setImageResource(R.drawable.react_wow);
                }
            }
        }
    }
}
