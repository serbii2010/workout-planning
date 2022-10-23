package com.thumbtack.school.workoutplanning.service;

import com.thumbtack.school.workoutplanning.repository.UserRepository;
import com.thumbtack.school.workoutplanning.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Service
@Transactional
public class DebugService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WorkoutRepository workoutRepository;
    @Autowired
    private EntityManager entityManager;

    public void clear() {
        workoutRepository.deleteAll();
        userRepository.deleteAll();
        entityManager.createNativeQuery("ALTER SEQUENCE  workout_id_seq RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER SEQUENCE  user_id_seq RESTART WITH 1").executeUpdate();
    }

}
