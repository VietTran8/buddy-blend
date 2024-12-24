package vn.edu.tdtu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAllReportResponse implements Serializable {
    private int currentPage;
    private int totalPages;
    private List<ReportResponse> reports;
}
