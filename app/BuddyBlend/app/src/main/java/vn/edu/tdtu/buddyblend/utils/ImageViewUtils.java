package vn.edu.tdtu.buddyblend.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import vn.edu.tdtu.buddyblend.R;

public class ImageViewUtils {
    public static void bindUserImage(Context context, String image, ImageView imageView) {
        if(image != null && !image.isEmpty()) {
            Glide.with(context)
                    .load(image)
                    .placeholder(R.color.light_gray)
                    .error(R.drawable.user)
                    .into(imageView);
        }else{
            Glide.with(context)
                    .load(R.drawable.user)
                    .placeholder(R.color.light_gray)
                    .error(R.drawable.user)
                    .into(imageView);
        }
    }

    public static void bindCoverImage(Context context, String image, ImageView imageView) {
        if(image != null && !image.isEmpty()) {
            Glide.with(context)
                    .load(image)
                    .placeholder(R.color.light_gray)
                    .error(R.drawable.user)
                    .into(imageView);
        }else{
            Glide.with(context)
                    .load(R.color.light_gray)
                    .placeholder(R.color.light_gray)
                    .error(R.drawable.user)
                    .into(imageView);
        }
    }
}
