package com.example.courseproject.models;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Expose
    private Long id;

    // Имя
    @Column(name="name", nullable=false)
    @Expose
    private String name;

    // Фамилия
    @Column(name="surname", nullable=false)
    @Expose
    private String surname;

    // Отчество
    @Column(name="patronymic", nullable=false)
    @Expose
    private String patronymic;

    // Электронная почта
    @Column(name="email", nullable=false)
    private String email;

    // Кабинет
    @Column(name="cabinet")
    @Expose
    private String cabinet;

    // Опыт работы
    @Column(name="experience", nullable=false)
    @Expose
    private int experience;

    // Категория
    @Column(name="category", nullable=false)
    @Expose
    private String category;

    // Текст "О докторе"
    @Column(name="about_doctor", nullable=false, columnDefinition="TEXT")
    private String aboutDoctor;

    // Текст "Образование"
    @Column(name="education", nullable=false, columnDefinition="TEXT")
    private String education;

    // Текст "Места работы"
    @Column(name="work_places", columnDefinition="TEXT")
    private String workPlaces;

    // Ссылка на файл фото
    @Column(name="image")
    @Expose
    private String image;

    // Пароль
    @Column(name="password",nullable=false)
    private String password;

    // Специальности доктора
    @ElementCollection(targetClass = Speciality.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "doctor_speciality", joinColumns = @JoinColumn(name = "doctor_id"))
    @Enumerated(EnumType.STRING)
    @Expose
    private Set<Speciality> specialities;

    // Получить опыт с постфиксом год / года / лет
    static final String[] declension = {"год", "года", "лет"};
    static final int[] cases = {2, 0, 1, 1, 1, 2};
    public String getExperienceWithPrefix() {
        int experience = getExperience();
        return experience + " " + declension[ (experience%100>4 &&experience%100<20)? 2 : cases[Math.min(experience % 10, 5)] ];
    }

    // Получить список специальностей
    public List<String> getSpecialitiesLabels() {
        return specialities.stream().map(Speciality::toString).collect(Collectors.toList());
    }

}
