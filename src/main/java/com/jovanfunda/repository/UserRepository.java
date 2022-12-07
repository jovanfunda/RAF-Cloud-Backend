package com.jovanfunda.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.jovanfunda.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

}
