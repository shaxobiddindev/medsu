package uz.medsu.sevice;

import uz.medsu.entity.Appointment;
import uz.medsu.utils.ResponseMessage;

public interface AppointmentService{
    ResponseMessage addAppointment(Appointment appointment);
}
