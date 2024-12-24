package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.response.FcmResponse;
import vn.edu.tdtu.model.User;

import java.util.List;

public interface FirebaseService {
    public String getAccessToken();
    public void removeUserRegistrationId(User user, List<String> registrationIds);
    public void saveUserDeviceGroup(User user, List<String> registrationIds);
    public FcmResponse getNotificationKey(String notificationKeyName);
}
