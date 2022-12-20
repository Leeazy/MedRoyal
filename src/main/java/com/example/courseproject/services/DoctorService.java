package com.example.courseproject.services;

import com.example.courseproject.models.Doctor;
import com.example.courseproject.models.Patient;
import com.example.courseproject.repo.DoctorRepository;
import com.example.courseproject.util.FileUploadUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    private final PasswordEncoder passwordEncoder;

    private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    private static final int pageSize = 4;

    @Value("${upload.path}")
    private String uploadPath;

    // Найти доктора по id
    private Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Доктор с id " + id + " не был найден!"));
    }

    // Находит страницу главных докторов
    public ModelAndView findMainDoctorsFirstPageView(ModelAndView modelAndView) {
        Page<Doctor> page = findPageOfAllDoctors(0);
        Map<Integer, List<Doctor>> doctorMap = new HashMap<>();
        List<Doctor> doctors = new ArrayList<>();
        doctorMap.put(1, page.stream().limit(4).collect(Collectors.toList()));
        if (page.getNumberOfElements() > 4) {
            doctorMap.put(2, page.getContent().subList(4, page.getNumberOfElements()));
        }
        modelAndView.addObject("doctors", doctorMap);
        modelAndView.addObject("totalPages", page.getTotalPages());
        modelAndView.setViewName("main/doctors");
        return modelAndView;
    }

    // Находит нужную страницу cвязанную с доктором по id
    public ModelAndView findDoctorByIdView(Long id, ModelAndView modelAndView, String viewName) {
        Doctor doctorById = getDoctorById(id);
        modelAndView.addObject("doctor", doctorById);
        modelAndView.setViewName(viewName);
        return modelAndView;
    }

    // Добавляет доктора в базу
    public ModelAndView saveNewDoctor(Doctor doctor, BindingResult bindingResult, ModelAndView modelAndView) {
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("doctors/newDoctor");
            return modelAndView;
        }
        if (doctor.getPassword() == null || doctor.getPassword().isEmpty()) {
            String doctorPassword = "password";
            doctor.setPassword(passwordEncoder.encode(doctorPassword));
        }
        doctorRepository.save(doctor);
        modelAndView.setViewName("redirect:/medroyal");
        return modelAndView;
    }

    // Удаляет доктора по id
    public void deleteDoctorById(Long id) {
        try {
            Doctor doctor = getDoctorById(id);
            String doctorDir = uploadPath + "doctors/" + doctor.getId();
            FileUploadUtil.deleteDirectory(doctorDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        doctorRepository.deleteById(id);
    }

    // Находит доктора по id (возвращает json)
    public String findDoctorByIdJson(Patient patient, Long id) {
        boolean reg = (patient != null);
        JsonObject jsonObject = new JsonObject();
        JsonElement jsonElement = gson.toJsonTree(getDoctorById(id));
        jsonElement.getAsJsonObject().addProperty("reg", reg);
        jsonObject.add("doctor", jsonElement);
        return jsonObject.toString();
    }

    // Находит докторов по странице (возвращает page)
    public Page<Doctor> findPageOfAllDoctors(Integer pageNumber) {
        return doctorRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("id")));
    }

    // Находит докторов по поиску и странице (возвращает page)
    public Page<Doctor> findPageOfDoctorsBySearch(String search, Integer pageNumber) {
        String[] fullNameParts = search.split(" ");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id"));
        switch (fullNameParts.length) {
            case 0:
                return null;
            case 1:
                return doctorRepository.findDoctorByOneString(fullNameParts[0], pageable);
            case 2:
                return doctorRepository.findDoctorByTwoStrings(fullNameParts[0], fullNameParts[1], pageable);
            default:
                return doctorRepository.findDoctorByThreeStrings(fullNameParts[0], fullNameParts[1], fullNameParts[2], pageable);
        }
    }

    // Находит докторов по странице (возвращает json)
    public String findDoctorsWithPageJson(Integer pageNumber) {
        Page<Doctor> page = doctorRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("id")));
        return gson.toJson(page.getContent());
    }

    // Находит докторов по поисковому запросу (возвращает json)
    public String findDoctorsBySearchWithPageJson(String search, Integer pageNumber) {
        JsonObject jsonObject = new JsonObject();
        Page<Doctor> page = findPageOfDoctorsBySearch(search, pageNumber);
        jsonObject.addProperty("entities", gson.toJson(page.getContent()));
        jsonObject.addProperty("totalPages", page.getTotalPages());
        return jsonObject.toString();
    }

    // Находит докторов по специальности (возвращает json)
    public String findDoctorsBySpecialityJson(String speciality) {
        return gson.toJson(doctorRepository.findDoctorsBySpeciality(speciality));
    }

    // Находит докторов по специальности и поисковому запросу (возвращает json)
    public String findDoctorsBySpecialityAndFullNameWithPageJson(String fullName, String speciality, Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id"));
        JsonObject jsonObject = new JsonObject();
        Page<Doctor> page;
        if (fullName.isBlank() && speciality.isBlank()) {
            page = findPageOfAllDoctors(pageNumber);
        }
        else {
            if (!fullName.isBlank() && !speciality.isBlank()) {
                String[] fullNameParts = fullName.split(" ");
                switch (fullNameParts.length) {
                    case 1:
                        page = doctorRepository.findDoctorBySpecialityAndOneString(speciality, fullNameParts[0], pageable);
                        break;
                    case 2:
                        page = doctorRepository.findDoctorBySpecialityAndTwoStrings(speciality, fullNameParts[0], fullNameParts[1], pageable);
                        break;
                    default:
                        page = doctorRepository.findDoctorBySpecialityAndThreeStrings(speciality, fullNameParts[0], fullNameParts[1], fullNameParts[2], pageable);
                        break;
                }
            }
            else {
                if (!speciality.isBlank()) {
                    page = doctorRepository.findDoctorsBySpeciality(speciality, pageable);
                }
                else {
                    page = findPageOfDoctorsBySearch(fullName, pageNumber);
                }
            }
        }
        jsonObject.addProperty("doctors", gson.toJson(Objects.requireNonNull(page).getContent()));
        jsonObject.addProperty("totalPages", page.getTotalPages());
        return jsonObject.toString();
    }



}
