package vn.edu.tdtu.buddyblend.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.util.Size;

import java.io.IOException;
import java.util.HashMap;

public class VideoUtils {
    public static Bitmap getThumbBitmap(String url) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(url, new HashMap<String, String>());
            Bitmap thumbnail = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

            return thumbnail;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }

        return null;
    }
}
