package com.thumbtack.school.workoutplanning.dto.response.subscription;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionLimitedDtoResponse extends BaseSubscriptionDtoResponse {
    private Integer total;
    private Integer remaining;

    public SubscriptionLimitedDtoResponse(Long id, String username, boolean isActive, String timestamp, String type, Integer total, Integer remaining) {
        super(id, username, isActive, timestamp, type);
        this.total = total;
        this.remaining = remaining;
    }
}
