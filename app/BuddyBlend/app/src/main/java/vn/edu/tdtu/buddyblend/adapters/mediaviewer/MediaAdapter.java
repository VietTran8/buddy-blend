package vn.edu.tdtu.buddyblend.adapters.mediaviewer;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.databinding.ItemViewMediaBinding;
import vn.edu.tdtu.buddyblend.dto.PostMedia;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MyViewHolder> {
    private List<PostMedia> postMedias = new ArrayList<>();
    private Context context;

    public List<PostMedia> getPostMedias() {
        return postMedias;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setPostMedias(List<PostMedia> postMedias) {
        this.postMedias = postMedias;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemViewMediaBinding binding = ItemViewMediaBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(postMedias.get(position));
    }

    @Override
    public int getItemCount() {
        return postMedias != null ? postMedias.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private final ItemViewMediaBinding binding;

        public MyViewHolder(@NonNull View itemView, ItemViewMediaBinding binding) {
            super(itemView);
            this.binding = binding;
        }

        public void bind(PostMedia media){
            if(media.getImage()){
                Picasso.get()
                        .load(media.getUrl())
                        .error(R.color.light_gray)
                        .centerCrop()
                        .resize(1024, 768)
                        .placeholder(R.color.light_gray)
                        .into(binding.postImage);
                binding.postImage.setVisibility(View.VISIBLE);
                binding.postVideo.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
            }else{
                //Video Player
                binding.postImage.setVisibility(View.GONE);
                binding.postVideo.setVisibility(View.VISIBLE);

                MediaController mediaController = new MediaController(context);

                binding.postVideo.setVideoPath(media.getUrl());
                binding.postVideo.setMediaController(mediaController);
                mediaController.setAnchorView(binding.mediaControllerContainer);

                binding.postVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.postVideo.start();
                        mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                            @Override
                            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                                    binding.progressBar.setVisibility(View.VISIBLE);
                                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                                    binding.progressBar.setVisibility(View.GONE);
                                }
                                return true;
                            }
                        });
                    }
                });
            }
        }
    }
}
