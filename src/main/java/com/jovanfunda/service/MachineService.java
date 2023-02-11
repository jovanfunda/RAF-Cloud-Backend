package com.jovanfunda.service;

import com.jovanfunda.model.database.Machine;
import com.jovanfunda.model.database.User;
import com.jovanfunda.model.enums.Status;
import com.jovanfunda.model.requests.MachineFilterRequest;
import com.jovanfunda.repository.MachineRepository;
import com.jovanfunda.repository.UserRepository;
import org.hibernate.StaleStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
            if (machine.getCreatedBy().equals(user) && machine.getActive()) {
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
        newMachine.setDateCreated(new Date());
        machineRepository.save(newMachine);
    }

    public List<Machine> filterMachine(String userEmail, MachineFilterRequest machineFilter) {

        List<Machine> machines = machineRepository.findAll();

        machines = machines.stream().filter(m -> m.getCreatedBy() == userRepository.findById(userEmail).get() && m.getActive()).collect(Collectors.toList());

        if (machineFilter.getName() != null) {
            machines = machines.stream().filter(m -> m.getName().toLowerCase().contains(machineFilter.getName().toLowerCase())).collect(Collectors.toList());
        }
        if (machineFilter.getStatuses() != null) {
            machines = machines.stream().filter(m -> machineFilter.getStatuses().contains(m.getStatus())).collect(Collectors.toList());
        }
        if (machineFilter.getDateFrom() != null && machineFilter.getDateTo() != null) {
            machines = machines.stream().filter(m -> m.getDateCreated().after(machineFilter.getDateFrom()) && m.getDateCreated().before(machineFilter.getDateTo())).collect(Collectors.toList());
        }

        return machines;
    }

    @Transactional
    public boolean destroyMachine(Long machineID) {
        if (machineRepository.findById(machineID).isPresent()) {
            machineRepository.findById(machineID).get().setActive(false);
            return true;
        }
        return false;
    }

    @Transactional
    public void startMachine(Long machineID) {
        Machine machine = this.machineRepository.findById(machineID).get();

        new Thread(() -> {
            try {
                Thread.sleep(10000);
                machine.setStatus(Status.RUNNING);
                this.machineRepository.save(machine);
            } catch (ObjectOptimisticLockingFailureException exception) {
                System.out.println("Transaction denied, machine currently working on another transaction.");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Transactional
    public void stopMachine(Long machineID) {
        Machine machine = this.machineRepository.findById(machineID).get();

        new Thread(() -> {
            try {
                Thread.sleep(10000);
                machine.setStatus(Status.STOPPED);
                this.machineRepository.save(machine);
            } catch (ObjectOptimisticLockingFailureException exception) {
                System.out.println("Transaction denied, machine currently working on another transaction.");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Transactional
    @Modifying
    public void restartMachine(Long machineID) {
        Machine machine = machineRepository.findById(machineID).get();
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                machine.setStatus(Status.STOPPED);
                machineRepository.save(machine);
                Thread.sleep(5000);
                Machine ma = machineRepository.findById(machineID).get();
                ma.setStatus(Status.RUNNING);
                machineRepository.save(ma);
            } catch (ObjectOptimisticLockingFailureException exception) {
                System.out.println("Transaction denied, machine currently working on another transaction.");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
