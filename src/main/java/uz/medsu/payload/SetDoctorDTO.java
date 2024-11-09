package uz.medsu.payload;

import java.util.List;

public record SetDoctorDTO(Long userId, List<Long> authoritiesId, String doctorSpeciality, String about, String startWorkDate, String endWorkDate, String startWorkTime, String breakTime, String endWorkTime) {
}
