package vn.edu.tdtu.util;

import java.security.SecureRandom;

public class OTPUtils {
    public static String generateOTP(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            otp.append(digit);
        }

        return otp.toString();
    }
}
