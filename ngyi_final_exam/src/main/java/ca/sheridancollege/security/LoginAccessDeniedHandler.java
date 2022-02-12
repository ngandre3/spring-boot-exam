package ca.sheridancollege.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/*
 * Name: Yin Tung Ng
 * Student #: 991602581
 * Assignment: Final Exam
 * Course: PROG32758 - Java 3
 */

/**
 * Used for Access Denied handling.
 * Must implement AccessDeniedHandler interface.
 * Must have have the @Component tag to AUTOWIRE with SecurityConfig.java
 */
@Component
public class LoginAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException)
			throws IOException, ServletException {

		// gets information about the logged-in user
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null) {
			System.out.println(auth.getName()
					+ " was trying to access protected resource: "
					+ request.getRequestURI());
		}
		
		// sends the denied user to this "access-denied" URL
		response.sendRedirect(request.getContextPath() + "/access-denied");
	}

}
