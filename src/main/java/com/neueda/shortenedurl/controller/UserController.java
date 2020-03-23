package com.neueda.shortenedurl.controller;

import com.neueda.shortenedurl.model.AppUser;
import com.neueda.shortenedurl.services.user.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import java.util.Date;

@CrossOrigin(origins = "http://localhost", maxAge = 3600)
@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserService userService;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public AppUser registerUser(@RequestBody AppUser user) {
		return userService.save(user);
	}

	@RequestMapping(value = "/token", method = RequestMethod.POST)
	public String createToken(@RequestBody AppUser login) throws ServletException {

		String jwtToken = "";
		if (login.getEmail() == null || login.getPassword() == null) {
			throw new ServletException("Please fill in username and password");
		}
		String email = login.getEmail();
		String password = login.getPassword();
		AppUser user = userService.findByEmail(email);
		if (user == null) {
			throw new ServletException("User email not found.");
		}
		String pwd = user.getPassword();
		if (!password.equals(pwd)) {
			throw new ServletException("Invalid password. Please check your name and password.");
		}

		jwtToken = Jwts.builder().setSubject(email).claim("roles", "user").setIssuedAt(new Date())
				.signWith(SignatureAlgorithm.HS256, "secretkey").compact();

		return jwtToken;
	}
}
