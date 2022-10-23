package com.thumbtack.school.workoutplanning.repository;

import com.thumbtack.school.workoutplanning.model.Workout;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkoutRepository extends JpaRepository<Workout, Long>,
        JpaSpecificationExecutor<Workout> {
    @Query("select count(w) from Workout w where " +
            "w.date=:date and " +
            "w.timeStart >= :timeStart and " +
            "w.timeStart <= :timeEnd")
    long getCountWorkoutByTrainerTime(@Param("date") LocalDate date,
                                      @Param("timeStart") LocalTime timeStart,
                                      @Param("timeEnd") LocalTime timeEnd);
}
