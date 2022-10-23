package com.thumbtack.school.workoutplanning.mappers.dto;

import com.thumbtack.school.workoutplanning.dto.request.account.RegistrationDtoRequest;
import com.thumbtack.school.workoutplanning.dto.request.account.UpdateAccountDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.account.AuthDtoResponse;
import com.thumbtack.school.workoutplanning.dto.response.account.UserDetailsResponse;
import com.thumbtack.school.workoutplanning.model.AuthType;
import com.thumbtack.school.workoutplanning.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", imports = AuthType.class)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    User registrationDtoToUser(RegistrationDtoRequest request);

    @Mapping(target = "role", source = "role.name")
    AuthDtoResponse userToDtoResponse(User user);

    @Mapping(target = "role", source = "role.name")
    UserDetailsResponse userToDetailsDtoResponse(User user);

    void updateDtoToUser(@MappingTarget User user, UpdateAccountDtoRequest request);
}
