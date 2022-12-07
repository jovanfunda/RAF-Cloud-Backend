package com.jovanfunda.service;

import java.util.List;
import java.util.Optional;

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

	public String checkPassword(String email, String password) {
		Optional<User> user_temp = userRepository.findById(email);
		if(user_temp.isPresent()) {
			if(user_temp.get().checkPassword(password)) {
				return user_temp.get().getJWToken();
			}
		}
		return "";
	}
}
