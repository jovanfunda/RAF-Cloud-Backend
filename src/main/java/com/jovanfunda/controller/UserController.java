package com.jovanfunda.controller;

import java.util.List;
import java.util.Optional;

import com.jovanfunda.authentication.AuthService;
import com.jovanfunda.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jovanfunda.service.UserService;


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
	public ResponseEntity<List<User>> getUsers(@RequestHeader String jwtoken) {
		if (authService.hasPermission(jwtoken, Permission.CAN_READ_USERS)) {
			return ResponseEntity.ok().body(userService.getUsers());
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}

	@PostMapping("/registerUser")
	public ResponseEntity<Boolean> registerNewUser(@RequestHeader String jwtoken, @RequestBody User user) {
		if (authService.hasPermission(jwtoken, Permission.CAN_CREATE_USERS)) {
			if (!userService.registerNewUser(user)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
			}
		}
		return ResponseEntity.ok().body(true);
	}

	@PostMapping("/hasPermission")
	public ResponseEntity<Boolean> hasPermission(@RequestBody JWTokenPermissionChecker jwtdto) {
		if (authService.hasPermission(jwtdto.getjwtoken(), jwtdto.getPermission())) {
			return ResponseEntity.ok().body(true);
		} else {
			return ResponseEntity.ok().body(false);
		}
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody UserLoginDto userDto) {
		Optional<User> optUser = userService.findById(userDto.getEmail());
		if (optUser.isPresent()) {
			User user = optUser.get();
			if(user.checkPassword(userDto.getPassword())) {
				return ResponseEntity.ok().body(authService.generateJWT(user));
			}
		}
		return ResponseEntity.status(404).build();
	}

	@PostMapping("/updateUser")
	public ResponseEntity<Boolean> updateUser(@RequestHeader String jwtoken, @RequestBody UpdateUserDto updateUser) {
		if (authService.hasPermission(jwtoken, Permission.CAN_UPDATE_USERS)) {
			return ResponseEntity.ok().body(userService.updateUser(updateUser));
		}
		return ResponseEntity.status(403).build();
	}

	@PostMapping("/deleteUser")
	public ResponseEntity<Void> deleteUser(@RequestHeader String jwtoken, @RequestBody String userEmail) {
		if (authService.hasPermission(jwtoken, Permission.CAN_DELETE_USERS)) {
			userService.deleteUser(userEmail);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.status(403).build();
	}
}
