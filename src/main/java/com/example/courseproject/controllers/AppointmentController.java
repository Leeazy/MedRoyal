package com.example.courseproject.controllers;

import com.example.courseproject.models.Patient;
import com.example.courseproject.services.AppointmentService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@AllArgsConstructor
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/new")
    public String getNewAppointmentView() {
        return "appointments/newAppointment";
    }

    @PostMapping("/new")
    @ResponseBody
    public String addNewAppointment(
            @AuthenticationPrincipal Patient patient,
            @RequestParam("date") String date,
            @RequestParam("time") String time,
            @RequestParam("doctorId") Long doctorId,
            @RequestParam("callbackInfo") String callbackInfo) {
        System.out.println(patient + " " + date + " " + time + " " + doctorId + " " + callbackInfo);
        Long appointmentId = appointmentService.saveAppointment(patient, date, time, doctorId, callbackInfo);
        return "/appointments/" + appointmentId + "/success";
    }

    @GetMapping("/{id}/success")
    public ModelAndView getSuccessAppointmentView(@PathVariable("id") Long id, ModelAndView modelAndView) {
        return appointmentService.findAppointmentByIdView(id, modelAndView, "appointments/success");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority({'ADMIN'})")
    @ResponseBody
    public void deleteAppointmentById(@PathVariable("id") Long id) {
        appointmentService.deleteAppointment(id);
    }

}
