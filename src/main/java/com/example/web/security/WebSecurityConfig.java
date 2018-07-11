package com.example.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public static PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Autowired
    public AdminUserDetailsService adminUserDetailsService;

    @Autowired
    public CustomUserDetailsService customUserDetailsService;

    @Configuration
    @Order(1)
    @ConditionalOnProperty(prefix = "server.ssl.", name = "enabled", havingValue = "true")
    public class SslAdminSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable();
            http.httpBasic().disable();
            http.authorizeRequests()
                    .antMatchers("/admin").hasRole("ADMIN")
                    .antMatchers("/user").hasRole("USER")
                    .and()
                    .x509().subjectPrincipalRegex("(.*)").userDetailsService(adminUserDetailsService)
                    .and()
                    .anonymous().disable();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(adminUserDetailsService);
        }
    }

    @Configuration
    @Order(1)
    @ConditionalOnProperty(prefix = "server.ssl.", name = "enabled", havingValue = "false")
    public class AdminSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/admin*").authorizeRequests().anyRequest().hasRole("ADMIN")
                    .and()
                    .formLogin()
                    .loginPage("/admin-login")
                    .loginProcessingUrl("/admin-login")
                    .failureUrl("/admin-login?error=loginError")
                    .defaultSuccessUrl("/admin")
                    .permitAll()
                    .and()
                    .logout().logoutUrl("/admin-logout").logoutSuccessUrl("/").deleteCookies("JSESSIONID").and().exceptionHandling().accessDeniedPage("/403").and().csrf().disable();
            ;
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.eraseCredentials(false);
            auth.userDetailsService(adminUserDetailsService);
        }
    }

    @Configuration
    @Order(2)
    @ConditionalOnProperty(prefix = "server.ssl.", name = "enabled", havingValue = "false")
    public class UserSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().antMatchers("/user").permitAll().and()
                    .formLogin()
                    .loginPage("/user-login")
                    .loginProcessingUrl("/user-login")
                    .failureUrl("/user-login?error=loginError")
                    .defaultSuccessUrl("/user")
                    .permitAll()
                    .and()
                    .logout().logoutUrl("/user-logout").logoutSuccessUrl("/").deleteCookies("JSESSIONID").and().exceptionHandling().accessDeniedPage("/403").and().csrf().disable();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(customUserDetailsService);

        }
    }


    @Configuration
    @Order(3)
    public class DefaultSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable();

            http
                    .authorizeRequests()
                    .antMatchers("/", "/page").permitAll();
        }
    }
}
