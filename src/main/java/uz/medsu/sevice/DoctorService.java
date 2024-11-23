package uz.medsu.sevice;


import uz.medsu.entity.Appointment;
import uz.medsu.utils.ResponseMessage;

public interface DoctorService {
    ResponseMessage showAppointments(Integer page, Integer size);
    ResponseMessage showAppointments(Long id);
    ResponseMessage rejectAppointment(Long appointmentId);
    ResponseMessage completeAppointment(Long appointmentId);
}
