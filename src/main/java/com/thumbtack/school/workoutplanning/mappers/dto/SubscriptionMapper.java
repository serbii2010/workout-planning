package com.thumbtack.school.workoutplanning.mappers.dto;

import com.thumbtack.school.workoutplanning.dto.request.subscription.BaseSubscriptionDtoRequest;
import com.thumbtack.school.workoutplanning.dto.request.subscription.SubscriptionLimitedDtoRequest;
import com.thumbtack.school.workoutplanning.dto.request.subscription.SubscriptionUnlimitedDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.subscription.BaseSubscriptionDtoResponse;
import com.thumbtack.school.workoutplanning.dto.response.subscription.SubscriptionLimitedDtoResponse;
import com.thumbtack.school.workoutplanning.dto.response.subscription.SubscriptionUnlimitedDtoResponse;
import com.thumbtack.school.workoutplanning.model.Subscription;
import com.thumbtack.school.workoutplanning.model.SubscriptionLimited;
import com.thumbtack.school.workoutplanning.model.SubscriptionType;
import com.thumbtack.school.workoutplanning.model.SubscriptionUnlimited;
import com.thumbtack.school.workoutplanning.service.UserService;
import com.thumbtack.school.workoutplanning.utils.DateTimeUtils;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", imports = {SubscriptionType.class, DateTimeUtils.class})
public interface SubscriptionMapper {
    SubscriptionMapper INSTANCE = Mappers.getMapper(SubscriptionMapper.class);

    @Mapping(target = "remaining", source = "total")
    @Mapping(target = "type", expression = "java(SubscriptionType.LIMITED)")
    @Mapping(target = "user", expression = "java(userService.findByUsername(request.getUsername()))")
    SubscriptionLimited requestToSubscription(SubscriptionLimitedDtoRequest request, @Context UserService userService);

    @Mapping(target = "type", expression = "java(SubscriptionType.UNLIMITED)")
    @Mapping(target = "user", expression = "java(userService.findByUsername(request.getUsername()))")
    @Mapping(target = "toDate", expression = "java(request.getFromDate().plusMonths(request.getCountMonth()))")
    SubscriptionUnlimited requestToSubscription(SubscriptionUnlimitedDtoRequest request, @Context UserService userService);
    default Subscription requestToSubscription(BaseSubscriptionDtoRequest request, @Context UserService userService) {
        if (request instanceof SubscriptionLimitedDtoRequest) {
            return requestToSubscription((SubscriptionLimitedDtoRequest) request, userService);
        } else if (request instanceof SubscriptionUnlimitedDtoRequest) {
            return requestToSubscription((SubscriptionUnlimitedDtoRequest) request, userService);
        }
        return null;
    }

    @Mapping(target = "type", expression = "java(SubscriptionType.LIMITED.getValue())")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "creationTimestamp", expression = "java(subscription.getCreationTimestamp() != null?subscription.getCreationTimestamp().format(DateTimeUtils.formatter):null)")
    SubscriptionLimitedDtoResponse subscriptionToDtoResponse(SubscriptionLimited subscription);

    @Mapping(target = "type", expression = "java(SubscriptionType.UNLIMITED.getValue())")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "creationTimestamp", expression = "java(subscription.getCreationTimestamp() != null?subscription.getCreationTimestamp().format(DateTimeUtils.formatter):null)")
    SubscriptionUnlimitedDtoResponse subscriptionToDtoResponse(SubscriptionUnlimited subscription);
    default BaseSubscriptionDtoResponse subscriptionToDtoResponse(Subscription subscription) {
        if (subscription instanceof SubscriptionUnlimited) {
            return subscriptionToDtoResponse((SubscriptionUnlimited) subscription);
        } else if (subscription instanceof SubscriptionLimited) {
            return subscriptionToDtoResponse((SubscriptionLimited) subscription);
        }
        return null;
    }
}
