package uz.medsu.sevice.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.hibernate.mapping.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.medsu.config.JwtProvider;
import uz.medsu.entity.Role;
import uz.medsu.entity.User;
import uz.medsu.enums.Authorities;
import uz.medsu.enums.Gender;
import uz.medsu.enums.Roles;
import uz.medsu.payload.ReturnUserDTO;
import uz.medsu.payload.SignInDTO;
import uz.medsu.payload.UserDTO;
import uz.medsu.repository.AuthorityRepository;
import uz.medsu.repository.RoleRepository;
import uz.medsu.repository.UserRepository;
import uz.medsu.sevice.AuthService;
import uz.medsu.utils.I18nUtil;
import uz.medsu.utils.ResponseMessage;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;

    @Override
    public ResponseMessage login(SignInDTO userDTO) {
        User user = userRepository.findByEmail(userDTO.email()).orElseThrow(RuntimeException::new);
        if (!passwordEncoder.matches(userDTO.password(),user.getPassword())){
            throw new RuntimeException(I18nUtil.getMessage("usernameOrPasswordWrong"));
        }
//        if (!user.getPassword().equals(userDTO.password())) {
//            throw new RuntimeException("Wrong password");
//        }
        String token = jwtProvider.generateToken(user);

//        byte[] encode = Base64.getEncoder().encode((user.getUsername() + ":" + user.getPassword()).getBytes());
        return ResponseMessage.builder().data(token).build();
    }

    @Override
    public ResponseMessage signUp(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.email())) {
            throw new RuntimeException("User already exists");
        }
        if (!(userDTO.gender().toUpperCase().equals(Gender.MALE.toString()) || userDTO.gender().toUpperCase().equals(Gender.FEMALE.toString()))) throw new RuntimeException("Invalid gender");
        Role role = Role.builder()
                .name(Roles.USER)
                .authorities(authorityRepository
                        .findAll()
                        .stream()
                        .filter(a -> a.getAuthorities().equals(Authorities.READ)
                                || a.getAuthorities().equals(Authorities.POST))
                        .toList())
                .build();
        roleRepository.save(role);
        User user = User
                .builder()
                .email(userDTO.email())
                .password(passwordEncoder.encode(userDTO.password()))
                .firstName(userDTO.firstName())
                .lastName(userDTO.lastName())
                .age(userDTO.age())
                .gender(Gender.valueOf(userDTO.gender().toUpperCase()))
                .role(role)
                .profession(Roles.USER)
                .isNonLocked(true)
                .enabled(true)
                .locale("en")
                .build();
        userRepository.save(user);
        return ResponseMessage.builder().data(new ReturnUserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getAge(), user.getGender().toString(), user.getRole())).build();
    }
}
