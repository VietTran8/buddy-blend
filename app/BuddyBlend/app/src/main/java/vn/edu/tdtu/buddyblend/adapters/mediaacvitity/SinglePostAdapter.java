package vn.edu.tdtu.buddyblend.adapters.mediaacvitity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.databinding.ItemViewSinglePicBinding;
import vn.edu.tdtu.buddyblend.dto.PostMedia;
import vn.edu.tdtu.buddyblend.models.PostResponse;
import vn.edu.tdtu.buddyblend.ui.activities.MediaViewerActivity;

public class SinglePostAdapter extends RecyclerView.Adapter<SinglePostAdapter.MyViewHolder> {
    private Context context;
    private List<PostMedia> medias = new ArrayList<>();
    private PostResponse currentPost;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setMedias(List<PostMedia> medias) {
        this.medias = medias;
        notifyDataSetChanged();
    }

    public void setCurrentPost(PostResponse currentPost) {
        this.currentPost = currentPost;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemViewSinglePicBinding binding = ItemViewSinglePicBinding.inflate(layoutInflater, parent, false);
        return new MyViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(medias.get(position), position);
    }

    @Override
    public int getItemCount() {
        return medias != null ? medias.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private final ItemViewSinglePicBinding binding;
        public MyViewHolder(@NonNull View itemView, ItemViewSinglePicBinding binding) {
            super(itemView);
            this.binding = binding;
        }

        public void bind(PostMedia media, int position) {
            String url = media.getUrl();
            Boolean isImage = media.getImage();
            if(isImage){
                binding.playBtn.setVisibility(View.GONE);
                Picasso.get()
                        .load(url)
                        .error(R.color.light_gray)
                        .placeholder(R.color.light_gray)
                        .into(binding.imgPic);
            }else{
                displayThumb(binding.imgPic, url);
            }

            binding.imgItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MediaViewerActivity.class);
                    intent.putExtra("itemIndex", position);
                    intent.putExtra("post", currentPost);

                    Gson gson = new Gson();
                    String mediasJson = gson.toJson(medias);

                    intent.putExtra("medias", mediasJson);

                    context.startActivity(intent);
                }
            });
        }

        private void displayThumb(ImageView imageView, String url){
            Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .error(R.color.light_gray)
                    .placeholder(R.color.light_gray)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            imageView.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        }
    }
}
