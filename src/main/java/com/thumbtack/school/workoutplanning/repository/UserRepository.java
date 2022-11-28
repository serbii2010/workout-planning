package com.thumbtack.school.workoutplanning.repository;

import com.thumbtack.school.workoutplanning.model.Role;
import com.thumbtack.school.workoutplanning.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findByUsername(String name);
    List<User> findAllByRole(Role role, Sort sort);
    @Query("select p from User p where p.email=:identifier or p.username=:identifier")
    User findByEmailOrUsername(String identifier);
}
