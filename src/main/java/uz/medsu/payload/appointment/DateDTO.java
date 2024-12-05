package uz.medsu.payload.appointment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateDTO implements Comparable<DateDTO> {
    private LocalDate date;
    private String dateOfWeek;

    @Override
    public int compareTo(DateDTO other) {
        int monthComparison = Integer.compare(this.date.getMonthValue(), other.date.getMonthValue());
        if (monthComparison != 0) {
            return monthComparison;
        }
        return Integer.compare(this.date.getDayOfMonth(), other.date.getDayOfMonth());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateDTO dateDTO = (DateDTO) o;
        return Objects.equals(date, dateDTO.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }
}
