package vn.edu.tdtu.buddyblend.adapters.homefragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.databinding.ImageItemBinding;
import vn.edu.tdtu.buddyblend.models.PostResponse;

public class PostPictureAdapter extends RecyclerView.Adapter<PostPictureAdapter.MyViewHolder> {
    private PostResponse post;
    private List<String> imageUrls;
    private List<String> types;
    private Context context;

    public PostPictureAdapter(PostResponse post, Context context) {
        this.post = post;
        this.context = context;
        this.imageUrls = new ArrayList<>();
        this.types = new ArrayList<>();

        post.getImageUrls().forEach(url -> {
            imageUrls.add(url);
            types.add("image");
        });

        post.getVideoUrls().forEach(url -> {
            imageUrls.add(url);
            types.add("video");
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageItemBinding binding = ImageItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new MyViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String currentImage = imageUrls.get(position);
        String currentType = types.get(position);
        holder.bind(currentImage, currentType, position);
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if(post.getImageUrls() != null)
            size += post.getImageUrls().size();

        if(post.getVideoUrls() != null)
            size += post.getVideoUrls().size();

        return size >= 4 ? 4 : size;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageItemBinding binding;
        public MyViewHolder(@NonNull View itemView, ImageItemBinding binding) {
            super(itemView);
            this.binding = binding;
        }

        public void bind(String imageUrl, String currentType, int position) {
            binding.remainingPicsOverlay.setVisibility(View.GONE);

            if(imageUrls.size() <= 1) {
                binding.imageItem.setMaxHeight(dpToPx(context, 300));
                binding.imageItem.setAdjustViewBounds(true);
            }

            if(imageUrls.size() > 4 && position == 3){
                binding.remainingPicsOverlay.setVisibility(View.VISIBLE);
                binding.remainingPics.setText("+" + (imageUrls.size() - 3));
            }

            Glide.with(context)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.color.light_gray)
                    .into(binding.imageItem);

            if(currentType.equals("video"))
                binding.playBtnWrapper.setVisibility(View.VISIBLE);
            else
                binding.playBtnWrapper.setVisibility(View.GONE);
        }

        public int dpToPx(Context context, int dp) {
            return (int) (dp * context.getResources().getDisplayMetrics().density);
        }
    }
}
