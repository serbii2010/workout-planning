package com.thumbtack.school.workoutplanning.repository;

import com.thumbtack.school.workoutplanning.model.AuthType;
import com.thumbtack.school.workoutplanning.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(AuthType name);
}
