package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.constant.MessageCode;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.ApprovePostRequest;
import vn.edu.tdtu.dto.request.ReportRequest;
import vn.edu.tdtu.dto.response.PaginationResponse;
import vn.edu.tdtu.dto.response.ReportResponse;
import vn.edu.tdtu.enums.EModerateType;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.mapper.response.ReportResponseMapper;
import vn.edu.tdtu.message.ModerationNotificationMsg;
import vn.edu.tdtu.model.Media;
import vn.edu.tdtu.model.Post;
import vn.edu.tdtu.model.Report;
import vn.edu.tdtu.publisher.KafkaEventPublisher;
import vn.edu.tdtu.repository.MediaRepository;
import vn.edu.tdtu.repository.PostRepository;
import vn.edu.tdtu.repository.ReportRepository;
import vn.edu.tdtu.service.intefaces.ReportService;
import vn.edu.tdtu.util.SecurityContextUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final MediaRepository mediaRepository;
    private final KafkaEventPublisher kafkaEventPublisher;
    private final ReportResponseMapper reportResponseMapper;

    @Override
    @CacheEvict(cacheNames = "reports", allEntries = true)
    public ResDTO<?> reportPost(ReportRequest request) {
        String userId = SecurityContextUtils.getUserId();

        ResDTO<Map<String, String>> response = new ResDTO<>();
        Map<String, String> data = new HashMap<>();

        Report report = new Report();
        report.setReason(request.getReason());
        report.setId(UUID.randomUUID().toString());
        report.setUserId(userId);
        report.setCreateAt(new Date());
        report.setActive(true);

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new BadRequestException(MessageCode.POST_NOT_FOUND_ID, request.getPostId()));

        report.setPostId(post.getId());

        response.setMessage(MessageCode.REPORT_SUBMITTED);
        response.setData(data);
        response.setCode(200);

        reportRepository.save(report);
        data.put("savedReport", report.getId());

        return response;
    }

    @Override
    @Cacheable(key = "T(java.util.Objects).hash(#a1, #a2)", value = "reports", unless = "#result.data.data.isEmpty()")
    public ResDTO<?> getAllReport(String token, int page, int size) {
        Page<Report> reportPage = reportRepository
                .findAllByActiveOrActive(null, true, PageRequest.of(page - 1, size));

        List<ReportResponse> reports = reportPage
                .stream()
                .map(p -> reportResponseMapper.mapToDto(token, p))
                .toList();

        PaginationResponse<ReportResponse> paginationResponse = new PaginationResponse<>();
        paginationResponse.setData(reports);
        paginationResponse.setPage(page);
        paginationResponse.setTotalPages(reportPage.getTotalPages());
        paginationResponse.setLimit(size);

        ResDTO<PaginationResponse<ReportResponse>> response = new ResDTO<>();
        response.setData(paginationResponse);
        response.setCode(200);
        response.setMessage(MessageCode.REPORT_FETCHED);

        return response;
    }

    @Override
    public ResDTO<?> approvePost(ApprovePostRequest request) {
        Report foundReport = reportRepository.findByIdAndActiveOrActive(request.getReportId(), null, true)
                .orElseThrow(() -> new BadRequestException(MessageCode.REPORT_NOT_FOUND));

        if(!request.getAccept()) {
            Post foundPost = postRepository.findByIdAndDetached(foundReport.getPostId(), false).orElseThrow(
                    () -> new BadRequestException(MessageCode.POST_NOT_FOUND)
            );

            foundPost.setDetached(true);

            List<Media> postMedias = mediaRepository.findAllById(foundPost.getMediaIds());

            mediaRepository.saveAll(postMedias
                    .stream()
                    .peek(media -> media.setDetached(true))
                    .toList()
            );

            postRepository.save(foundPost);

            ModerationNotificationMsg msg = new ModerationNotificationMsg();
            msg.setType(EModerateType.TYPE_POST);
            msg.setAccept(request.getAccept());
            msg.setTimestamp(String.valueOf(System.currentTimeMillis()));
            msg.setRejectReason(request.getReason());
            msg.setToUserId(foundPost.getUserId());
            msg.setRefId(foundPost.getId());

            kafkaEventPublisher.pubModerateResultNotificationMessage(msg);
        }

        foundReport.setActive(false);
        reportRepository.save(foundReport);

        return new ResDTO<>(
            request.getAccept() ? MessageCode.POST_ACCEPTED : MessageCode.POST_DETACHED,
                null,
                HttpServletResponse.SC_OK
        );
    }
}
