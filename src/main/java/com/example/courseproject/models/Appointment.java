package com.example.courseproject.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Appointment {
    //Генератор ID
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //Объекты модели
    // Дата приёма
    @Column(name="date",nullable=false)
    private Date date;

    // Время приёма
    @Column(name="time",nullable=false)
    private LocalTime time;

    // Информация для обратной связи (ФИО + телефон)
    @Column(name="call_info")
    String callbackInfo;

    // Статус приёма
    @Column(name="status", nullable = false)
    private int status;

    // Объект Пациент связанный с приёмом, может быть null
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    // Объект Доктор связанный с приёмом
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    // Получить имя пациента (из callBackInfo при patient = null)
    public String getPatientName() {
        return patient != null ? patient.getSurname() + " " + patient.getName() : callbackInfo.substring(0,callbackInfo.indexOf(" ", callbackInfo.indexOf(" ") + 1));
    }

    // Получить телефон пациента (из callBackInfo при patient = null)
    public String getPatientTelephone() {
        return patient != null ? patient.getTelephone() : callbackInfo.substring(callbackInfo.indexOf(" ", callbackInfo.indexOf(" ") + 1));
    }

}
