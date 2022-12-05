package com.thumbtack.school.workoutplanning.service;

import com.thumbtack.school.workoutplanning.exception.BadRequestErrorCode;
import com.thumbtack.school.workoutplanning.exception.BadRequestException;
import com.thumbtack.school.workoutplanning.model.Record;
import com.thumbtack.school.workoutplanning.model.RecordStatus;
import com.thumbtack.school.workoutplanning.model.Subscription;
import com.thumbtack.school.workoutplanning.model.SubscriptionLimited;
import com.thumbtack.school.workoutplanning.model.SubscriptionType;
import com.thumbtack.school.workoutplanning.model.SubscriptionUnlimited;
import com.thumbtack.school.workoutplanning.model.User;
import com.thumbtack.school.workoutplanning.repository.Operator;
import com.thumbtack.school.workoutplanning.repository.RecordRepository;
import com.thumbtack.school.workoutplanning.repository.SubscriptionRepository;
import com.thumbtack.school.workoutplanning.repository.SubscriptionSpecificationBuilder;
import com.thumbtack.school.workoutplanning.utils.AuthUtils;
import java.time.LocalDate;
import java.util.List;
import javax.transaction.Transactional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@Transactional(rollbackOn = {RuntimeException.class}, dontRollbackOn = {AccessDeniedException.class})
@Slf4j
public class SubscriptionService {
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private RecordRepository recordRepository;

    public Subscription insert(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    public List<Subscription> filter(SubscriptionType type, String username, LocalDate dateStart, LocalDate dateEnd) {
        Specification<Subscription> specification = getSubscriptionSpecification(type, username, dateStart, dateEnd);
        return subscriptionRepository.findAll(specification, Sort.by("creationTimestamp").descending());
    }

    public void countdown(User client, LocalDate dateWorkout) {
        Subscription subscription = getActiveSubscriptionByUser(client);
        checkCounter(subscription);

        if (subscription instanceof SubscriptionLimited) {
            SubscriptionLimited subscriptionLimited = (SubscriptionLimited) subscription;
            subscriptionLimited.setRemaining(subscriptionLimited.getRemaining() - 1);
            subscriptionRepository.save(subscriptionLimited);
        }
        if (subscription instanceof SubscriptionUnlimited) {
            SubscriptionUnlimited subscriptionUnlimited = (SubscriptionUnlimited) subscription;

            if (dateWorkout.isAfter(subscriptionUnlimited.getToDate())) {
                throw new BadRequestException(BadRequestErrorCode.DATE_WORKOUT_IS_OUT_OF_SUBSCRIPTION_RANGE);
            }
        }
    }

    public void increment(User client) {
        Subscription subscription = getActiveSubscriptionByUser(client);
        if (subscription instanceof SubscriptionLimited) {
            SubscriptionLimited subscriptionLimited = (SubscriptionLimited) subscription;
            subscriptionLimited.setRemaining(subscriptionLimited.getRemaining() + 1);
            subscriptionRepository.save(subscriptionLimited);
        }
    }

    public Subscription setActive(Long id, Boolean isActive) {
        Subscription subscription = subscriptionRepository.findById(id).orElseThrow(
                () -> new BadRequestException(BadRequestErrorCode.SUBSCRIPTION_NOT_FOUND)
        );

        if (subscription.isActive() && isActive) {
            return subscription;
        }
        if (isActive && subscriptionRepository.findByUserAndIsActive(subscription.getUser(), true).isPresent()) {
            throw new BadRequestException(BadRequestErrorCode.ACTIVATED_SUBSCRIPTION_ALREADY_EXISTS);
        }
        subscription.setActive(isActive);
        subscriptionRepository.save(subscription);
        return subscription;
    }

    public Subscription getActiveSubscriptionByUser(User client) {
        Subscription subscription = subscriptionRepository.findByUserAndIsActive(client, true).orElseThrow(
                () -> new AccessDeniedException(BadRequestErrorCode.ACTIVATED_SUBSCRIPTION_NOT_FOUND.getErrorString())
        );
        checkActivatedSubscription(subscription);
        return subscription;
    }

    private void checkActivatedSubscription(Subscription subscription) {
        if (subscription instanceof SubscriptionLimited) {
            SubscriptionLimited sl = (SubscriptionLimited) subscription;
            if (sl.getRemaining() == 0 && getActiveAndQueuedRecordByUser(subscription.getUser()).size() == 0) {
                sl.setActive(false);
                this.subscriptionRepository.save(sl);
            }
        }
        if (subscription instanceof SubscriptionUnlimited) {
            SubscriptionUnlimited su = (SubscriptionUnlimited) subscription;
            if (LocalDate.now().isBefore(su.getFromDate())) {
                return;
            }
            if (su.getToDate().isBefore(LocalDate.now())) {
                su.setActive(false);
                this.subscriptionRepository.save(su);
            }
        }
        if (!subscription.isActive()) {
            throw new AccessDeniedException(BadRequestErrorCode.ACTIVATED_SUBSCRIPTION_NOT_FOUND.getErrorString());
        }
    }

    private void checkCounter(Subscription subscription) {
        if (!subscription.isActive()) {
            throw new AccessDeniedException("Subscription not active");
        }

        if (subscription instanceof SubscriptionLimited) {
            SubscriptionLimited sl = (SubscriptionLimited) subscription;
            if (sl.getRemaining() == 0) {
                throw new AccessDeniedException("Subscription counter is 0");
            }
        }
    }

    private List<Record> getActiveAndQueuedRecordByUser(User user) {
        List<Record> records = recordRepository.findAllByUserAndStatus(user, RecordStatus.ACTIVE);
        records.addAll(recordRepository.findAllByUserAndStatus(user, RecordStatus.QUEUED));
        return records;
    }

    private Specification<Subscription> getSubscriptionSpecification(
            @NonNull SubscriptionType type,
            String username,
            LocalDate dateStart,
            LocalDate dateEnd) {
        SubscriptionSpecificationBuilder builder = new SubscriptionSpecificationBuilder();

        builder.with("type", Operator.EQUALS, type);
        switch (AuthUtils.getRole()) {
            case CLIENT:
                builder.with("user.username", Operator.EQUALS_USER, AuthUtils.getUsername());
                break;
            case ADMIN:
                if (username != null) {
                    builder.with("user.username", Operator.LIKES_USER, username);
                }
                break;
            default:
                throw new AccessDeniedException("Action forbidden");
        }
        if (dateStart != null) {
            builder.with("timestamp", Operator.LARGE, dateStart);
        }
        if (dateEnd != null) {
            builder.with("timestamp", Operator.LESS, dateEnd);
        }
        return builder.build();
    }
}
