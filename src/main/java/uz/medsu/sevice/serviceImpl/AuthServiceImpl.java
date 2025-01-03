package uz.medsu.sevice.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.medsu.config.JwtProvider;
import uz.medsu.entity.*;
import uz.medsu.enums.Authorities;
import uz.medsu.enums.CodeType;
import uz.medsu.enums.Gender;
import uz.medsu.enums.Roles;
import uz.medsu.event.SendEmailEvent;
import uz.medsu.payload.EmailMessage;
import uz.medsu.payload.users.*;
import uz.medsu.repository.*;
import uz.medsu.sevice.AuthService;
import uz.medsu.sevice.EmailService;
import uz.medsu.utils.I18nUtil;
import uz.medsu.utils.ResponseMessage;
import uz.medsu.utils.Util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final CodeRepository codeRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordKeyRepository passwordKeyRepository;
    private final EmailCheckerService emailCheckerService;
    private final FakeUserRepository fakeUserRepository;
    private final Random random = new Random();

    @Override
    public ResponseMessage login(SignInDTO userDTO) {
        User user = userRepository.findByUsernameOrEmail(userDTO.username(), userDTO.username()).orElseThrow(() -> new RuntimeException("Username or password is incorrect!"));
        if (!passwordEncoder.matches(userDTO.password(), user.getPassword())) {
            throw new RuntimeException(I18nUtil.getMessage("usernameOrPasswordWrong", user));
        }
//        if (!user.getPassword().equals(userDTO.password())) {
//            throw new RuntimeException("Wrong password");
//        }
        String token = jwtProvider.generateToken(userDTO.username());

//        byte[] encode = Base64.getEncoder().encode((user.getUsername() + ":" + user.getPassword()).getBytes());
        return ResponseMessage.builder().success(true).message("Login successfully!").data(token).build();
    }

    @Override
    public ResponseMessage signUp(UserDTO userDTO) {
        if (!emailCheckerService.isValidEmailFormat(userDTO.email()) || !emailCheckerService.isValidEmailAddress(userDTO.email()))
            throw new RuntimeException("Invalid email address!");
        if (userRepository.existsByUsername(userDTO.username()))
            throw new RuntimeException("Username already exists!");
        if (userRepository.existsByEmail(userDTO.email()))
            throw new RuntimeException("Email already exists");

//        if (!(userDTO.gender().toUpperCase().equals(Gender.MALE.toString()) || userDTO.gender().toUpperCase().equals(Gender.FEMALE.toString())))
//            throw new RuntimeException("Invalid gender");
        Optional<FakeUser> optionalFakeUser = fakeUserRepository.findByEmail(userDTO.email());
        FakeUser user;
        if (optionalFakeUser.isEmpty()){
            user = FakeUser
                    .builder()
                    .email(userDTO.email())
                    .username(userDTO.username())
                    .password(userDTO.password())
//                .firstName(userDTO.firstName())
//                .lastName(userDTO.lastName())
//                .age(userDTO.age())
                    .build();
            fakeUserRepository.save(user);
        }
        user = optionalFakeUser.get();

        ResponseMessage responseMessage = confirmResendCode(user.getEmail(), CodeType.ACCOUNT);
        return ResponseMessage.builder().success(true).message("Sign Up successfully, " + responseMessage.getMessage()).build();
    }

    @Override
    public ResponseMessage confirmEmail(EmailConfirmDTO emailDTO) {
        Code code = codeRepository.findByEmailAndType(emailDTO.email(), CodeType.ACCOUNT).orElseThrow(() -> new RuntimeException("Verification code is invalid or expired!"));
        if (code.getExpired().toLocalDateTime().isBefore(LocalDateTime.now()) ||
                !code.getCode().equals(emailDTO.code())
        ) throw new RuntimeException("Verification code is invalid or expired!");
        FakeUser fakeUser = fakeUserRepository.findByEmail(emailDTO.email()).orElseThrow(() -> new RuntimeException("User not found!"));
        Role role = Role.builder()
                .name(Roles.USER)
                .authorities(authorityRepository
                        .findAll()
                        .stream()
                        .filter(a -> a.getAuthorities().equals(Authorities.READ)
                                || a.getAuthorities().equals(Authorities.POST)
                                || a.getAuthorities().equals(Authorities.EDIT)
                                || a.getAuthorities().equals(Authorities.DELETE)
                        )
                        .toList())
                .build();
        roleRepository.save(role);
        User user = User
                .builder()
                .email(fakeUser.getEmail())
                .username(fakeUser.getUsername())
                .password(passwordEncoder.encode(fakeUser.getPassword()))
//                .firstName(userDTO.firstName())
//                .lastName(userDTO.lastName())
//                .age(userDTO.age())
                .gender(Gender.MALE)
                .role(role)
                .profession(Roles.USER)
                .isNonLocked(true)
                .enabled(true)
                .locale("en")
                .build();
        userRepository.save(user);
        return ResponseMessage.builder().success(true).message(I18nUtil.getMessage("accountConfirmed", user)).build();
    }

    @Override
    public ResponseMessage confirmResendCode(String email, CodeType codeType) {
        Optional<Code> optionalCode = codeRepository.findByEmailAndType(email, codeType);
        if (optionalCode.isPresent() && optionalCode.get().getExpired().toLocalDateTime().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Code doesn't expired!");
        }
        Code code;
        if (optionalCode.isPresent() && optionalCode.get().getExpired().toLocalDateTime().isBefore(LocalDateTime.now())) {
            code = optionalCode.get();
            code.setCode(generateCode());
            code.setExpired(Timestamp.valueOf(LocalDateTime.now().plusMinutes(1)));
        } else {
            code = Code
                    .builder()
                    .code(generateCode())
                    .type(codeType)
                    .expired(Timestamp.valueOf(LocalDateTime.now().plusMinutes(1)))
                    .email(email)
                    .build();
        }
        codeRepository.save(code);

        eventPublisher.publishEvent(new SendEmailEvent(
                new EmailMessage(
                        code.getEmail(),
                        "Confirm your email!",
                        code.getCode()
                )
        ));
        return ResponseMessage
                .builder()
                .success(true)
                .message("Confirmation code send to your email!")
                .build();
    }

    @Override
    public ResponseMessage confirmEmailForResetPassword(EmailConfirmDTO emailDTO) {
        Code code = codeRepository.findByEmailAndType(emailDTO.email(), CodeType.PASSWORD).orElseThrow(() -> new RuntimeException("Verification code is invalid or expired!"));
        if (code.getExpired().toLocalDateTime().isBefore(LocalDateTime.now()) ||
                !code.getCode().equals(emailDTO.code())
        ) throw new RuntimeException("Verification code is invalid or expired!");
        User user = userRepository.findByEmail(emailDTO.email()).orElseThrow(() -> new RuntimeException("User not found!"));
        StringBuilder sb = new StringBuilder();
        String key = sb
                .append(user.getEmail())
                .append("/")
                .append(LocalDateTime.now().plusMinutes(5))
                .toString();
        System.out.println(key);
        String token = jwtProvider.generateToken(Base64.getEncoder().encodeToString(key.getBytes()));
        PasswordKey passwordKey = new PasswordKey();
        passwordKey.setKey(key);
        passwordKeyRepository.save(passwordKey);
        return ResponseMessage.builder().success(true).message("Your token for password change, token expires after 5 minutes").data(token).build();
    }

    @Override
    public ResponseMessage forgotPassword(ForgotPasswordDTO passwordDTO) {
        String subject = new String(Base64.getDecoder().decode(jwtProvider.getSubject(passwordDTO.token())));
        String[] split = subject.split("/");
        System.out.println(subject);
        System.out.println(split[1]);
        LocalDateTime expire = LocalDateTime.parse(split[1]);
        if (expire.isBefore(LocalDateTime.now())) throw new RuntimeException("Token expired!");
        PasswordKey passwordKey = passwordKeyRepository.findByKey(subject).orElseThrow(() -> new RuntimeException("Invalid token!"));
        User user = userRepository.findByEmail(passwordDTO.email()).orElseThrow(() -> new RuntimeException("User not found!"));
        user.setPassword(passwordEncoder.encode(passwordDTO.password()));
        userRepository.save(user);
        passwordKeyRepository.delete(passwordKey);
        return ResponseMessage.builder().success(true).message("Your password has been changed!").build();
    }

    private String generateCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(random.nextInt(0, 10));
        }
        return sb.toString();
    }
}
