package com.thumbtack.school.workoutplanning.repository;

import com.thumbtack.school.workoutplanning.model.Role;
import com.thumbtack.school.workoutplanning.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String name);
    List<User> findAllByRole(Role role);
    @Query("select p from User p where p.email=:identifier or p.username=:identifier")
    User findByEmailOrUsername(String identifier);
}
