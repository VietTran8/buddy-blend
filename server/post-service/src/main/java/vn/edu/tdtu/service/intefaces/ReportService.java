package vn.edu.tdtu.service.intefaces;

import vn.edu.tdtu.dto.request.ApprovePostRequest;
import vn.edu.tdtu.dto.request.ReportRequest;
import vn.tdtu.common.viewmodel.ResponseVM;

public interface ReportService {
    ResponseVM<?> reportPost(ReportRequest request);

    ResponseVM<?> getAllReport(String token, int page, int size);

    ResponseVM<?> approvePost(ApprovePostRequest request);
}