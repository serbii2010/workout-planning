package com.thumbtack.school.workoutplanning.dto.request.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionActivateDtoRequest {
    private Boolean isActive;
}
