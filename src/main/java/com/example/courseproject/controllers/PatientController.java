package com.example.courseproject.controllers;

import com.example.courseproject.models.Patient;
import com.example.courseproject.services.PatientService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
@AllArgsConstructor
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/new")
    public String getAddNewPatientView(@ModelAttribute("patient") Patient patient) {
        return "main/registration";
    }

    @PostMapping("/new")
    public ModelAndView addNewPatient(@ModelAttribute("patient") @Valid Patient patient, BindingResult bindingResult,
                                      ModelAndView modelAndView) {
        return patientService.savePatient(patient, bindingResult, modelAndView);
    }

    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id")
    public ModelAndView getPatientByIdView(@PathVariable("id") Long id, ModelAndView modelAndView) {
        return patientService.findPatientById(id, modelAndView, "patients/getById");
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("#id == authentication.principal.id")
    public ModelAndView getEditPatientByIdView(@PathVariable("id") Long id, ModelAndView modelAndView) {
        return patientService.findPatientById(id, modelAndView, "patients/editById");
    }

    @PutMapping("/{id}")
    @PreAuthorize("#patient.id == authentication.principal.id")
    public ModelAndView editPatientById(@ModelAttribute("patient") @Valid Patient patient, BindingResult bindingResult,
                                        @RequestParam("profileImage") MultipartFile multipartFile, ModelAndView modelAndView) {
        return patientService.updatePatient(patient, bindingResult, multipartFile, modelAndView);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority({'ADMIN'})")
    @ResponseBody
    public String deletePatientById(@PathVariable("id") Long id) {
        patientService.deletePatientById(id);
        return "Успешно!\n Аккаунт был удалён!";
    }

}
