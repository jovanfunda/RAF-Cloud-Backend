package com.jovanfunda.model;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class User {
	@Transient
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Id
	String email;
	String password;
	String JWToken;
	String name;
	String lastname;
	@ElementCollection
	List<Permission> permissions;

	public User() {
		bCryptPasswordEncoder = new BCryptPasswordEncoder();
	}
	
	public User(String email, String password) {
		this.email = email;
		this.password = password;
		permissions = new ArrayList<>();
	}

	public boolean checkPassword(String passwordAttempt) {
		return bCryptPasswordEncoder.matches(passwordAttempt, this.password);
	}
	
	public void setPassword(String newPassword) {
		this.password = bCryptPasswordEncoder.encode(newPassword);
	}

	public String getPassword() {
		return this.password;
	}

	public String getJWToken() {
		return JWToken;
	}

	public void setJWToken(String JWToken) {
		this.JWToken = JWToken;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

	public void addPermission(Permission permission) {
		this.permissions.add(permission);
	}

	public void removePermission(String permission) {
		this.permissions.remove(permission);
	}

	@Override
	public String toString() {
		return "User{" +
				"email='" + email + '\'' +
				", password='" + password + '\'' +
				", name='" + name + '\'' +
				", lastname='" + lastname + '\'' +
				", permissions=" + permissions +
				'}';
	}
}
