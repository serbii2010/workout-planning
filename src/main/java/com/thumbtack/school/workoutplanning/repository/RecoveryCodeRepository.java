package com.thumbtack.school.workoutplanning.repository;

import com.thumbtack.school.workoutplanning.model.RecoveryCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecoveryCodeRepository extends JpaRepository<RecoveryCode, Long> {
    RecoveryCode findByCode(String code);
}
