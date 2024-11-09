package uz.medsu.sevice;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.medsu.payload.SignInDTO;
import uz.medsu.payload.UserDTO;
import uz.medsu.utils.ResponseMessage;


public interface AuthService {
    ResponseMessage login(SignInDTO signInDTO);
    ResponseMessage signUp(UserDTO userDTO);
}
