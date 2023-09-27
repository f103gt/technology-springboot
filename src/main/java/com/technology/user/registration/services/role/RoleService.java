package com.technology.user.registration.services.role;

public interface RoleService {
    void addRoleManager(String username);
    void addRoleAdmin(String username);

    void deleteRoleManager(String username);
    void deleteRoleAdmin(String username);

}
