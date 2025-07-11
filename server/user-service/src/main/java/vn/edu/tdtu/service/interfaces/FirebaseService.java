package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.response.FcmResponse;
import vn.edu.tdtu.model.User;

import java.util.List;

public interface FirebaseService {
    String getAccessToken();

    void removeUserRegistrationId(User user, List<String> registrationIds);

    void saveUserDeviceGroup(User user, List<String> registrationIds);

    FcmResponse getNotificationKey(String notificationKeyName);
}
