package uz.medsu.sevice.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.medsu.entity.*;
import uz.medsu.enums.Days;
import uz.medsu.enums.DoctorSpeciality;
import uz.medsu.enums.Roles;
import uz.medsu.payload.ReturnUserDTO;
import uz.medsu.payload.SetDoctorDTO;
import uz.medsu.payload.UserRoleEditDTO;
import uz.medsu.repository.*;
import uz.medsu.sevice.AdminService;
import uz.medsu.utils.I18nUtil;
import uz.medsu.utils.ResponseMessage;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final DaysRepository daysRepository;
    private final SpecialityRepository specialityRepository;

    @Override
    public ResponseMessage roles() {
        return ResponseMessage.builder().data(roleRepository.findAll()).build();
    }

    @Override
    public ResponseMessage setDoctor(SetDoctorDTO doctorDTO) {
        User user = userRepository.findById(doctorDTO.userId()).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("userNotFound")));
        if (checkAuthorityId(doctorDTO.authoritiesId())) throw new RuntimeException(I18nUtil.getMessage("authorityIdIncorrect"));

        user.getRole().setName(Roles.DOCTOR);
        user.setProfession(Roles.DOCTOR);
        Speciality speciality = Speciality.builder()
                .about(doctorDTO.about())
                .workTimes(formatTimes(doctorDTO.startWorkTime(), doctorDTO.breakTime(), doctorDTO.endWorkTime()))
                .workDays(formatDays(doctorDTO.startWorkDate(), doctorDTO.endWorkDate()))
                .doctorSpecialty(DoctorSpeciality.valueOf(doctorDTO.doctorSpeciality().toUpperCase()))
                .userId(user.getId())
                .build();
        user.getRole().setAuthorities(authorityRepository.findAll().stream().filter(a -> doctorDTO.authoritiesId().contains(a.getId())).toList());
        specialityRepository.save(speciality);
        userRepository.save(user);
        return ResponseMessage.builder().success(true).message(I18nUtil.getMessage("userChangedSuccess")).data(new ReturnUserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getAge(), user.getGender().name(), user.getRole())).build();
    }

    @Override
    public ResponseMessage setRole(UserRoleEditDTO roleEditDTO) {
        return null;
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

    private List<ReturnUserDTO> usersReturn(List<User> users) {
        List<ReturnUserDTO> returnUsers = new ArrayList<>();
        for (User user : users) {
            returnUsers.add(new ReturnUserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getAge(), user.getGender().name(), user.getRole()));
        }
        return returnUsers;
    }

    private boolean checkAuthorityId(List<Long> ids) {
        for (Long id : ids) {
            if (!authorityRepository.existsById(id)) return true;
        }
        return false;
    }

    private List<Day> formatDays(String start, String end) {
        Day startDay;
        Day endDay;
        try{
            startDay = daysRepository.findByDay(Days.valueOf(start.toUpperCase())).orElseThrow(RuntimeException::new);
            endDay = daysRepository.findByDay(Days.valueOf(end.toUpperCase())).orElseThrow(RuntimeException::new);
        }
        catch (Exception e){
            throw new RuntimeException(I18nUtil.getMessage("weekdaysError"));
        }
        List<Day> all = daysRepository.findAll();
        List<Day> days = new ArrayList<>();

        if (startDay.getId() > endDay.getId()) {
            for (long i = startDay.getId(); i <= 7; i++) {
                days.add(all.get((int) (i-1)));
            }
            for (long i = 1; i <= endDay.getId(); i++) {
                days.add(all.get((int) (i-1)));
            }
        }else {
            for (long i = startDay.getId(); i <= endDay.getId(); i++) {
                days.add(all.get((int) (i-1)));
            }
        }

        return days;
    }

    private List<Time> formatTimes(String start, String breakTime, String end ) {
        List<Time> workTimes = new ArrayList<>();

        try {
            int first = Integer.parseInt(start.split(":")[0]);
            int br = Integer.parseInt(breakTime.split(":")[0]);
            int last = Integer.parseInt(end.split(":")[0]);
            LocalTime breakLocal = formatTime(breakTime);
            for (int i = first; i < last; i++) {
                if (i == br)continue;
                Time time = new Time();
                LocalTime startTime = formatTime(i+":00");
                time.setTime(startTime.format(DateTimeFormatter.ofPattern("HH:mm")));
                workTimes.add(time);
            }

        }catch (Exception e){
            throw new RuntimeException(I18nUtil.getMessage("timeFormatError"));
        }

        return workTimes;
    }

    private LocalTime formatTime(String time) {
        return LocalTime.parse(time);
    }
}
