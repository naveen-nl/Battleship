
package com.api.backend.utils;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * SecurityConfiguration is a configuration class used to set up Spring Security
 * for the application.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	/**
	 * Configures the global AuthenticationManagerBuilder with the custom
	 * UserDetailsService and the password encoder for authentication.
	 *
	 * @param auth the AuthenticationManagerBuilder to configure
	 * @throws Exception if an error occurs while configuring the
	 *                   AuthenticationManagerBuilder
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	/**
	 * Creates a BCryptPasswordEncoder for password encoding.
	 *
	 * @return the BCryptPasswordEncoder object
	 */
	private PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Configures the HTTP security for the application.
	 *
	 * @param http the HttpSecurity to configure
	 * @throws Exception if an error occurs while configuring the HttpSecurity
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().authorizeRequests().antMatchers("/game/**").authenticated()
				.antMatchers(Constants.AUTH_WHITE_LIST).permitAll().and().httpBasic().and().csrf().disable().headers()
				.frameOptions().sameOrigin();
	}

	/**
	 * Configures web security to ignore certain URLs.
	 *
	 * @param web the WebSecurity to configure
	 * @throws Exception if an error occurs while configuring the WebSecurity
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(Constants.WEB_WHITE_LIST);
	}

	/**
	 * Creates a CorsFilter to handle Cross-Origin Resource Sharing (CORS)
	 * configuration.
	 *
	 * @return the CorsFilter object
	 */
	@Bean
	public CorsFilter corsfilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		// TODO: add in property file
		config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
		config.setAllowedMethods(Arrays.asList(Constants.HTTP_METHODS));
		config.setAllowedHeaders(Arrays.asList(Constants.HTTP_HEADERS));
		config.setAllowCredentials(true);
		config.setMaxAge(3600L);
		source.registerCorsConfiguration(Constants.CORS_PATTERN, config);
		return new CorsFilter(source);
	}

}
