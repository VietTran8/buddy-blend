package vn.edu.tdtu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.request.ConfirmTokenCheckingRequest;
import vn.edu.tdtu.repository.httpclient.AuthClient;
import vn.edu.tdtu.service.interfaces.AuthService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthClient authClient;

    @Override
    public boolean confirmationTokenChecking(ConfirmTokenCheckingRequest request) {
        ResponseEntity<?> response = authClient.confirmTokenChecking(request);

        return response.getStatusCode().equals(HttpStatusCode.valueOf(200));
    }
}
