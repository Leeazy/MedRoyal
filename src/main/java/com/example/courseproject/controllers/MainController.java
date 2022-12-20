package com.example.courseproject.controllers;

import com.example.courseproject.services.DoctorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@AllArgsConstructor
@RequestMapping("/medroyal")
public class MainController {

    private final DoctorService doctorService;

    @GetMapping()
    public String homePage() {
        return "main/home";
    }

    @GetMapping("/doctors")
    public ModelAndView doctorsPage(ModelAndView modelAndView) {
        return doctorService.findMainDoctorsFirstPageView(modelAndView);
    }

    @GetMapping("/services")
    public String servicesPage() {
        return "mistakes/notFound";
    }

    @GetMapping("/contacts")
    public String contactsPage() {
        return "mistakes/notFound";
    }

}
