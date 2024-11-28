package uz.medsu.utils;

import jakarta.persistence.criteria.Join;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import uz.medsu.entity.Doctor;
import uz.medsu.entity.User;

import java.util.Locale;

public interface Util {

    String[] openUrl = {
            "/auth/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
//            "/image/**"
    };

    static User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
