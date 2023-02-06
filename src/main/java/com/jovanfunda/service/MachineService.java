package com.jovanfunda.service;

import com.jovanfunda.model.database.Machine;
import com.jovanfunda.model.database.User;
import com.jovanfunda.model.enums.Status;
import com.jovanfunda.repository.MachineRepository;
import com.jovanfunda.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MachineService {

    @Autowired
    MachineRepository machineRepository;

    @Autowired
    UserRepository userRepository;

    public MachineService(MachineRepository machineRepository, UserRepository userRepository) {
        this.machineRepository = machineRepository;
        this.userRepository = userRepository;
    }

    public List<Machine> getMachines(String userEmail) {
        User user = userRepository.findById(userEmail).get();
        List<Machine> machines = new ArrayList<>();
        machineRepository.findAll().forEach(machine -> {
            if(machine.getCreatedBy().equals(user)) {
                machines.add(machine);
            }
        });
        return machines;
    }

    public void createMachine(String userEmail, String machineName) {
        Machine newMachine = new Machine();
        newMachine.setName(machineName);
        newMachine.setStatus(Status.STOPPED);
        newMachine.setCreatedBy(userRepository.findById(userEmail).get());
        newMachine.setActive(true);
        machineRepository.save(newMachine);
    }



}
