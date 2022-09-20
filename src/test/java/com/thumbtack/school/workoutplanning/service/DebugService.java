package com.thumbtack.school.workoutplanning.service;

import com.thumbtack.school.workoutplanning.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DebugService {
    @Autowired
    private UserRepository userRepository;

    public void clear() {
        userRepository.deleteAll();
    }

}
