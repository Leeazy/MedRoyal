package com.example.courseproject.config;

import com.example.courseproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public WebSecurityConfig(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/medroyal", "/medroyal/**",
                            "/doctors/*/resume", "/doctors/by-expanded-search",
                            "/patients/new", "/appointments/new", "/appointments/*/success",
                            "/doctors/*/json", "/doctors/by-speciality", "/doctors/by-search").permitAll()
                    .antMatchers(HttpMethod.POST, "/patients/new", "/appointments/new").permitAll()
                    .antMatchers("/css/**", "/fonts/**", "/js/**", "/webjars/**",
                        "/applicationFiles/doctors/**", "/applicationFiles/designElements/**",
                        "/applicationFiles/usersFiles/**").permitAll()
                    .anyRequest().authenticated()
                .and()
                    .formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("/medroyal")
                    .permitAll()
                .and()
                    .logout()
                    .permitAll()
                .and()
                .csrf().disable().cors();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(passwordEncoder);
    }

}
