package com.example.web.security;

import com.example.web.ldap.LdapService;
import com.example.web.ldap.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminUserDetailsService implements UserDetailsService {

    @Autowired
    private LdapService ldapService;

    @Autowired
    public PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person person = ldapService.getUser(username);

        User result = new User(
                    person.getName(), encoder.encode(person.getPassword()), person.getRoles());

        return result;
    }
}
