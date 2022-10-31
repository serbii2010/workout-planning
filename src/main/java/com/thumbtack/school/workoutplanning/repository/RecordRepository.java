package com.thumbtack.school.workoutplanning.repository;

import com.thumbtack.school.workoutplanning.model.Record;
import com.thumbtack.school.workoutplanning.model.StatusRecord;
import com.thumbtack.school.workoutplanning.model.User;
import com.thumbtack.school.workoutplanning.model.Workout;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findAllByStatusOrderByDateTime(StatusRecord status);

    Record findByUserAndWorkout(User user, Workout workout);
}
