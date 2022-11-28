package com.thumbtack.school.workoutplanning.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IsCountMonthValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsCountMonth {
    String message() default "Bad count workouts. Set count in 8 or 12 or 24";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
