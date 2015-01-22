package com.mlesniak.news;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.web.context.support.StandardServletEnvironment;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Separate authentication controller, since it has to be used before the SecurityConfiguration.
 *
 * @author Michael Lesniak (mail@mlesniak.com)
 */
@Configuration
public class AuthenticationConfiguration extends GlobalAuthenticationConfigurerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationConfiguration.class);

    @Autowired
    private Environment env;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        Set<String> usernames = getUserNames();

        for (String username : usernames) {
            String password = env.getProperty("user." + username + ".password");
            String role = env.getProperty("user." + username + ".role");

            if (password == null || role == null || username == null) {
                LOG.error("Incorrect configuration for user. username={}", username);
                continue;
            }

            auth.inMemoryAuthentication()
                    .withUser(username)
                    .password(password)
                    .roles(role)
            ;
        }
    }


    private Set<String> getUserNames() {
        List<PropertiesPropertySource> props = new LinkedList<>();
        if (env instanceof StandardServletEnvironment) {
            StandardServletEnvironment servletEnvironment = (StandardServletEnvironment) env;
            for (PropertySource<?> propertySource : servletEnvironment.getPropertySources()) {
                if (propertySource instanceof PropertiesPropertySource) {
                    props.add((PropertiesPropertySource) propertySource);
                }
            }
        }

        // TODO Complete Java 8 filter?
        Set<String> usernames = new HashSet<>();
        for (PropertiesPropertySource prop : props) {
            List<String> ids = Arrays.asList(prop.getPropertyNames()).stream().filter(new Predicate<String>() {
                @Override
                public boolean test(String s) {
                    return s.startsWith("user.");
                }
            })
                    .map(s -> s.split("\\.")[1])
                    .collect(Collectors.toList());
            usernames.addAll(ids);
        }
        return usernames;
    }
}
