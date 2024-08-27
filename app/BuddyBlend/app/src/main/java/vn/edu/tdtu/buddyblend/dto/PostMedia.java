package vn.edu.tdtu.buddyblend.dto;

import java.io.Serializable;

public class PostMedia implements Serializable {
    private String url;
    private Boolean isImage;

    public PostMedia(String url, Boolean isImage) {
        this.url = url;
        this.isImage = isImage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getImage() {
        return isImage;
    }

    public void setImage(Boolean image) {
        isImage = image;
    }
}
