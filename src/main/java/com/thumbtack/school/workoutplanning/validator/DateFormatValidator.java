package com.thumbtack.school.workoutplanning.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.validator.GenericValidator;

public class DateFormatValidator  implements ConstraintValidator<DateFormat, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return GenericValidator.isDate(value, "yyyy-MM-dd", true);
    }
}
