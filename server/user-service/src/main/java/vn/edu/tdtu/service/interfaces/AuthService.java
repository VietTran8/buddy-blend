package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.request.ConfirmTokenCheckingRequest;

public interface AuthService {
    boolean confirmationTokenChecking(ConfirmTokenCheckingRequest request);
}
