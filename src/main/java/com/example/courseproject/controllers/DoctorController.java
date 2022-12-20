package com.example.courseproject.controllers;

import com.example.courseproject.models.Doctor;
import com.example.courseproject.models.Patient;
import com.example.courseproject.services.DoctorService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
@AllArgsConstructor
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping("/new")
    @PreAuthorize("hasAnyAuthority({'ADMIN'})")
    public String getNewDoctorView(@ModelAttribute("doctor") Doctor doctor) {
        return "doctors/newDoctor";
    }

    @PostMapping("/new")
    @PreAuthorize("hasAnyAuthority({'ADMIN'})")
    public ModelAndView addNewDoctor(@ModelAttribute("doctor") @Valid Doctor doctor,
                                     BindingResult bindingResult, ModelAndView modelAndView) {
        return doctorService.saveNewDoctor(doctor, bindingResult, modelAndView);
    }

    @GetMapping("/{id}/resume")
    public ModelAndView getDoctorResumeByIdView(@PathVariable("id") Long doctorId, ModelAndView modelAndView) {
        return doctorService.findDoctorByIdView(doctorId, modelAndView, "doctors/resume");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority({'ADMIN'})")
    public String deleteDoctorById(@PathVariable("id") Long id) {
        doctorService.deleteDoctorById(id);
        return "redirect:/doctors/all";
    }

    @GetMapping("/{id}/json")
    @ResponseBody
    public String getDoctorByIdJson(@AuthenticationPrincipal Patient patient, @PathVariable("id") Long id) {
        return doctorService.findDoctorByIdJson(patient, id);
    }

    @GetMapping("/all/page/{pageNumber}")
    @ResponseBody
    public String getDoctorsWithPageJson(@PathVariable("pageNumber") Integer pageNumber) {
        return doctorService.findDoctorsWithPageJson(pageNumber);
    }

    @GetMapping("/by-search")
    @ResponseBody
    public String getDoctorsBySearchWithPageJson(@RequestParam("search") String search,
                                                 @RequestParam("pageNumber") Integer pageNumber) {
        return doctorService.findDoctorsBySearchWithPageJson(search, pageNumber);
    }

    @GetMapping("/by-speciality")
    @ResponseBody
    public String getDoctorsBySpecialityJson(@RequestParam("speciality") String speciality) {
        return doctorService.findDoctorsBySpecialityJson(speciality);
    }

    @GetMapping("/by-expanded-search")
    @ResponseBody
    public String getDoctorsByExpandedSearchWithPageJson(@RequestParam("fullName") String fullName,
                                                         @RequestParam("speciality") String speciality,
                                                         @RequestParam("pageNumber") Integer pageNumber) {
        return doctorService.findDoctorsBySpecialityAndFullNameWithPageJson(fullName, speciality, pageNumber);
    }

}
