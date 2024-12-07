package uz.medsu.sevice.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.medsu.entity.*;
import uz.medsu.enums.AppointmentStatus;
import uz.medsu.enums.Authorities;
import uz.medsu.enums.DoctorSpeciality;
import uz.medsu.enums.Roles;
import uz.medsu.payload.users.LocationDTO;
import uz.medsu.payload.users.ReturnUserDTO;
import uz.medsu.payload.SetDoctorDTO;
import uz.medsu.payload.users.UserDTO;
import uz.medsu.payload.users.UserRoleEditDTO;
import uz.medsu.repository.*;
import uz.medsu.sevice.AdminService;
import uz.medsu.utils.I18nUtil;
import uz.medsu.utils.ResponseMessage;
import uz.medsu.utils.Util;

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
    private final EmailCheckerService emailCheckerService;
    private final AppointmentRepository appointmentRepository;
    private final RatingRepository ratingRepository;
    private final SpecialityRepository doctorRepository;
    private final LocationRepository locationRepository;
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
        if (user.isEnabled() && user.isAccountNonLocked() && user.isAccountNonExpired() && !user.getRole().getName().equals(Roles.USER))
            throw new RuntimeException(I18nUtil.getMessage("userNotFound"));
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
        user.getRole().setAuthorities(authorityRepository.findAll().stream().filter(a -> doctorDTO.authoritiesId().contains(a.getId()) && List.of(Authorities.EDIT,
                Authorities.POST,
                Authorities.READ,
                Authorities.DELETE).contains(a.getAuthorities())).toList());
        specialityRepository.save(speciality);
        userRepository.save(user);
        return ResponseMessage.builder().success(true).message(I18nUtil.getMessage("userChangedSuccess")).data(new ReturnUserDTO(user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getAge(),
                user.getGender().name(),
                user.getRole(),
                user.getEnabled(),
                user.getIsNonLocked(),
                user.getImageUrl())).build();
    }

    @Override
    public ResponseMessage setRole(UserRoleEditDTO roleEditDTO) {
        User user = userRepository.findById(roleEditDTO.userId()).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("userNotFound")));
        if (user.getProfession().equals(Roles.ADMIN)) throw new RuntimeException(I18nUtil.getMessage("userNotFound"));
        if (!(user.isEnabled() && user.isAccountNonLocked() && user.isAccountNonExpired()))
            throw new RuntimeException(I18nUtil.getMessage("userNotFound"));
        if (checkAuthorityId(roleEditDTO.authorityIds()))
            throw new RuntimeException(I18nUtil.getMessage("authorityIdIncorrect"));

        user.getRole().setName(Roles.valueOf(roleEditDTO.roleName().toUpperCase()));
        user.setProfession(Roles.valueOf(roleEditDTO.roleName().toUpperCase()));
        user.getRole().setAuthorities(authorityRepository.findAll().stream().filter(a -> roleEditDTO.authorityIds().contains(a.getId()) && List.of(Authorities.EDIT,
                Authorities.POST,
                Authorities.READ,
                Authorities.DELETE).contains(a.getAuthorities())).toList());
        userRepository.save(user);
        return ResponseMessage.builder().success(true).data(new ReturnUserDTO(user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getAge(),
                user.getGender().name(),
                user.getRole(),
                user.getEnabled(),
                user.getIsNonLocked(),
                user.getImageUrl())).build();
    }

    @Override
    public ResponseMessage getDoctor(Integer page,
                                     Integer size) {
        List<User> doctors = userRepository.findAllByProfession(Roles.DOCTOR,
                PageRequest.of(page,
                        size)).stream().toList();

        return ResponseMessage.builder().success(true).data(usersReturn(doctors)).build();
    }

    @Override
    public ResponseMessage getAllUsers(Integer page,
                                       Integer size) {
        List<User> users = userRepository.findAll(PageRequest.of(page,
                size)).stream().toList();
        return ResponseMessage.builder().success(true).data(usersReturn(users)).build();
    }

    @Override
    public ResponseMessage getAdmins(Integer page,
                                     Integer size) {
        List<User> admins = userRepository.findAllByProfession(Roles.ADMIN,
                PageRequest.of(page,
                        size)).stream().toList();
        return ResponseMessage.builder().success(true).data(usersReturn(admins)).build();
    }

    @Override
    public ResponseMessage getUsers(Integer page,
                                    Integer size) {
        List<User> users = userRepository.findAllByProfession(Roles.USER,
                PageRequest.of(page,
                        size)).stream().toList();
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

    @Override
    public ResponseMessage addUser(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.username()))
            throw new RuntimeException("Username already exists!");
        if (userRepository.existsByEmail(userDTO.email()))
            throw new RuntimeException("Email already exists");
        User user = User
                .builder()
                .email(userDTO.email())
                .password(userDTO.password())
                .build();
        return ResponseMessage
                .builder()
                .success(true)
                .data(
                        new ReturnUserDTO(
                                user.getId(),
                                user.getFirstName(),
                                user.getLastName(),
                                user.getUsername(),
                                user.getEmail(),
                                user.getAge(),
                                user.getGender().name(),
                                user.getRole(),
                                user.getEnabled(),
                                user.getIsNonLocked(),
                                user.getImageUrl()
                        )
                )
                .build();
    }

    @Override
    public ResponseMessage deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("userNotFound")));
        Role role = user.getRole();
        userRepository.deleteById(id);
        roleRepository.delete(role);
        return ResponseMessage.builder().success(true).message("User deleted! ID: " + id).build();
    }

    @Override
    public ResponseMessage setRating(Long id, Double mark) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("userNotFound")));
        if (!appointment.getStatus().equals(AppointmentStatus.COMPLETED)) throw new RuntimeException("Appointment is not completed");
        Rating rating = Rating
                .builder()
                .appointmentId(appointment.getId())
                .rating(mark)
                .doctorId(appointment.getDoctor().getId())
                .userId(appointment.getUser().getId())
                .build();
        ratingRepository.save(rating);
        Doctor doctor = doctorRepository.findById(appointment.getDoctor().getId()).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("doctorNotFound")));
        doctor.setRating(ratingRepository.sumRatingByDoctorId(doctor.getId()));
        doctorRepository.save(doctor);
        return ResponseMessage.builder().success(true).message(I18nUtil.getMessage("appointmentRateSuccess")).build();
    }

    @Override
    public ResponseMessage setLocation(Long userId, LocationDTO locationDTO) {
        Optional<Location> optionalLocation = locationRepository.findByUser_Id(userId);
        Location location;
        location = optionalLocation
                .orElseGet(() ->
                        Location
                                .builder()
                                .user(userRepository.findById(userId).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("userNotFound"))))
                                .build()
                );
        location.setLongitude(locationDTO.longitude());
        location.setLatitude(locationDTO.latitude());
        locationRepository.save(location);
        return ResponseMessage
                .builder()
                .success(true)
                .message(I18nUtil.getMessage("locationUpdated"))
                .build();
    }

    @Override
    public ResponseMessage getLocation(Long userId) {
        Location location = locationRepository.findByUser_Id(userId).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("locationNotFound")));
        return ResponseMessage
                .builder()
                .success(true)
                .data(
                        new LocationDTO(
                                location.getLatitude(),
                                location.getLongitude()
                        )
                )
                .build();
    }

    @Override
    public ResponseMessage doctorLocation(Long id, LocationDTO locationDTO) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("userNotFound")));
        Optional<Location> optionalLocation = locationRepository.findByUser(user);
        Location location = optionalLocation
                .orElseGet(() ->
                        Location
                                .builder()
                                .user(Util.getCurrentUser())
                                .build()
                );
        location.setLongitude(locationDTO.longitude());
        location.setLatitude(locationDTO.latitude());
        locationRepository.save(location);
        return ResponseMessage
                .builder()
                .success(true)
                .message(I18nUtil.getMessage("locationUpdated"))
                .build();
    }

    private List<ReturnUserDTO> usersReturn(List<User> users) {
        List<ReturnUserDTO> returnUsers = new ArrayList<>();
        for (User user : users) {
            returnUsers.add(new ReturnUserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail(), user.getAge(), user.getGender().name(), user.getRole(), user.getEnabled(), user.getIsNonLocked(), user.getImageUrl()));
        }
        return returnUsers;
    }

    private boolean checkAuthorityId(List<Long> ids) {
        for (Long id : ids) {
            if (!authorityRepository.existsById(id)) return true;
        }
        return false;
    }
}
