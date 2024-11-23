package uz.medsu.payload.appointment;

public record ResponseAppointmentDTO(
        Long id,
        Long userId,
        Long doctorId,
        String date,
        String time,
        String status,
        Long invoiceId
) {
}
