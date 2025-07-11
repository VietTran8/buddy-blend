package vn.tdtu.edu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.tdtu.common.exception.BadRequestException;
import vn.tdtu.common.utils.MessageCode;
import vn.tdtu.common.viewmodel.ResponseVM;
import vn.tdtu.edu.model.Violation;
import vn.tdtu.edu.repository.ViolationRepository;
import vn.tdtu.edu.service.interfaces.ViolationService;

@Service
@RequiredArgsConstructor
public class ViolationServiceImpl implements ViolationService {
    private final ViolationRepository violationRepository;

    @Override
    public ResponseVM<Violation> findByRefId(String refId) {
        Violation foundViolation = violationRepository.findByRefId(refId)
                .orElseThrow(() -> new BadRequestException(MessageCode.Notification.VIOLATION_NOT_FOUND));

        ResponseVM<Violation> response = new ResponseVM<>();
        response.setData(foundViolation);
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(MessageCode.Notification.VIOLATION_FETCHED);

        return response;
    }

    @Override
    public ResponseVM<Violation> findById(String id) {
        Violation foundViolation = violationRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(MessageCode.Notification.VIOLATION_NOT_FOUND));

        ResponseVM<Violation> response = new ResponseVM<>();
        response.setData(foundViolation);
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(MessageCode.Notification.VIOLATION_FETCHED);

        return response;
    }

    @Override
    public Violation save(Violation violation) {
        return violationRepository.save(violation);
    }
}
