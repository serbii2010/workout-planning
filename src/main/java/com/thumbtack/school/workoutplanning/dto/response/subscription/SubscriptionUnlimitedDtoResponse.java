package com.thumbtack.school.workoutplanning.dto.response.subscription;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionUnlimitedDtoResponse extends BaseSubscriptionDtoResponse {
    private LocalDate fromDate;
    private LocalDate toDate;

    public SubscriptionUnlimitedDtoResponse(Long id, String username, boolean isActive, String timestamp, String type, LocalDate fromDate, LocalDate toDate) {
        super(id, username, isActive, timestamp, type);
        this.fromDate = fromDate;
        this.toDate = toDate;
    }
}
