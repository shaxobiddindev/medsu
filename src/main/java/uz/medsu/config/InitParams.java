package uz.medsu.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.medsu.entity.*;
import uz.medsu.enums.Authorities;
import uz.medsu.enums.Days;
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
    private final DaysRepository daysRepository;
    private final TimeRepository timeRepository;

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

            for (Days value : Days.values()) {
                Day day = new Day();
                day.setDay(value);
                daysRepository.save(day);
            }

            for (int i = 10; i < 18; i++) {
                Time time = new Time();
                time.setTime(i + ":00");
                timeRepository.save(time);
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
        }
    }
}
