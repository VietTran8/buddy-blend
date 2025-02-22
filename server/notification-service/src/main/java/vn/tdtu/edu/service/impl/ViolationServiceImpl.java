package vn.tdtu.edu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.tdtu.edu.dto.ResDTO;
import vn.tdtu.edu.exception.BadRequestException;
import vn.tdtu.edu.model.Violation;
import vn.tdtu.edu.repository.ViolationRepository;
import vn.tdtu.edu.service.interfaces.ViolationService;

@Service
@RequiredArgsConstructor
public class ViolationServiceImpl implements ViolationService {
    private final ViolationRepository violationRepository;

    @Override
    public ResDTO<Violation> findByRefId(String refId) {
        Violation foundViolation = violationRepository.findByRefId(refId)
                .orElseThrow(() -> new BadRequestException("Violation not found!"));

        ResDTO<Violation> response = new ResDTO<>();
        response.setData(foundViolation);
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage("Violation fetched successfully!");

        return response;
    }

    @Override
    public ResDTO<Violation> findById(String id) {
        Violation foundViolation = violationRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Violation not found!"));

        ResDTO<Violation> response = new ResDTO<>();
        response.setData(foundViolation);
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage("Violation fetched successfully!");

        return response;
    }

    @Override
    public Violation save(Violation violation) {
        return violationRepository.save(violation);
    }
}
