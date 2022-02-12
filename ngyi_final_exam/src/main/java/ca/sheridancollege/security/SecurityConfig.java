package ca.sheridancollege.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/*
 * Name: Yin Tung Ng
 * Student #: 991602581
 * Assignment: Final Exam
 * Course: PROG32758 - Java 3
 */

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private LoginAccessDeniedHandler accessdeniedhandler;

	@Autowired
	UserDetailedServiceImple userDetailsService;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
    	http.csrf().disable();	//remove in deployment, for H2 database
		http.headers().frameOptions().disable();
		http.authorizeRequests()
			//Define URL's and who has access
			.antMatchers("/admin/**",
						 "/h2-console/**").hasRole("ADMIN")	// ADMIN ONLY
			.antMatchers("/scoreboard").hasRole("USER") // scoreboard for USERS only
			.antMatchers("/",
						"/images/**",
						"/css/**",
						"/js/**",
						"/**").permitAll()
			// have to specify POST method for the after-register page
			.antMatchers(HttpMethod.POST, "/register").permitAll()
			.anyRequest().authenticated()
		.and()
		.csrf().ignoringAntMatchers("/h2-console/**")
		.and()
		.formLogin()
			.loginPage("/login")
			.permitAll()
		.and()
		.logout()
			.invalidateHttpSession(true)
			.clearAuthentication(true)
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			.logoutSuccessUrl("/login?logout")
			.permitAll()
		.and()
		.exceptionHandling()
		.accessDeniedHandler(accessdeniedhandler);
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}
}
