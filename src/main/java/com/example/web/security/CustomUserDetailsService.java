package com.example.web.security;

import com.example.web.ldap.LdapService;
import com.example.web.ldap.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private LdapService ldapService;

    @Autowired
    public PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person person = ldapService.getUser(username);

        //TODO commented works fine if an admin hits the admin side first then wants to hit the user side, but if not...
        //TODO uncommented works fine, but a user could hit admin stuff without needing admin password...
//        String ldapName = username;
//        if (person != null) {
//            ldapName = person.getName();
//        }

        return new User(person.getName(), encoder.encode(""), person.getRoles());

//        return new User(ldapName, encoder.encode(""), Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
