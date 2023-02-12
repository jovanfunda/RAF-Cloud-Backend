package com.jovanfunda.repository;

import com.jovanfunda.model.database.ErrorHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorRepository extends JpaRepository<ErrorHistory, Long> {
}
