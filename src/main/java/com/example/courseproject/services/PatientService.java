package com.example.courseproject.services;

import com.example.courseproject.mappers.PatientMapper;
import com.example.courseproject.models.Patient;
import com.example.courseproject.models.Role;
import com.example.courseproject.repo.PatientRepository;
import com.example.courseproject.util.FileUploadUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    private final static int pageSize = 4;

    @Value("${upload.path}")
    private String uploadPath;

    // Найти пациента по id
    private Patient getPatientById(Long id) {
        return patientRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Пациент с id " + id + " не был найден!"));
    }

    // Найти нужную страницу с пациентом по id
    public ModelAndView findPatientById(Long id, ModelAndView modelAndView, String viewName) {
        modelAndView.addObject("patient", getPatientById(id));
        modelAndView.setViewName(viewName);
        return modelAndView;
    }

    // Сохранить пациента в базу
    public ModelAndView savePatient(Patient patient, BindingResult bindingResult, ModelAndView modelAndView) {
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("main/registration");
        }
        else {
            if (patientRepository.findByEmail(patient.getEmail()) != null) {
                modelAndView.addObject("emailMessage", "Пользователь с таким email уже существует!");
                modelAndView.setViewName("main/registration");
            }
            else {
                patient.setPassword(passwordEncoder.encode(patient.getPassword()));
                patient.setRoles(Collections.singleton(Role.USER));
                patientRepository.save(patient);
                modelAndView.addObject("email", patient.getEmail());
                modelAndView.setViewName("main/login");
            }
        }
        return modelAndView;
    }

    // Обновить пациента
    public ModelAndView updatePatient(Patient updatedPatient, BindingResult bindingResult, MultipartFile multipartFile, ModelAndView modelAndView) {
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("/patients/editById");
        }
        else {
            if (!multipartFile.isEmpty()) savePatientFile(updatedPatient, multipartFile);
            Patient patient = getPatientById(updatedPatient.getId());
            PatientMapper.INSTANCE.updatePatientFromUpdatedEntity(updatedPatient, patient);
            patientRepository.save(patient);
            modelAndView.setViewName("redirect:/patients/" + updatedPatient.getId());
        }
        return modelAndView;
    }

    // Удалить пациента
    public void deletePatientById(Long id) {
        try {
            Patient patient = getPatientById(id);
            String patientDir = uploadPath + "patients/" + patient.getId();
            FileUploadUtil.deleteDirectory(patientDir);
        } catch (Exception e) {
            System.out.println("Ошибка в удалении файла пациента!");
        }
        patientRepository.deleteById(id);
    }

    // Сохранить файл пациента
    public void savePatientFile(Patient patient, MultipartFile multipartFile) {
        try {
            String uploadDir = uploadPath + "/patients/" + patient.getId();
            String fileName = UUID.randomUUID() + "." + Objects.requireNonNull(multipartFile.getContentType()).substring(6);
            patient.setImage(fileName);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } catch (Exception e) {
            System.out.println("Ошибка в сохранении файла!");
        }
    }

}
