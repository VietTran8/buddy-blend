package vn.edu.tdtu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaginationResponse<D> implements Serializable {


    private int page;
    private int limit;
    private int totalPages;
    private List<D> data;
}
