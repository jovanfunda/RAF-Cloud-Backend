package com.jovanfunda.controller;

import java.util.List;

import com.jovanfunda.authentication.AuthService;
import com.jovanfunda.model.JWTokenPermissionChecker;
import com.jovanfunda.model.Permission;
import com.jovanfunda.model.UserLoginDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jovanfunda.service.UserService;
import com.jovanfunda.model.User;


@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private AuthService authService;

	public UserController(UserService userService, AuthService authService) {
		this.userService = userService;
		this.authService = authService;
	}
	
	@PostMapping("/users")
	public ResponseEntity<List<User>> getUsers(@RequestBody String jwtoken) {
		if(authService.hasPermission(jwtoken, Permission.CAN_READ_USERS)) {
			return ResponseEntity.ok().body(userService.getUsers());
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}
	
	@PostMapping("/registerUser")
	public String registerNewUser(@RequestBody User user) {
		String JWToken = authService.generateJWT(user);
		user.setJWToken(JWToken);
		if(!userService.registerNewUser(user)) {
			return null;
		}
		return JWToken;
	}

	@PostMapping("/hasPermission")
	public ResponseEntity<Boolean> hasPermission(@RequestBody JWTokenPermissionChecker jwtdto) {
		if(authService.hasPermission(jwtdto.getjwtoken(), jwtdto.getPermission())) {
			return ResponseEntity.ok().body(true);
		} else {
			return ResponseEntity.ok().body(false);
		}
	}

	// JWToken if user exists, empty String if it doesn't
	@PostMapping("/checkPassword")
	public String checkPassword(@RequestBody UserLoginDto user) {
		return userService.checkPassword(user.getEmail(), user.getPassword());
	}
}
