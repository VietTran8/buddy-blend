package vn.edu.tdtu.buddyblend.business;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.dto.ResDTO;
import vn.edu.tdtu.buddyblend.repository.APIClient;
import vn.edu.tdtu.buddyblend.repository.IFileRepository;
import vn.edu.tdtu.buddyblend.repository.IPostRepository;
import vn.edu.tdtu.buddyblend.utils.ErrorResponseUtils;
import vn.edu.tdtu.buddyblend.utils.UriUtils;

public class FileBusiness {
    private final IFileRepository fileRepository;
    private final String TAG = "FileBusiness";
    private static FileBusiness instance;
    private FileBusiness() {
        fileRepository = APIClient.getClient().create(IFileRepository.class);
    }

    public static FileBusiness getInstance(){
        if(instance == null)
            instance = new FileBusiness();
        return instance;
    }

    public void uploadAll(Activity activity, List<Uri> uris, String type, ActionCallback<List<String>> callback){
        List<String> uploadedUrls = new ArrayList<>();
        if(!uris.isEmpty()){
            List<MultipartBody.Part> parts = new ArrayList<>();
            for (Uri fileUri : uris) {
                if(!isWebUrl(fileUri)){
                    File file = new File(UriUtils.getPathFromURI(activity, fileUri));
                    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    MultipartBody.Part part = MultipartBody.Part.createFormData("files", file.getName(), requestBody);
                    parts.add(part);
                }else if(isWebUrl(fileUri)){
                    uploadedUrls.add(fileUri.toString());
                }
            }

            if(!parts.isEmpty()) {
                Call<ResDTO<Map<String, List<String>>>> call = fileRepository.uploadAll(type, parts);
                call.enqueue(new Callback<ResDTO<Map<String, List<String>>>>() {
                    @Override
                    public void onResponse(Call<ResDTO<Map<String, List<String>>>> call, Response<ResDTO<Map<String, List<String>>>> response) {
                        if(response.isSuccessful()){
                            List<String> newUrls = response.body().getData().get("urls");
                            newUrls.addAll(uploadedUrls);
                            callback.onSuccess(newUrls);
                        }else{
                            ErrorResponseUtils<ResDTO<Map<String, List<String>>>, List<String>> errorResponseUtils = new ErrorResponseUtils<>();
                            errorResponseUtils.doErrorCallback(response, callback);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResDTO<Map<String, List<String>>>> call, Throwable t) {
                        callback.onFailure(t.getMessage());
                    }
                });
            }else{
                callback.onSuccess(uploadedUrls);
            }
        }else{
            callback.onSuccess(new ArrayList<>());
        }
    }

    public byte[] compressBitmap(Bitmap bitmap, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }

    private boolean isWebUrl(Uri uri) {
        String scheme = uri.getScheme();
        return (scheme != null && (scheme.equals("http") || scheme.equals("https")));
    }
}
