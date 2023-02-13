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
    @Query("update Machine m set m.busy=true where m.id=:machineID and m.busy=false")
    public int setBusy(@Param("machineID") Long machineID);

    @Transactional
    @Query("update Machine m set m.status='STOPPED' where m.id=:machineID")
    @Modifying
    public void setStopped(@Param("machineID") Long machineID);

    @Transactional
    @Query("update Machine m set m.status='RUNNING' where m.id=:machineID")
    @Modifying
    public void setRunning(@Param("machineID") Long machineID);

    @Transactional
    @Modifying
    @Query("update Machine m set m.busy=false where m.id=:machineID")
    public void unbusy(@Param("machineID") Long machineID);
}
