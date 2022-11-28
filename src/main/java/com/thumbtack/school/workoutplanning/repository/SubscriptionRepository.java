package com.thumbtack.school.workoutplanning.repository;

import com.thumbtack.school.workoutplanning.model.Subscription;
import com.thumbtack.school.workoutplanning.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long>, JpaSpecificationExecutor<Subscription> {
    Optional<Subscription> findByUserAndIsActive(User client, boolean b);
}
