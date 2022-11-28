package com.thumbtack.school.workoutplanning.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class IsCountMonthValidator implements ConstraintValidator<IsCountMonth, Integer> {
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        List<Integer> list = List.of(3, 6, 12);
        return list.contains(value);
    }
}
