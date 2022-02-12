package ca.sheridancollege.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ca.sheridancollege.database.DatabaseAccess;

/*
 * Name: Yin Tung Ng
 * Student #: 991602581
 * Assignment: Final Exam
 * Course: PROG32758 - Java 3
 */

@Service
public class UserDetailedServiceImple implements UserDetailsService {

	@Autowired
	@Lazy
	DatabaseAccess da;

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		// Find the user based on the user name.
		// we use specific package naming for User CLASS bc we have 2 types of USER
		// objects: 1 is our bean, other is from Spring Security
		ca.sheridancollege.beans.User user = da.findUserAccount(username);
		// If the user doesn't exist, throw an exception
		if (user == null) {
			System.out.println("User not found:"
					+ username);
			throw new UsernameNotFoundException(
					"User "+ username + " was not found in the database");
		}

		// Get a list of roles
		List<String> roleNames = da.getRolesById(user.getUserId());
		// Change the list of roles into a list of GrantedAuthority
		List<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();
		if (roleNames != null) {
			for (String role : roleNames) {
				// convert every role to a SIMPLEGRANTEDAUTHORIY, then add to grantList
				grantList.add(new SimpleGrantedAuthority(role));
			}
		}

		// Create a user (type UserDetails), based on the information above.
		// CAREFUL: User class import must come from Spring Security
		UserDetails userDetails = (UserDetails) new User(user.getUserName(),
				user.getEncryptedPassword(), grantList);

		return userDetails;

	}

}
