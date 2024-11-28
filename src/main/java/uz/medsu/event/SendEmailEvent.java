package uz.medsu.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import uz.medsu.payload.EmailMessage;


@Getter
public class SendEmailEvent extends ApplicationEvent {
    private final EmailMessage message;

    public SendEmailEvent(EmailMessage message) {
        super(message);
        this.message= message;
    }
}
