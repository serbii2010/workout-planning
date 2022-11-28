package com.thumbtack.school.workoutplanning.dto.request.subscription;

import com.thumbtack.school.workoutplanning.validator.IsCountMonth;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class SubscriptionUnlimitedDtoRequest extends BaseSubscriptionDtoRequest {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDate;
    @IsCountMonth
    private Integer countMonth;

    public SubscriptionUnlimitedDtoRequest(String username, LocalDate fromDate, Integer countMonth) {
        super(username);
        this.fromDate = fromDate;
        this.countMonth = countMonth;
    }
}
