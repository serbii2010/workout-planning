package com.thumbtack.school.workoutplanning.repository;

import com.thumbtack.school.workoutplanning.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDate;
import java.time.LocalTime;

public interface WorkoutRepository extends JpaRepository<Workout, Long>,
        JpaSpecificationExecutor<Workout> {
    default Long getCountWorkoutByTime(EntityManager entityManager, LocalDate date, LocalTime timeStart, Integer duration, Workout workout) {
        String queryStr = "SELECT count(*) " +
                "FROM workout " +
                "WHERE " +
                "   workout.date=:date " +
                "   AND ((time_start between :timeStart and :timeEnd) " +
                "        OR (time_start + (interval '1' minute) * duration) between :timeStart and :timeEnd)";

        if (workout != null) {
            queryStr = queryStr + " AND workout.id != :workoutId";
        }
        Query query = entityManager.createNativeQuery(queryStr);
        query.setParameter("date", date);
        query.setParameter("timeStart", timeStart);
        query.setParameter("timeEnd", timeStart.plusMinutes(duration));
        if (workout != null) {
            query.setParameter("workoutId", workout.getId());
        }
        Object res = query.getSingleResult();
        return Long.parseLong(res.toString());
    }
}
