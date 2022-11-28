package com.thumbtack.school.workoutplanning.dto.request.subscription;

import com.thumbtack.school.workoutplanning.validator.IsCountLimitWorkout;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionLimitedDtoRequest extends BaseSubscriptionDtoRequest {
    @IsCountLimitWorkout
    private Integer total;

    public SubscriptionLimitedDtoRequest(String username, Integer total) {
        super(username);
        this.total = total;
    }
}
