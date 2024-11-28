package uz.medsu.sevice.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.medsu.entity.*;
import uz.medsu.enums.Authorities;
import uz.medsu.enums.DoctorSpeciality;
import uz.medsu.enums.Roles;
import uz.medsu.payload.users.ReturnUserDTO;
import uz.medsu.payload.SetDoctorDTO;
import uz.medsu.payload.users.UserRoleEditDTO;
import uz.medsu.repository.*;
import uz.medsu.sevice.AdminService;
import uz.medsu.utils.I18nUtil;
import uz.medsu.utils.ResponseMessage;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final SpecialityRepository specialityRepository;
    @Value("${my_var.start-time}")
    private String startTime;
    @Value("${my_var.break-time}")
    private String breakTime;
    @Value("${my_var.end-time}")
    private String endTime;

    @Override
    public ResponseMessage roles() {
        return ResponseMessage.builder().data(roleRepository.findAll()).build();
    }

    @Override
    public ResponseMessage setDoctor(SetDoctorDTO doctorDTO) {
        User user = userRepository.findById(doctorDTO.userId()).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("userNotFound")));
        if (user.getProfession().equals(Roles.ADMIN)) throw new RuntimeException(I18nUtil.getMessage("userNotFound"));
        if(user.isEnabled() && user.isAccountNonLocked() && user.isAccountNonExpired() && !user.getRole().getName().equals(Roles.USER)) throw new RuntimeException(I18nUtil.getMessage("userNotFound"));
        if (checkAuthorityId(doctorDTO.authoritiesId()))
            throw new RuntimeException(I18nUtil.getMessage("authorityIdIncorrect"));

        user.getRole().setName(Roles.DOCTOR);
        user.setProfession(Roles.DOCTOR);
        Doctor speciality = Doctor.builder()
                .about(doctorDTO.about())
                .doctorSpecialty(DoctorSpeciality.valueOf(doctorDTO.doctorSpeciality().toUpperCase()))
                .user(user)
                .appointmentPrice(doctorDTO.appointmentPrice())
                .rating(0.0)
                .build();
        user.getRole().setAuthorities(authorityRepository.findAll().stream().filter(a -> doctorDTO.authoritiesId().contains(a.getId()) && List.of(Authorities.EDIT, Authorities.POST, Authorities.READ, Authorities.DELETE).contains(a.getAuthorities())).toList());
        specialityRepository.save(speciality);
        userRepository.save(user);
        return ResponseMessage.builder().success(true).message(I18nUtil.getMessage("userChangedSuccess")).data(new ReturnUserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getAge(), user.getGender().name(), user.getRole(), user.getEnabled(), user.getIsNonLocked())).build();
    }

    @Override
    public ResponseMessage setRole(UserRoleEditDTO roleEditDTO) {
        User user = userRepository.findById(roleEditDTO.userId()).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("userNotFound")));
        if (user.getProfession().equals(Roles.ADMIN)) throw new RuntimeException(I18nUtil.getMessage("userNotFound"));
        if(!(user.isEnabled() && user.isAccountNonLocked() && user.isAccountNonExpired())) throw new RuntimeException(I18nUtil.getMessage("userNotFound"));
        if (checkAuthorityId(roleEditDTO.authorityIds()))
            throw new RuntimeException(I18nUtil.getMessage("authorityIdIncorrect"));

        user.getRole().setName(Roles.valueOf(roleEditDTO.roleName().toUpperCase()));
        user.setProfession(Roles.valueOf(roleEditDTO.roleName().toUpperCase()));
        user.getRole().setAuthorities(authorityRepository.findAll().stream().filter(a -> roleEditDTO.authorityIds().contains(a.getId()) && List.of(Authorities.EDIT, Authorities.POST, Authorities.READ, Authorities.DELETE).contains(a.getAuthorities())).toList());
        userRepository.save(user);
        return ResponseMessage.builder().success(true).data(new ReturnUserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getAge(), user.getGender().name(), user.getRole(), user.getEnabled(), user.getIsNonLocked())).build();
    }

    @Override
    public ResponseMessage getDoctor(Integer page, Integer size) {
        List<User> doctors = userRepository.findAllByProfession(Roles.DOCTOR, PageRequest.of(page, size)).stream().toList();

        return ResponseMessage.builder().success(true).data(usersReturn(doctors)).build();
    }

    @Override
    public ResponseMessage getAllUsers(Integer page, Integer size) {
        List<User> users = userRepository.findAll(PageRequest.of(page, size)).stream().toList();
        return ResponseMessage.builder().success(true).data(usersReturn(users)).build();
    }

    @Override
    public ResponseMessage getAdmins(Integer page, Integer size) {
        List<User> admins = userRepository.findAllByProfession(Roles.ADMIN, PageRequest.of(page, size)).stream().toList();
        return ResponseMessage.builder().success(true).data(usersReturn(admins)).build();
    }

    @Override
    public ResponseMessage getUsers(Integer page, Integer size) {
        List<User> users = userRepository.findAllByProfession(Roles.USER, PageRequest.of(page, size)).stream().toList();
        return ResponseMessage.builder().success(true).data(usersReturn(users)).build();
    }

    @Override
    public ResponseMessage blockUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("userNotFound")));
        user.setIsNonLocked(false);
        userRepository.save(user);
        return ResponseMessage.builder().success(true).message(I18nUtil.getMessage("userBlocked")).build();
    }

    @Override
    public ResponseMessage unblockUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("userNotFound")));
        user.setIsNonLocked(true);
        userRepository.save(user);
        return ResponseMessage.builder().success(true).message(I18nUtil.getMessage("unblockUser")).build();
    }

    @Override
    public ResponseMessage enableUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("userNotFound")));
        user.setEnabled(true);
        userRepository.save(user);
        return ResponseMessage.builder().success(true).message(I18nUtil.getMessage("enableUser")).build();
    }

    private List<ReturnUserDTO> usersReturn(List<User> users) {
        List<ReturnUserDTO> returnUsers = new ArrayList<>();
        for (User user : users) {
            returnUsers.add(new ReturnUserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getAge(), user.getGender().name(), user.getRole(), user.getEnabled(), user.getIsNonLocked()));
        }
        return returnUsers;
    }

    private boolean checkAuthorityId(List<Long> ids) {
        for (Long id : ids) {
            if (!authorityRepository.existsById(id)) return true;
        }
        return false;
    }


    private Optional<Timestamp> checkDate(String date) {
        try {
            LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return !DayOfWeek.SUNDAY.equals(localDate.getDayOfWeek()) ? Optional.of(Timestamp.valueOf(LocalDateTime.of(localDate, LocalTime.of(0,0)))) : Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException(I18nUtil.getMessage("dateFormatError"));
        }
    }

    private Optional<String> formatTimes(String time) {
        try {
            if (!time.split(":")[1].equals("00"))throw new RuntimeException(I18nUtil.getMessage("timeFormatError"));
            int start = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm")).getHour();
            int breakHour = LocalTime.parse(breakTime, DateTimeFormatter.ofPattern("HH:mm")).getHour();
            int end = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm")).getHour();
            int inputTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm")).getHour();
            return (start <= inputTime && end > inputTime && breakHour != inputTime) ? Optional.of(time) : Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException(I18nUtil.getMessage("timeFormatError"));
        }
    }
}
