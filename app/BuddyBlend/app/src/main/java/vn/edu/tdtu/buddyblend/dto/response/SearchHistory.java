package vn.edu.tdtu.buddyblend.dto.response;

import java.io.Serializable;

public class SearchHistory implements Serializable {
    public String id;
    private String query;
    private String userId;
    private String createdAt;

    public SearchHistory(String id, String query, String userId, String createdAt) {
        this.id = id;
        this.query = query;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public SearchHistory() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}