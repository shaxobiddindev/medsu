package uz.medsu.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import uz.medsu.repository.UserRepository;
import uz.medsu.utils.Util;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final UserRepository userRepository;
    private final MyFilter myFilter;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(myFilter, UsernamePasswordAuthenticationFilter.class);
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable());
        http
                .userDetailsService(userDetailsService());
        http
                .authorizeRequests()
                .requestMatchers(Util.openUrl)
                .permitAll()
                .requestMatchers(new String[]{"/admin/**"})
                .hasRole("ADMIN")
                .requestMatchers(new String[]{"/doctor/**"})
                .hasAnyRole("DOCTOR", "ADMIN")
                .requestMatchers(new String[]{"/user/**", "/article/**", "/drug/**", "/order/**", "/image/**"})
                .hasAnyRole("USER", "ADMIN", "DOCTOR")
                .anyRequest()
                .authenticated();
//        http.oauth2Login(oauth2Login ->
//                oauth2Login
//                        .loginPage("/auth/oauth/authorize")
//                        .defaultSuccessUrl("/api/user/profile", true));
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(){
        UserDetailsService userDetailsService =(username)->
                userRepository.findByUsernameOrEmail(username, username).orElseThrow(() -> new RuntimeException("User not found"));
        return userDetailsService;
    }
}

