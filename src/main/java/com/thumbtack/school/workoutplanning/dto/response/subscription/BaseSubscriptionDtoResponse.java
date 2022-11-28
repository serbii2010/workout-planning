package com.thumbtack.school.workoutplanning.dto.response.subscription;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseSubscriptionDtoResponse {
    private Long id;
    private String username;
    private boolean isActive;
    private String creationTimestamp;
    private String type;
}
