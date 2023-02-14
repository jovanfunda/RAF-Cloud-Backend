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
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Service
public class MachineService {

    @Autowired
    MachineRepository machineRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ErrorRepository errorRepository;

    @Autowired
    TaskScheduler taskScheduler;

    private final Queue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();

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
        newMachine.setBusy(false);
        machineRepository.save(newMachine);
    }

    public List<Machine> filterMachine(String userEmail, MachineFilterRequest machineFilter) {

        List<Machine> machines = machineRepository.findAll();

        machines = machines.stream().filter(m -> m.getCreatedBy() == userRepository.findById(userEmail).get() && m.getActive()).collect(Collectors.toList());

        if (machineFilter.getName() != null) {
            machines = machines.stream().filter(m -> m.getName().toLowerCase().contains(machineFilter.getName().toLowerCase())).collect(Collectors.toList());
        }
        if (machineFilter.getStatus() != null) {
            machines = machines.stream().filter(m -> machineFilter.getStatus().contains(m.getStatus())).collect(Collectors.toList());
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
        System.err.println("start prvi " + machineID);
        int rowsUpdated = machineRepository.setBusy(machineID);
        new Thread(() -> {
            try {
                if(rowsUpdated > 0 ) {
                    Thread.sleep(10000);
                    machineRepository.setRunning(machineID);
                    machineRepository.unbusy(machineID);
                } else {
                    throw new Exception();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (Exception exception) {
                System.out.println("Start transaction denied, machine currently working on another transaction.");
                errorRepository.save(new ErrorHistory(new Date(), machineID, "Start", "Transaction denied, another transaction currently running"));
            }
        }).start();
    }

    @Transactional
    public void stopMachine(Long machineID) {
        System.err.println("stop prvi " + machineID);
        int rowsUpdated = machineRepository.setBusy(machineID);
        new Thread(() -> {
            try {
                if(rowsUpdated > 0) {
                    Thread.sleep(10000);
                    machineRepository.setStopped(machineID);
                    machineRepository.unbusy(machineID);
                } else {
                    throw new Exception();
                }
            } catch (InterruptedException e) {
                System.out.println("Runtime Exception Stopped");
                throw new RuntimeException(e);
            } catch (Exception exception) {
                System.out.println("Stop transaction denied, machine currently working on another transaction.");
                errorRepository.save(new ErrorHistory(new Date(), machineID, "Stop", "Transaction denied, another transaction currently running"));
            }
        }).start();
    }

    @Transactional
    @Modifying
    public void restartMachine(Long machineID) {
        System.err.println("restart prvi " + machineID);
        int rowsUpdated = machineRepository.setBusy(machineID);
        new Thread(() -> {
            try {
                if(rowsUpdated > 0) {
                    Thread.sleep(5000);
                    machineRepository.setStopped(machineID);
                    Thread.sleep(5000);
                    machineRepository.setRunning(machineID);
                    machineRepository.unbusy(machineID);
                } else {
                    throw new Exception();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (Exception exception) {
                System.out.println("Restart transaction denied, machine currently working on another transaction.");
                errorRepository.save(new ErrorHistory(new Date(), machineID, "Restart", "Transaction denied, another transaction currently running"));
            }
        }).start();
    }

    @Transactional
    public void scheduleMachine(Long machineID, String job, String scheduleTime) {
        Schedule schedule = new Schedule(scheduleTime);
        CronTrigger cronTrigger = new CronTrigger("0 " + schedule.minute + " " + schedule.hour + " " + schedule.date + " " + schedule.month + " *");
        Runnable task = () -> {
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
        };
        taskQueue.add(task);
        taskScheduler.schedule(() -> {
            Runnable nextTask = taskQueue.poll();
            if (nextTask != null) {
                nextTask.run();
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
