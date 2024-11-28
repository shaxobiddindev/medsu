package uz.medsu.sevice;

import uz.medsu.enums.CodeType;
import uz.medsu.payload.users.EmailConfirmDTO;
import uz.medsu.payload.users.ForgotPasswordDTO;
import uz.medsu.payload.users.SignInDTO;
import uz.medsu.payload.users.UserDTO;
import uz.medsu.utils.ResponseMessage;


public interface AuthService {
    ResponseMessage login(SignInDTO signInDTO);
    ResponseMessage signUp(UserDTO userDTO);
    ResponseMessage confirmEmail(EmailConfirmDTO emailDTO);
    ResponseMessage confirmResendCode(String email, CodeType codeType);
    ResponseMessage confirmEmailForResetPassword(EmailConfirmDTO emailDTO);
    ResponseMessage forgotPassword(ForgotPasswordDTO passwordDTO);
}
