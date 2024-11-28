package uz.medsu.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.medsu.config.JwtProvider;
import uz.medsu.enums.CodeType;
import uz.medsu.payload.users.EmailConfirmDTO;
import uz.medsu.payload.users.ForgotPasswordDTO;
import uz.medsu.payload.users.SignInDTO;
import uz.medsu.payload.users.UserDTO;
import uz.medsu.repository.UserRepository;
import uz.medsu.sevice.AuthService;
import uz.medsu.utils.ResponseMessage;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    final JwtProvider jwtProvider;
    final UserRepository userRepository;
    final AuthService authService;


    @PostMapping("/sign-up")
    public ResponseEntity<ResponseMessage> signUp(@RequestBody UserDTO userDTO){
        return ResponseEntity.ok(authService.signUp(userDTO));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody SignInDTO userDTO){
        return ResponseEntity.ok(authService.login(userDTO));
    }

    @PostMapping("/account/resend-code")
    public ResponseEntity<?> resendCode(@RequestBody String email){
        return ResponseEntity.ok(authService.confirmResendCode(email, CodeType.ACCOUNT));
    }

    @PostMapping("/account/confirm")
    public ResponseEntity<?> emailConfirm(@RequestBody EmailConfirmDTO email){
        return ResponseEntity.ok(authService.confirmEmail(email));
    }

    @PostMapping("/password/confirm")
    public ResponseEntity<?> passwordConfirm(@RequestBody EmailConfirmDTO email){
        return ResponseEntity.ok(authService.confirmEmailForResetPassword(email));
    }

    @PostMapping("/password/resend-code")
    public ResponseEntity<?> passwordResendCode(@RequestBody String email){
        return ResponseEntity.ok(authService.confirmResendCode(email, CodeType.ACCOUNT));
    }

    @PostMapping("/password")
    public ResponseEntity<?> passwordChange(@RequestBody ForgotPasswordDTO forgotPasswordDTO){
        return ResponseEntity.ok(authService.forgotPassword(forgotPasswordDTO));
    }
}
