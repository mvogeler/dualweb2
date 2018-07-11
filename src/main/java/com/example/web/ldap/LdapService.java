package com.example.web.ldap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.naming.InvalidNameException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapName;
import java.util.ArrayList;
import java.util.List;

@Service
public class LdapService {
    @Value("${ldap.url}")
    private String ldapUrl;

    @Value("${ldap.base.dn}")
    private String ldapBaseDn;

    @Value("${ldap.username}")
    private String ldapSecurityPrincipal;

    @Value("${ldap.password}")
    private String ldapPrincipalPassword;

    @Value("${ldap.user.dn.pattern}")
    private String ldapDnPattern;

    @Value("${server.ssl.enabled}")
    private boolean isUsingCerts;


    public Person getUser(String username) {
        LdapQuery query = LdapQueryBuilder.query().base(ldapBaseDn)
                .where("objectclass").is("person").and("uid").is(getName(username));

        List<Person> personList = getLdapTemplate().search(query, new PersonAttributesMapper());

        if (personList.size() >= 1) {
            return personList.get(0);
        } else {
            System.out.println(username + " not in LDAP");
        }

        Person person = new Person();
        person.setName(username);
        return person;

    }

    private String getName(String id) {
        String name = id;

        if (isUsingCerts) {
            System.out.println("using certs");
            try {
                name = LdapUtils.getStringValue(new LdapName(id), "cn");

                //TODO check rest of DN?  Like O, C, OU?
                System.out.println("LDAP-" + name);

            } catch (InvalidNameException e) {
                e.printStackTrace();
            }
        }

        return name;
    }

    private List<GrantedAuthority> getRoles(String username) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (username != null) {
            LdapQuery groupQuery = LdapQueryBuilder.query().base(ldapBaseDn)
                    .where("objectclass").is("groupOfUniqueNames");

            List<Group> groupList = getLdapTemplate().search(groupQuery, new GroupAttributesMapper());

            System.out.println(username);
            for (Group group : groupList) {
                if (group.getUniqueMembers().contains("uid=" + username + "," + ldapBaseDn)) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + group.getName().toUpperCase()));
                    System.out.println(group.getName());
                }
            }
        }

        if (!authorities.contains(new SimpleGrantedAuthority("ROLE_USER"))) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            System.out.println("USER");
        }

        return authorities;
    }

    private class GroupAttributesMapper implements AttributesMapper {

        @Override
        public Object mapFromAttributes(Attributes attributes) throws NamingException {
            Group group = new Group();
            group.setName((String) attributes.get("cn").get());

            NamingEnumeration attr = attributes.get("uniqueMember").getAll();
            while (attr.hasMore()) {
                String member = (String) attr.next();
                group.addUniqueMember(member);
            }

            return group;
        }
    }

    private class PersonAttributesMapper implements AttributesMapper {

        @Override
        public Object mapFromAttributes(Attributes attributes) throws NamingException {
            Person person = new Person();
            if (attributes.get("uid") != null) {
                person.setName((String) attributes.get("uid").get());
            }

            byte[] password = "".getBytes();
            if (attributes.get("userPassword") != null) {
                password = (byte[]) attributes.get("userPassword").get();
            }
            person.setPassword(new String(password));

            person.setRoles(getRoles(person.getName()));
            return person;
        }
    }

    private LdapTemplate getLdapTemplate() {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(ldapUrl);
        contextSource.setUserDn(ldapSecurityPrincipal);
        contextSource.setPassword(ldapPrincipalPassword);
        try {
            contextSource.afterPropertiesSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LdapTemplate ldapTemplate = new LdapTemplate();
        ldapTemplate.setContextSource(contextSource);
        return ldapTemplate;
    }
}
