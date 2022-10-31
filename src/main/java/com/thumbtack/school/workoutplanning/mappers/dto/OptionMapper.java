package com.thumbtack.school.workoutplanning.mappers.dto;

import com.thumbtack.school.workoutplanning.dto.response.option.OptionDtoResponse;
import com.thumbtack.school.workoutplanning.model.Option;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OptionMapper {
    OptionMapper INSTANCE = Mappers.getMapper(OptionMapper.class);

    OptionDtoResponse optionToDtoResponse(Option option);
}
