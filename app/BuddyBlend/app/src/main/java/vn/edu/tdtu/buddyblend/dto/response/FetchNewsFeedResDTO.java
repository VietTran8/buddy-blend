package vn.edu.tdtu.buddyblend.dto.response;

import java.util.List;

import vn.edu.tdtu.buddyblend.models.PostResponse;

public class FetchNewsFeedResDTO {
    private int totalPages;
    private List<PostResponse> posts;

    public FetchNewsFeedResDTO() {
    }

    public FetchNewsFeedResDTO(int totalPages, List<PostResponse> posts) {
        this.totalPages = totalPages;
        this.posts = posts;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<PostResponse> getPosts() {
        return posts;
    }

    public void setPosts(List<PostResponse> posts) {
        this.posts = posts;
    }
}
