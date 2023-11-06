package com.technology.user.controllers;

import com.technology.user.dto.UserDto;
import com.technology.role.services.RoleService;
import com.technology.user.services.NewEmployeeService;
import com.technology.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class AdministrationController {
    //TODO check if user to delete is not a current user
    private final RoleService roleService;
    private final UserService userService;
    private final NewEmployeeService newEmployeeService;
    @Autowired
    public AdministrationController(RoleService roleService, UserService userService, NewEmployeeService newEmployeeService) {
        this.roleService = roleService;
        this.userService = userService;
        this.newEmployeeService = newEmployeeService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/admin/all-users")
    public ResponseEntity<List<UserDto>> getUsers(){
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.getAllUsers());
    }

    @PostMapping("/admin/add-new-employees")
    public ResponseEntity<String> uploadNewEmployeesData(@RequestParam("newEmployeesData")MultipartFile data){
        newEmployeeService.uploadNewEmployeesData(data);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .build();
    }

    @PatchMapping("/admin/add-manager")
    public ResponseEntity<String> addManager(@RequestBody String username){
        roleService.addRoleManager(username);
        return ResponseEntity.ok("User role was changed to manager");
    }

    @PatchMapping("/admin/add-admin")
    public ResponseEntity<String> makeAdmin(@RequestBody String username){
        roleService.addRoleAdmin(username);
        return ResponseEntity.ok("User role was changed to admin");
    }

    @DeleteMapping("/admin/delete-manager")
    public ResponseEntity<String> deleteManager(@RequestBody String username){
        roleService.deleteRoleManager(username);
        return ResponseEntity.ok("User has no management authorities");
    }
}
