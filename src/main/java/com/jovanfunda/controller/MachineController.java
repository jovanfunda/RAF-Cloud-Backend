package com.jovanfunda.controller;

import com.jovanfunda.authentication.AuthService;
import com.jovanfunda.model.database.ErrorHistory;
import com.jovanfunda.model.database.Machine;
import com.jovanfunda.model.enums.Permission;
import com.jovanfunda.model.requests.MachineFilterRequest;
import com.jovanfunda.model.requests.ScheduleJobRequest;
import com.jovanfunda.service.MachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MachineController {

    @Autowired
    private MachineService machineService;

    @Autowired
    private AuthService authService;

    public MachineController(MachineService machineService, AuthService authService) {
        this.machineService = machineService;
        this.authService = authService;
    }

    @GetMapping("/machines")
    public ResponseEntity<List<Machine>> getMachines(@RequestHeader String jwtoken) {
        if (authService.isTokenExpired(jwtoken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else if (authService.hasPermission(jwtoken, Permission.CAN_SEARCH_MACHINES)) {
            String userEmail = authService.getEmailFromToken(jwtoken);
            return ResponseEntity.ok().body(machineService.getMachines(userEmail));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/createMachine")
    public ResponseEntity<Void> createMachine(@RequestHeader String jwtoken, @RequestBody String machineName) {
        if (authService.isTokenExpired(jwtoken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else if (authService.hasPermission(jwtoken, Permission.CAN_CREATE_MACHINES)) {
            String userEmail = authService.getEmailFromToken(jwtoken);
            machineService.createMachine(userEmail, machineName);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/filterMachine")
    public ResponseEntity<List<Machine>> filterMachine(@RequestHeader String jwtoken, @RequestBody MachineFilterRequest machineFilter) {
        if (authService.isTokenExpired(jwtoken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else if (authService.hasPermission(jwtoken, Permission.CAN_SEARCH_MACHINES)) {
            String userEmail = authService.getEmailFromToken(jwtoken);
            List<Machine> machines = machineService.filterMachine(userEmail, machineFilter);
            return ResponseEntity.ok().body(machines);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/deleteMachine/{machineID}")
    public ResponseEntity<Boolean> deleteMachine(@RequestHeader String jwtoken, @PathVariable Long machineID) {
        if (authService.isTokenExpired(jwtoken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else if (authService.hasPermission(jwtoken, Permission.CAN_DESTROY_MACHINES)) {
            boolean deleted = machineService.destroyMachine(machineID);
            return ResponseEntity.ok().body(deleted);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/startMachine/{machineID}")
    public ResponseEntity<Void> startMachine(@RequestHeader String jwtoken, @PathVariable Long machineID) {
        if (authService.isTokenExpired(jwtoken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else if (authService.hasPermission(jwtoken, Permission.CAN_START_MACHINES)) {
            machineService.startMachine(machineID);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/stopMachine/{machineID}")
    public ResponseEntity<Void> stopMachine(@RequestHeader String jwtoken, @PathVariable Long machineID) {
        if (authService.isTokenExpired(jwtoken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else if (authService.hasPermission(jwtoken, Permission.CAN_STOP_MACHINES)) {
            machineService.stopMachine(machineID);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/restartMachine/{machineID}")
    public ResponseEntity<Void> restartMachine(@RequestHeader String jwtoken, @PathVariable Long machineID) {
        if (authService.isTokenExpired(jwtoken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else if (authService.hasPermission(jwtoken, Permission.CAN_RESTART_MACHINES)) {
            machineService.restartMachine(machineID);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/scheduleJob")
    public ResponseEntity<Void> scheduleJob(@RequestHeader String jwtoken, @RequestBody ScheduleJobRequest scheduleJobRequest) {
        if (authService.isTokenExpired(jwtoken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            machineService.scheduleMachine(scheduleJobRequest.getMachineID(), scheduleJobRequest.getJob(), scheduleJobRequest.getScheduleTime());
            return ResponseEntity.ok().build();
        }
    }

    @GetMapping("/errorHistory")
    public ResponseEntity<List<ErrorHistory>> getErrors(@RequestHeader String jwtoken) {
        if (authService.isTokenExpired(jwtoken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else if (authService.hasPermission(jwtoken, Permission.CAN_SEARCH_MACHINES)) {
            String userEmail = authService.getEmailFromToken(jwtoken);
            return ResponseEntity.ok().body(machineService.getErrors(userEmail));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
