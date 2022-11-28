package com.thumbtack.school.workoutplanning.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class IsCountLimitWorkoutValidator implements ConstraintValidator<IsCountLimitWorkout, Integer> {
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        List<Integer> list = List.of(8, 12, 24);
        return list.contains(value);
    }
}
