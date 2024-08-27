package vn.edu.tdtu.buddyblend.adapters.createpost;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import java.util.List;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.databinding.ItemViewCreatePostMediaBinding;
import vn.edu.tdtu.buddyblend.utils.UriUtils;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {
    private Context context;
    private List<Uri> mediaList;
    private OnEmptyListListener listener;

    public MediaAdapter(Context context, List<Uri> mediaList, OnEmptyListListener listener) {
        this.context = context;
        this.mediaList = mediaList;
        this.listener = listener;
    }

    public List<Uri> getMediaList() {
        return this.mediaList;
    }

    public void addMediaList(List<Uri> mediaList) {
        this.mediaList.addAll(mediaList);
        notifyItemRangeInserted(this.mediaList.size() - 1, mediaList.size());
    }

    public interface OnEmptyListListener{
        void onEmptyList();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemViewCreatePostMediaBinding binding = ItemViewCreatePostMediaBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri mediaUri = mediaList.get(position);
        holder.bind(mediaUri, position);
    }

    @Override
    public int getItemCount() {
        return mediaList != null ? mediaList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemViewCreatePostMediaBinding binding;
        public ViewHolder(@NonNull View itemView, ItemViewCreatePostMediaBinding binding) {
            super(itemView);
            this.binding = binding;
        }

        public void bind(Uri uri, int position){
            if(UriUtils.isImageFile(context, uri))
                Picasso.get()
                        .load(uri)
                        .error(R.color.light_gray)
                        .placeholder(R.color.light_gray)
                        .into(binding.imageView);
            else if(UriUtils.isVideoFile(context, uri)) {
                Glide.with(context)
                        .load(uri)
                        .error(R.color.light_gray)
                        .placeholder(R.color.light_gray)
                        .apply(new RequestOptions().frame(1000000))
                        .into(binding.imageView);
            }

            binding.delImage.setOnClickListener(view -> {
                mediaList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mediaList.size());
                if(mediaList.isEmpty()){
                    listener.onEmptyList();
                }
            });
        }
    }
}
