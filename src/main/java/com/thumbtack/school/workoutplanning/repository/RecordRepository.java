package com.thumbtack.school.workoutplanning.repository;

import com.thumbtack.school.workoutplanning.model.Record;
import com.thumbtack.school.workoutplanning.model.RecordStatus;
import com.thumbtack.school.workoutplanning.model.User;
import com.thumbtack.school.workoutplanning.model.Workout;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Long> {
    Record findByUserAndWorkout(User user, Workout workout);
    List<Record> findAllByStatusAndWorkoutOrderByDateTime(RecordStatus status, Workout workout);
    List<Record> findAllByUserAndStatus(User user, RecordStatus active);
}
