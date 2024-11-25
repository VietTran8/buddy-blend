package vn.edu.tdtu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaginationResponse<D> {
    private int page;
    private int limit;
    private int totalPages;
    private List<D> data;
    private long totalElements;
}
