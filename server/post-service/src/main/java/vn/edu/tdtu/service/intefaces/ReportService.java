package vn.edu.tdtu.service.intefaces;

import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.ApprovePostRequest;
import vn.edu.tdtu.dto.request.ReportRequest;

public interface ReportService {
    public ResDTO<?> reportPost(ReportRequest request);

    public ResDTO<?> getAllReport(String token, int page, int size);

    public ResDTO<?> approvePost(ApprovePostRequest request);
}
