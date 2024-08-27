package vn.edu.tdtu.buddyblend.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.edu.tdtu.buddyblend.enums.EMediaType;

public class UriUtils {
    private static String getMimeType(Context context, Uri uri) {
        String mimeType = null;
        if (uri != null && uri.getScheme() != null) {
            if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                ContentResolver cr = context.getContentResolver();
                mimeType = cr.getType(uri);
            } else {
                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
                if (fileExtension != null) {
                    mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
                }
            }
        }
        return mimeType;
    }

    public static boolean isVideoFile(Context context, Uri uri) {
        String mimeType = getMimeType(context, uri);
        return mimeType != null && mimeType.startsWith("video/");
    }

    public static boolean isImageFile(Context context, Uri uri) {
        String mimeType = getMimeType(context, uri);
        return mimeType != null && mimeType.startsWith("image/");
    }

    public static Map<EMediaType, List<Uri>> groupByMediaType(Context context, List<Uri> uris){
        Map<EMediaType, List<Uri>> map = new HashMap<>();
        List<Uri> imageUris = new ArrayList<>();
        List<Uri> videoUris = new ArrayList<>();

        uris.forEach(uri -> {
            if(isVideoFile(context, uri)){
                videoUris.add(uri);
            }else if(isImageFile(context, uri)){
                imageUris.add(uri);
            }
        });

        map.put(EMediaType.TYPE_IMAGE, imageUris);
        map.put(EMediaType.TYPE_VIDEO, videoUris);

        return map;
    }

    public static String getPathFromURI(Activity activity, Uri contentUri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.getContentResolver().query(contentUri, projection, null, null, null);

        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }

        return null;
    }
}
