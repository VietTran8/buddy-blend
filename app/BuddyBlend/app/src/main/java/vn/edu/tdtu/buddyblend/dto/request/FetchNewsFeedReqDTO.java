package vn.edu.tdtu.buddyblend.dto.request;

public class FetchNewsFeedReqDTO {
    String startTime;
    int page;
    int size;

    public FetchNewsFeedReqDTO(String startTime, int page, int size) {
        this.startTime = startTime;
        this.page = page;
        this.size = size;
    }

    public FetchNewsFeedReqDTO() {
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}