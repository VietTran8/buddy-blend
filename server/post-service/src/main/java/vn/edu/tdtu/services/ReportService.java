package vn.edu.tdtu.services;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.dtos.request.ReportRequest;
import vn.edu.tdtu.dtos.response.GetAllReportResponse;
import vn.edu.tdtu.dtos.response.ReportResponse;
import vn.edu.tdtu.mappers.response.ReportResponseMapper;
import vn.edu.tdtu.models.Report;
import vn.edu.tdtu.repositories.PostRepository;
import vn.edu.tdtu.repositories.ReportRepository;
import vn.edu.tdtu.utils.JwtUtils;
import vn.edu.tdtu.utils.SecurityContextUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final PostShareService postShareService;
    private final JwtUtils jwtUtils;
    private final ReportResponseMapper reportResponseMapper;

    @CacheEvict(cacheNames = "reports", allEntries = true)
    public ResDTO<?> reportPost(ReportRequest request){
        String userId = SecurityContextUtils.getUserId();

        ResDTO<Map<String, String>> response = new ResDTO<>();
        Map<String, String> data = new HashMap<>();

        Report report = new Report();
        report.setReason(request.getReason());
        report.setId(UUID.randomUUID().toString());
        report.setUserId(userId);
        report.setCreateAt(new Date());

        postRepository.findById(request.getPostId())
                .ifPresentOrElse(
                        p -> {
                            report.setPostId(p.getId());

                            response.setMessage("submitted");
                            response.setData(data);
                            response.setCode(200);
                        }, () -> {
                            postShareService.findById(request.getPostId())
                                    .ifPresentOrElse(
                                            postShare -> {
                                                report.setPostId(postShare.getSharedPostId());

                                                response.setMessage("submitted");
                                                response.setData(data);
                                                response.setCode(200);
                                            },
                                            () -> {
                                                throw new RuntimeException("post not found with id: " + request.getPostId());
                                            });
                        }
                );
        reportRepository.save(report);
        data.put("savedReport", report.getId());

        return response;
    }

    @Cacheable(key = "T(java.util.Objects).hash(#a1, #a2)", value = "reports", unless = "#result.data.reports.isEmpty()")
    public ResDTO<?> getAllReport(String token, int page, int size){
        Page<Report> reportPage = reportRepository
                .findAll(PageRequest.of(page - 1, size));

        List<ReportResponse> reports = reportPage
                .stream()
                .map(p -> reportResponseMapper.mapToDto(token, p))
                .toList();

        GetAllReportResponse allReportResponse = new GetAllReportResponse();
        allReportResponse.setCurrentPage(page);
        allReportResponse.setTotalPages(reportPage.getTotalPages());
        allReportResponse.setReports(reports);

        ResDTO<GetAllReportResponse> response = new ResDTO<>();
        response.setData(allReportResponse);
        response.setCode(200);
        response.setMessage("reports fetched successfully");

        return response;
    }
}
