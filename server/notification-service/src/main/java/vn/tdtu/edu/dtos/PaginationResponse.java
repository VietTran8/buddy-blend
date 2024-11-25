package vn.tdtu.edu.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaginationResponse<D> {
    private int page;
    private int limit;
    private int totalPages;
    private List<D> data;
    private Long totalElements;
}
