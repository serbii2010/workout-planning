package com.thumbtack.school.workoutplanning.service;

import com.thumbtack.school.workoutplanning.exception.BadRequestErrorCode;
import com.thumbtack.school.workoutplanning.exception.BadRequestException;
import com.thumbtack.school.workoutplanning.model.Option;
import com.thumbtack.school.workoutplanning.model.Workout;
import com.thumbtack.school.workoutplanning.repository.OptionRepository;
import com.thumbtack.school.workoutplanning.repository.RuleRecord;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Transactional(rollbackOn = {Throwable.class})
@Slf4j
public class RuleService {

    @Autowired
    private OptionRepository optionRepository;
    @Value("${time-morning}")
    private String timeMorning;
    public Option getRule(String rule) throws BadRequestException {
        try {
            RuleRecord nameRule = RuleRecord.valueOf(rule);
            Option option = optionRepository.findByName(nameRule.name());
            if (option == null) {
                option = new Option();
            }
            return option;
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException(BadRequestErrorCode.BAD_RULE_NAME);
        }
    }

    public Option setRule(String rule, String value) throws BadRequestException {
        try {
            RuleRecord nameRule = RuleRecord.valueOf(rule);
            validateRule(nameRule, value);
            Option option = optionRepository.findByName(nameRule.name());
            if (option == null) {
                option = new Option();
                option.setName(nameRule.name());
            }
            option.setValue(value);
            optionRepository.save(option);
            return option;
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException(BadRequestErrorCode.BAD_RULE_NAME);
        }
    }

    public void validateRule(RuleRecord nameRule, String value) throws BadRequestException {
        switch (nameRule) {
            case HOUR_BEFORE_WORKOUT:
                try {
                    if (value != null) {
                        Integer.parseInt(value);
                    }
                } catch (NumberFormatException ex) {
                    log.error("value HOUR_BEFORE_WORKOUT is not number");
                    throw new BadRequestException(BadRequestErrorCode.BAD_RULE_VALUE);
                }
                break;
            case TIME_BEFORE_MORNING_WORKOUT:
                try {
                    if (value != null) {
                        LocalTime.parse(value);
                    }
                } catch (DateTimeParseException ex) {
                    log.error("value TIME_BEFORE_MORNING_WORKOUT is not time");
                    throw new BadRequestException(BadRequestErrorCode.BAD_RULE_VALUE);
                }
                break;
        }
    }

    public void checkRules(Workout workout) throws BadRequestException {
        LocalDateTime dateTime = LocalDateTime.of(workout.getDate(), workout.getTimeStart());
        Option hourBefore = optionRepository.findByName(RuleRecord.HOUR_BEFORE_WORKOUT.name());
        if (hourBefore != null &&
                hourBefore.getValue() != null &&
                LocalDateTime.now().isAfter(dateTime.minusHours(Long.parseLong(hourBefore.getValue())))
        ) {
            BadRequestErrorCode error = BadRequestErrorCode.BAD_DATE_OR_TIME;
            error.setErrorString(String.format("You can not modify record for a workout less than %s hours before it starts", hourBefore.getValue()));
            throw new BadRequestException(error);
        }

        if (workout.getTimeStart().isBefore(LocalTime.parse(timeMorning))) {
            Option beforeMorningTime = optionRepository.findByName(RuleRecord.TIME_BEFORE_MORNING_WORKOUT.name());
            if (beforeMorningTime == null || beforeMorningTime.getValue() == null) {
                return;
            }
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime threshold = LocalDateTime.of(workout.getDate().minusDays(1), LocalTime.parse(beforeMorningTime.getValue()));
            if (now.isAfter(threshold)
            ) {
                BadRequestErrorCode error = BadRequestErrorCode.BAD_DATE_OR_TIME;
                error.setErrorString(String.format("You can not modify record for a morning workout after %s", beforeMorningTime.getValue()));
                throw new BadRequestException(error);
            }
        }
    }
}
