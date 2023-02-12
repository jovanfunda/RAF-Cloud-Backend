package com.jovanfunda.repository;

import com.jovanfunda.model.database.Machine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {

    @Transactional
    @Modifying
    @Query("update Machine m set m.status='STOPPED' where m.id=:machineID")
    public void firstRestartStep(@Param("machineID") Long machineID);

}
