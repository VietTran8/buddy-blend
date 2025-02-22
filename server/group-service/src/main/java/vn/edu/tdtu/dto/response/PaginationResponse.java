package vn.edu.tdtu.dto.response;

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
    private int totalElements;
    private List<D> data;
}
