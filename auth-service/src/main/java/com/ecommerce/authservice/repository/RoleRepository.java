package com.ecommerce.authservice.repository;

import com.ecommerce.authservice.entity.Role;
import com.ecommerce.authservice.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);

}