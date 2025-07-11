package vn.tdtu.common.viewmodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaginationResponseVM<D> implements Serializable {
    private int page;
    private int limit;
    private int totalPages;
    private List<D> data;
    private long totalElements;

    public PaginationResponseVM(int page, int limit, int totalPages, List<D> data) {
        this.page = page;
        this.limit = limit;
        this.totalPages = totalPages;
        this.data = data;
    }
}
