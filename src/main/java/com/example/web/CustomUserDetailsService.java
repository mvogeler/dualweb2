package com.example.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    public PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(username);
        if (username.equals("admin")) {
            return new User("admin", encoder.encode("password"), Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        } else if (username.equals("user")) {
            return new User("user", encoder.encode("password"), Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
        }
        return new User("test", encoder.encode("password"), Arrays.asList(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
    }
}
