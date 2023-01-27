package com.jovanfunda.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jovanfunda.model.UpdateUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jovanfunda.model.User;
import com.jovanfunda.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public List<User> getUsers() {
		return userRepository.findAll();
	}

	public boolean registerNewUser(User user) {
		if(!userExists(user.getEmail())) {
			userRepository.save(user);
			return true;
		}
		return false;
	}

	public boolean userExists(String email) {
		return userRepository.findById(email).isPresent();
	}


	public Boolean updateUser(UpdateUserDto updateUserDto) {
		if(userRepository.findById(updateUserDto.getRealEmail()).isPresent()) {
			User realUser = userRepository.findById(updateUserDto.getRealEmail()).get();
			System.out.println(updateUserDto.getUser().getName());
			realUser.setName(updateUserDto.getUser().getName());
			System.out.println(updateUserDto.getUser().getLastname());
			realUser.setLastname(updateUserDto.getUser().getLastname());
			System.out.println(updateUserDto.getUser().getPermissions());
			realUser.setPermissions(updateUserDto.getUser().getPermissions());
			System.out.println(updateUserDto.getPassword());
			if(!updateUserDto.getPassword().equals(""))
				realUser.setPassword(updateUserDto.getPassword());
			System.out.println(realUser);
			userRepository.save(realUser);
			return true;
		}
		return false;
	}

	public void deleteUser(String userEmail) {
		userRepository.deleteById(userEmail);
	}

	public Optional<User> findById(String email) {
		return userRepository.findById(email);
	}
}
