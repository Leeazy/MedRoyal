package com.example.courseproject.services;

import com.example.courseproject.models.Appointment;
import com.example.courseproject.models.Patient;
import com.example.courseproject.repo.AppointmentRepository;
import com.example.courseproject.repo.DoctorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Objects;

@AllArgsConstructor
@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    private final DoctorRepository doctorRepository;

    // Найти приём по id
    private Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Запись на приём с id " + id + "не была найдена!"));
    }

    // Найти указанную страницу c приёмом по id
    public ModelAndView findAppointmentByIdView(Long id, ModelAndView modelAndView, String viewName) {
        Appointment appointmentById = getAppointmentById(id);
        modelAndView.addObject("appointment", appointmentById);
        modelAndView.setViewName(viewName);
        return modelAndView;
    }

    // Сохранить приём
    public Long saveAppointment(Patient patient, String date, String time, Long doctorId, String callbackInfo) {
        Appointment newAppointment = new Appointment();

        // Date
        java.util.Date parsed = null;
        try {
            parsed = new SimpleDateFormat("dd-MM-yyyy").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date sqlDate = new Date(Objects.requireNonNull(parsed).getTime());
        newAppointment.setDate(sqlDate);

        // Time
        LocalTime localTime = LocalTime.parse(time + ":00");
        newAppointment.setTime(localTime);

        // Doctor
        newAppointment.setDoctor(doctorRepository.findById(doctorId).orElseThrow(() -> new EntityNotFoundException("Доктор с id " + doctorId + "не был найден!")));

        // Patient
        if (patient != null) {
            newAppointment.setPatient(patient);
            newAppointment.setStatus(0);
        }
        else newAppointment.setStatus(1);

        if (!callbackInfo.isBlank()) newAppointment.setCallbackInfo(callbackInfo);

        appointmentRepository.save(newAppointment);

        return newAppointment.getId();
    }

    // Удалить приём
    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

}
