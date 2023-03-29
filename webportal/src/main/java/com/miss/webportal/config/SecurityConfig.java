package com.miss.webportal.config;

/* 1. Please implement this class
 * refer here https://github.com/t217145/COMPS368-Code/tree/main/u7/jdbcauth.web
*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    /*
    * 2. For the securityFilterChain
    *  You should block all event and venue create, edit and delete function and only manager can acceess
    *  You should block all ticket edit and delete function and only manager can acceess
    *  You should block all ticket claim function and only staff can acceess
    *  You should block all ticket create function and anyone with login can accees
    *  All other page should open to public
    */
    private static final String USERQUERY = "select usrcode, password, isactive from myusers where usrcode = ? and isactive = 1";
    private static final String ROLEQUERY = "select usrcode, roles from myroles where usrcode = ?";

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, DataSource dataSource, PasswordEncoder passwordEncoder) throws Exception {
        auth.jdbcAuthentication()
                .passwordEncoder(passwordEncoder)
                .dataSource(dataSource)
                .usersByUsernameQuery(USERQUERY)
                .authoritiesByUsernameQuery(ROLEQUERY);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .logout(withDefaults())
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/login", "/logout", "/resources/**", "/h2-console", "/h2-console/**", "/events/index", "/venues/index").permitAll()
                        .requestMatchers("/events/**", "/venues/**", "/tickets/**").hasAuthority("ROLE_MANAGER")
                        .requestMatchers("/tickets/claim").hasAnyAuthority("ROLE_MANAGER", "ROLE_STAFF")
                        .requestMatchers("/tickets/create").authenticated()
                        .anyRequest().permitAll()
                )
//                .logout(Customizer.withDefaults())
//                .authorizeHttpRequests((authorize) -> authorize
//                        .requestMatchers("/login", "/logout", "/resources/**", "/h2-console", "/h2-console/**","/events", "/venues").permitAll()
//                        .requestMatchers(  "/events", "/venues", "tickets", "tickets/edit").hasAnyAuthority("ROLE_MANAGER")
//                        .requestMatchers( "tickets/claim").hasAnyAuthority("ROLE_MANAGER", "ROLE_STAFF")
//                        .requestMatchers( "tickets/create").hasAnyAuthority("ROLE_MANAGER", "ROLE_STAFF","ROLE_CUSTOMER")
//                        .anyRequest().permitAll()
//                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .permitAll()
                )


                .sessionManagement((sessions) -> sessions
                        .sessionConcurrency((concurrency) -> concurrency
                                .maximumSessions(1)
                                .expiredUrl("/login?expired")
                        )
                )
                .csrf().ignoringRequestMatchers("/login", "/logout", "/resources/**", "/h2-console", "/h2-console/**");
        return http.build();
    }
}

