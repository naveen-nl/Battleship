package com.api.backend.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * CustomUserDetailService is an implementation of Spring Security's UserDetailsService
 * used for loading user details during authentication.
 */
@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private Environment env;

    @Value("${custom.user.username}")
    private String username;

    @Value("${custom.user.password}")
    private String encodedPassword;

    /**
     * Loads user details for authentication.
     *
     * @param username the username to load user details for
     * @return the UserDetails object containing user details
     * @throws UsernameNotFoundException if the specified username is not found
     */
    //TODO: extend the class to handle user credentials and generate JWT token
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (this.username.equals(username)) {
            return User.builder().username(username).password(encodedPassword).roles(Constants.USER_ROLE).build();
        } else {
            throw new UsernameNotFoundException(env.getProperty("CustomUserDetailService.USER_NOT_FOUND"));
        }
    }
}
