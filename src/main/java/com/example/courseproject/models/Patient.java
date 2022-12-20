package com.example.courseproject.models;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Date;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Patient implements User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Expose
    private Long id;

    // Имя
    @Column(name="name", nullable=false)
    @Size(min=2, max=30, message = "Имя должно быть от 2-х до 30 букв")
    @NotBlank(message = "Это поле является обязательным")
    @Expose
    private String name;

    // Фамилия
    @Column(name="surname", nullable=false)
    @Size(min=2, max=30, message = "Фамилия должна быть от 2-х до 30 букв")
    @NotBlank(message = "Это поле является обязательным")
    @Expose
    private String surname;

    // Дата рождения
    @Column(name="birth_date", nullable=false)
    @NotNull(message = "Это поле является обязательным")
    @Expose
    private Date birthDate;

    // Пол
    @Column(name="sex", nullable=false)
    @Min(value=1, message="Недопустимое значение")
    @NotNull(message = "Это поле является обязательным")
    @Expose
    private int sex;

    // Номер телефона
    @Column(name="telephone", nullable=false)
    @Size(min=17, max=18, message = "Недопустимое значение")
    @NotBlank(message = "Это поле является обязательным")
    @Expose
    private String telephone;

    // Электронная почта
    @Column(name="email", nullable=false)
    @Email(message = "Недопустимое значение")
    @NotBlank(message = "Это поле является обязательным")
    @Expose
    private String email;

    // Пароль
    @Column(name="password", nullable=false)
    @NotBlank(message = "Это поле является обязательным")
    private String password;

    // Ссылка на изображение
    @Column(name="image")
    private String image;

    // Приёмы относящиеся к пациенту
    @OneToMany(
            mappedBy = "patient",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<Appointment> appointments = new HashSet<>();

    // Роли пациента в системе
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "patient_role", joinColumns = @JoinColumn(name = "patient_id")) // описывает, что данное поле будет храниться в отдельной таблице, для кторой мы не описывали mapping
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    public String getProfileLink() {
        return "patients/" + getId();
    }

    // Методы унаследованные от UserDetails предназначенные для авторизации
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
