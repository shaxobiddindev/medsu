package uz.medsu.sevice.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.medsu.entity.Appointment;
import uz.medsu.enums.AppointmentStatus;
import uz.medsu.enums.PaymentStatus;
import uz.medsu.payload.appointment.ResponseAppointmentDTO;
import uz.medsu.payload.doctors.ResponseDoctorDTO;
import uz.medsu.repository.AppointmentRepository;
import uz.medsu.repository.InvoiceRepository;
import uz.medsu.repository.SpecialityRepository;
import uz.medsu.sevice.DoctorService;
import uz.medsu.utils.I18nUtil;
import uz.medsu.utils.ResponseMessage;
import uz.medsu.utils.Util;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final AppointmentRepository appointmentRepository;
    private final SpecialityRepository doctorRepository;
    private final InvoiceRepository invoiceRepository;

    @Override
    public ResponseMessage showAppointments(Integer page, Integer size) {
            List<ResponseAppointmentDTO> appointmentDTOS = appointmentRepository.findAllByDoctor(doctorRepository.findByUser(Util.getCurrentUser()).orElseThrow(()-> new RuntimeException(I18nUtil.getMessage("doctorNotFound"))),PageRequest.of(page, size)).toList().stream().map(appointment -> {
            ResponseDoctorDTO responseDoctorDTO = new ResponseDoctorDTO(
                    appointment.getDoctor().getId(),
                    appointment.getDoctor().getAbout(),
                    appointment.getDoctor().getUser().getFirstName(),
                    appointment.getDoctor().getUser().getLastName(),
                    appointment.getDoctor().getDoctorSpecialty().toString(),
                    appointment.getDoctor().getAppointmentPrice(),
                    appointment.getDoctor().getRating(),
                    null,
                    null,
                    appointment.getDoctor().getUser().getImageUrl()
            );
            return new ResponseAppointmentDTO(
                    appointment.getId(),
                    appointment.getUser().getId(),
                    responseDoctorDTO,
                    appointment.getDate().toLocalDateTime().toLocalDate().toString(),
                    appointment.getTime(),
                    appointment.getStatus().toString(),
                    appointment.getInvoice().getId()
            );
        }).toList();
        return ResponseMessage
                .builder()
                .success(true)
                .data(appointmentDTOS)
                .build();
    }

    @Override
    public ResponseMessage showAppointments(Long id) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("appointmentNotFound")));
        if (!appointment.getDoctor().getId().equals(doctorRepository.findByUser(Util.getCurrentUser()).orElseThrow(()->new RuntimeException(I18nUtil.getMessage("doctorNotFound"))).getId()))
            throw new RuntimeException(I18nUtil.getMessage("appointmentNotFound"));
        ResponseDoctorDTO responseDoctorDTO = new ResponseDoctorDTO(
                appointment.getDoctor().getId(),
                appointment.getDoctor().getAbout(),
                appointment.getDoctor().getUser().getFirstName(),
                appointment.getDoctor().getUser().getLastName(),
                appointment.getDoctor().getDoctorSpecialty().toString(),
                appointment.getDoctor().getAppointmentPrice(),
                appointment.getDoctor().getRating(),
                null,
                null,
                appointment.getDoctor().getUser().getImageUrl()
        );
        return ResponseMessage
                .builder()
                .success(true)
                .data(new ResponseAppointmentDTO(
                        appointment.getId(),
                        appointment.getUser().getId(),
                        responseDoctorDTO,
                        appointment.getDate().toLocalDateTime().toLocalDate().toString(),
                        appointment.getTime(),
                        appointment.getStatus().toString(),
                        appointment.getInvoice().getId()
                ))
                .build();
    }

    @Override
    public ResponseMessage rejectAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("appointmentNotFound")));
        if (!appointment.getDoctor().getId().equals(doctorRepository.findById(Util.getCurrentUser().getId()).orElseThrow(()-> new RuntimeException(I18nUtil.getMessage("doctorNotFound"))).getId())) throw  new RuntimeException(I18nUtil.getMessage("setStatusError"));
        if (appointment.getInvoice().getAmount() > 0) throw new RuntimeException(I18nUtil.getMessage("paidAppointmentChangeError"));
        appointment.setStatus(AppointmentStatus.REJECTED);
        appointmentRepository.save(appointment);
        appointment.getInvoice().setStatus(PaymentStatus.CANCELLED);
        invoiceRepository.save(appointment.getInvoice());
        ResponseDoctorDTO responseDoctorDTO = new ResponseDoctorDTO(
                appointment.getDoctor().getId(),
                appointment.getDoctor().getAbout(),
                appointment.getDoctor().getUser().getFirstName(),
                appointment.getDoctor().getUser().getLastName(),
                appointment.getDoctor().getDoctorSpecialty().toString(),
                appointment.getDoctor().getAppointmentPrice(),
                appointment.getDoctor().getRating(),
                null,
                null,
                appointment.getDoctor().getUser().getImageUrl()
        );
        return ResponseMessage
                .builder()
                .success(true)
                .data(new ResponseAppointmentDTO(appointment.getId(),appointment.getUser().getId(), responseDoctorDTO, appointment.getDate().toLocalDateTime().toLocalDate().toString(), appointment.getTime(), appointment.getStatus().toString(), appointment.getInvoice().getId()))
                .build();
    }

    @Override
    public ResponseMessage completeAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("appointmentNotFound")));
        if (!appointment.getDoctor().getId().equals(doctorRepository.findById(Util.getCurrentUser().getId()).orElseThrow(()-> new RuntimeException(I18nUtil.getMessage("doctorNotFound"))).getId())) throw  new RuntimeException(I18nUtil.getMessage("setStatusError"));
        if (appointment.getInvoice().getAmount() < appointment.getDoctor().getAppointmentPrice()) throw new RuntimeException(I18nUtil.getMessage("setStatusError"));
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);
        ResponseDoctorDTO responseDoctorDTO = new ResponseDoctorDTO(
                appointment.getDoctor().getId(),
                appointment.getDoctor().getAbout(),
                appointment.getDoctor().getUser().getFirstName(),
                appointment.getDoctor().getUser().getLastName(),
                appointment.getDoctor().getDoctorSpecialty().toString(),
                appointment.getDoctor().getAppointmentPrice(),
                appointment.getDoctor().getRating(),
                null,
                null,
                appointment.getDoctor().getUser().getImageUrl()
        );
        return ResponseMessage
                .builder()
                .success(true)
                .data(new ResponseAppointmentDTO(appointment.getId(), appointment.getUser().getId(), responseDoctorDTO, appointment.getDate().toLocalDateTime().toLocalDate().toString(), appointment.getTime(), appointment.getStatus().toString(), appointment.getInvoice().getId()))
                .build();
    }
}
