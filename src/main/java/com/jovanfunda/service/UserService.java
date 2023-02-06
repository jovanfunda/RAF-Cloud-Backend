package com.jovanfunda.service;

import java.util.List;
import java.util.Optional;

import com.jovanfunda.model.requests.UpdateUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jovanfunda.model.database.User;
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
			realUser.setName(updateUserDto.getUser().getName());
			realUser.setLastname(updateUserDto.getUser().getLastname());
			realUser.setPermissions(updateUserDto.getUser().getPermissions());
			if(!updateUserDto.getPassword().equals(""))
				realUser.setPassword(updateUserDto.getPassword());
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
