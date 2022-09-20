package com.thumbtack.school.workoutplanning.repository;

import com.thumbtack.school.workoutplanning.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String name);
}
