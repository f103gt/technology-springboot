package com.technology.user.controllers;

import com.technology.role.services.RoleService;
import com.technology.shift.services.ShiftServiceV2;
import com.technology.user.dto.UserDto;
import com.technology.user.services.NewEmployeeServiceV2;
import com.technology.user.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdministrationController {
    //TODO check if user to delete is not a current user
    private final RoleService roleService;
    private final UserService userService;
    private final NewEmployeeServiceV2 newEmployeeService;
    private final ShiftServiceV2 shiftService;


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/admin/all-users")
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.getAllUsers());
    }

    //TODO ADD RESPONSE DATA TYPE
    @PostMapping(value = "/admin/add-new-employees",
            consumes = "multipart/form-data")
    public ResponseEntity<String> uploadNewEmployeesData(
            @RequestParam("newEmployeesData") MultipartFile data) {
        newEmployeeService.parseFile(data);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/admin/distribute-shifts")
    public ResponseEntity<String> distributeShifts(
            @RequestParam("shifts") MultipartFile data) {
        shiftService.parseCSVFile(data);
        return ResponseEntity.ok().build();
    }

    //TODO REVISE ALL ADD- CONTROLLERS
    @PatchMapping("/admin/add-manager")
    public ResponseEntity<String> addManager(@RequestBody String username) {
        roleService.addRoleManager(username);
        return ResponseEntity.ok("User role was changed to manager");
    }

    @PatchMapping("/admin/add-admin")
    public ResponseEntity<String> makeAdmin(@RequestBody String username) {
        roleService.addRoleAdmin(username);
        return ResponseEntity.ok("User role was changed to admin");
    }

    @DeleteMapping("/admin/delete-manager")
    public ResponseEntity<String> deleteManager(@RequestBody String username) {
        roleService.deleteRoleManager(username);
        return ResponseEntity.ok("User has no management authorities");
    }
}
