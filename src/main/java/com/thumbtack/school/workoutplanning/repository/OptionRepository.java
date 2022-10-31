package com.thumbtack.school.workoutplanning.repository;

import com.thumbtack.school.workoutplanning.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<Option, Long> {
    Option findByName(String name);
}
