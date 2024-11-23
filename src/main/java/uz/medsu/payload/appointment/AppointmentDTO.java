package uz.medsu.payload.appointment;

public record AppointmentDTO(
    Long doctorId,
    String date,
    String time
) {
}
