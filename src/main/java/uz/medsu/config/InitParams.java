package uz.medsu.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.medsu.entity.*;
import uz.medsu.enums.Authorities;
import uz.medsu.enums.Gender;
import uz.medsu.enums.Roles;
import uz.medsu.repository.*;

@Component
@RequiredArgsConstructor
public class InitParams implements CommandLineRunner {
    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final CardRepository cardRepository;
    @Value("${spring.sql.init.mode}")
    String mode;


    @Override
    public void run(String... args) throws Exception {
        if (mode.equals("always")) {
            for (Authorities value : Authorities.values()) {
                Authority authority = new Authority();
                authority.setAuthorities(value);
                authorityRepository.save(authority);
            }

            Role superAdmin = new Role();
            superAdmin.setName(Roles.ADMIN);
            superAdmin.setAuthorities(authorityRepository.findAll());

            roleRepository.save(superAdmin);


            User admin = User.builder()
                    .firstName("admin")
                    .lastName("admin")
                    .password(passwordEncoder.encode("admin"))
                    .enabled(true)
                    .email("admin")
                    .role(superAdmin)
                    .profession(Roles.ADMIN)
                    .gender(Gender.MALE)
                    .isNonLocked(true)
                    .locale("en")
                    .age(20)
                    .build();
            userRepository.save(admin);

            Card card = Card.builder()
                    .user(admin)
                    .expiryDate("11/30")
                    .number("9860120105531434")
                    .balance(500.0)
                    .type("HUMO")
                    .build();
            cardRepository.save(card);
        }
    }
}
