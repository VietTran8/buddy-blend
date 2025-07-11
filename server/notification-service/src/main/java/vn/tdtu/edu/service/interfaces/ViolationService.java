package vn.tdtu.edu.service.interfaces;

import vn.tdtu.common.viewmodel.ResponseVM;
import vn.tdtu.edu.model.Violation;

public interface ViolationService {
    ResponseVM<Violation> findByRefId(String refId);

    ResponseVM<Violation> findById(String refId);

    Violation save(Violation violation);
}
