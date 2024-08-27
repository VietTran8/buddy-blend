package vn.edu.tdtu.buddyblend.dto.response;

import java.io.Serializable;
import java.util.List;

import vn.edu.tdtu.buddyblend.models.MinimizedUser;
import vn.edu.tdtu.buddyblend.models.PostResponse;

public class SearchResponse implements Serializable {
    private List<MinimizedUser> users;
    private List<PostResponse> posts;

    public SearchResponse(List<MinimizedUser> users, List<PostResponse> posts) {
        this.users = users;
        this.posts = posts;
    }

    public SearchResponse() {
    }

    public List<MinimizedUser> getUsers() {
        return users;
    }

    public void setUsers(List<MinimizedUser> users) {
        this.users = users;
    }

    public List<PostResponse> getPosts() {
        return posts;
    }

    public void setPosts(List<PostResponse> posts) {
        this.posts = posts;
    }
}