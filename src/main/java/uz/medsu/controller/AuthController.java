package uz.medsu.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.medsu.config.JwtProvider;
import uz.medsu.payload.SignInDTO;
import uz.medsu.payload.UserDTO;
import uz.medsu.repository.UserRepository;
import uz.medsu.sevice.AuthService;
import uz.medsu.sevice.serviceImpl.AuthServiceImpl;
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
}
