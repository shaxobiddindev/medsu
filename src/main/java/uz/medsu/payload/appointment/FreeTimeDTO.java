package uz.medsu.payload.appointment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreeTimeDTO {
        private String time;
        private Boolean isBooked;
}
