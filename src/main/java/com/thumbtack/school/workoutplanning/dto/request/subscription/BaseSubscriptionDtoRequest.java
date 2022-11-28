package com.thumbtack.school.workoutplanning.dto.request.subscription;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.thumbtack.school.workoutplanning.model.SubscriptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SubscriptionLimitedDtoRequest.class, name = SubscriptionType.LIMITED_VALUE),
        @JsonSubTypes.Type(value = SubscriptionUnlimitedDtoRequest.class, name = SubscriptionType.UNLIMITED_VALUE)
})
public abstract class BaseSubscriptionDtoRequest {
    private String username;
}
