package vn.tdtu.edu.service.interfaces;

import vn.tdtu.edu.dto.ResDTO;
import vn.tdtu.edu.model.Violation;

public interface ViolationService {
    ResDTO<Violation> findByRefId(String refId);
    ResDTO<Violation> findById(String refId);
    Violation save(Violation violation);
}
