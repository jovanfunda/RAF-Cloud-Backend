package com.jovanfunda.service;

import com.jovanfunda.model.database.ErrorHistory;
import com.jovanfunda.model.database.Machine;
import com.jovanfunda.model.database.User;
import com.jovanfunda.model.enums.Status;
import com.jovanfunda.model.requests.MachineFilterRequest;
import com.jovanfunda.repository.ErrorRepository;
import com.jovanfunda.repository.MachineRepository;
import com.jovanfunda.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
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

    @Autowired
    ErrorRepository errorRepository;

    TaskScheduler taskScheduler;

    public MachineService(MachineRepository machineRepository, UserRepository userRepository, ErrorRepository errorRepository, TaskScheduler taskScheduler) {
        this.machineRepository = machineRepository;
        this.userRepository = userRepository;
        this.errorRepository = errorRepository;
        this.taskScheduler = taskScheduler;
    }

    public List<Machine> getMachines(String userEmail) {
        User user = userRepository.findById(userEmail).get();
        return machineRepository.findAll().stream().filter(m -> (m.getCreatedBy() == user) && m.getActive()).collect(Collectors.toList());
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
                System.out.println("Start transaction denied, machine currently working on another transaction.");
                errorRepository.save(new ErrorHistory(new Date(), machineID, "Start", "Transaction denied, another transaction currently running"));
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
                System.out.println("Stop transaction denied, machine currently working on another transaction.");
                errorRepository.save(new ErrorHistory(new Date(), machineID, "Stop", "Transaction denied, another transaction currently running"));
            } catch (InterruptedException e) {
                System.out.println("Runtime Exception Stopped");
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
//                machine.setStatus(Status.STOPPED);
//                machineRepository.save(machine);
                System.out.println(machineRepository.findById(machineID).get().getVersion());
                machineRepository.firstRestartStep(machineID);
                System.out.println(machineRepository.findById(machineID).get().getVersion());
                Thread.sleep(5000);
                Machine ma = machineRepository.findById(machineID).get();
                ma.setStatus(Status.RUNNING);
                machineRepository.save(ma);
            } catch (ObjectOptimisticLockingFailureException exception) {
                System.out.println("Restart transaction denied, machine currently working on another transaction.");
                errorRepository.save(new ErrorHistory(new Date(), machineID, "Restart", "Transaction denied, another transaction currently running"));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Transactional
    public void scheduleMachine(Long machineID, String job, String scheduleTime) {
        Schedule schedule = new Schedule(scheduleTime);
        CronTrigger cronTrigger = new CronTrigger("0 " + schedule.minute + " " + schedule.hour + " " + schedule.date + " " + schedule.month + " *");
        taskScheduler.schedule(() -> {
            switch (job) {
                case "start":
                    startMachine(machineID);
                    break;
                case "stop":
                    stopMachine(machineID);
                    break;
                case "restart":
                    restartMachine(machineID);
                    break;
            }
        }, cronTrigger);
    }

    class Schedule {
        private String minute;
        private String hour;
        private String month;
        private String date;

        public Schedule(String scheduleTime) {
            date = scheduleTime.split("\\.")[0];
            month = scheduleTime.split(" ")[0].split("\\.")[1];
            hour = scheduleTime.split(" ")[1].split(":")[0];
            minute = scheduleTime.split(" ")[1].split(":")[1];
        }

        @Override
        public String toString() {
            return "Schedule{" +
                    "minute='" + minute + '\'' +
                    ", hour='" + hour + '\'' +
                    ", month='" + month + '\'' +
                    ", date='" + date + '\'' +
                    '}';
        }
    }

    public List<ErrorHistory> getErrors(String userEmail) {
        return errorRepository.findAll().stream().filter(e -> machineRepository.findById(e.getMachineID()).get().getCreatedBy() == (userRepository.findById(userEmail)).get()).collect(Collectors.toList());
    }
}
