package uz.medsu.payload.appointment;

import uz.medsu.payload.doctors.ResponseDoctorDTO;

public record ResponseAppointmentDTO(
        Long id,
        Long userId,
        ResponseDoctorDTO doctorDTO,
        String date,
        String time,
        String status,
        Long invoiceId
) {
}
