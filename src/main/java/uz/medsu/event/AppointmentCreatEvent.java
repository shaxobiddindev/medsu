package uz.medsu.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import uz.medsu.entity.Appointment;
import uz.medsu.entity.DrugOrder;

@Getter
public class AppointmentCreatEvent extends ApplicationEvent {
    private final Appointment appointment;

    public AppointmentCreatEvent(Appointment appointment) {
        super(appointment);
        this.appointment = appointment;
    }
}
