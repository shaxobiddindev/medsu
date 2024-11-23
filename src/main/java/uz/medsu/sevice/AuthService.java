package uz.medsu.sevice;

import uz.medsu.payload.users.SignInDTO;
import uz.medsu.payload.users.UserDTO;
import uz.medsu.utils.ResponseMessage;


public interface AuthService {
    ResponseMessage login(SignInDTO signInDTO);
    ResponseMessage signUp(UserDTO userDTO);
}
