package com.artenatural.Back.repositories;

import com.artenatural.Back.entities.User;
import com.artenatural.Back.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Integer> {

     Role findByRoleName(String rolename);
}
