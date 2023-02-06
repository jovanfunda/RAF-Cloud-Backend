package com.jovanfunda.repository;

import com.jovanfunda.model.database.Machine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineRepository extends JpaRepository<Machine, String> {
}
