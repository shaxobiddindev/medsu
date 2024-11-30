package uz.medsu.sevice.serviceImpl;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.validation.constraints.Email;
import org.springframework.stereotype.Service;

@Service
public class EmailCheckerService {

    public boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    public boolean isValidEmailFormat(@Email String email) {
        return true;
    }

}
