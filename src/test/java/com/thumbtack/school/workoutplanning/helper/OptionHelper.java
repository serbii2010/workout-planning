package com.thumbtack.school.workoutplanning.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thumbtack.school.workoutplanning.dto.request.options.OptionDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.option.OptionDtoResponse;
import javax.servlet.http.Cookie;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class OptionHelper {
    public static OptionDtoRequest getOptionDtoRequest() {
        return new OptionDtoRequest(
                "2"
        );
    }

    public static OptionDtoRequest getOptionBeforeMorningDtoRequest() {
        return new OptionDtoRequest(
                "19:00"
        );
    }


    public static OptionDtoResponse getOptionHourDtoResponse() {
        return new OptionDtoResponse(
                "HOUR_BEFORE_WORKOUT",
                "2"
        );
    }

    public static OptionDtoResponse getOptionBeforeMorningDtoResponse() {
        return new OptionDtoResponse(
                "TIME_BEFORE_MORNING_WORKOUT",
                "19:00"
        );
    }
    public static void setOptionHourBeforeWorkout(MockMvc mvc, ObjectMapper mapper, Cookie cookie) throws Exception {
        OptionDtoRequest request = OptionHelper.getOptionDtoRequest();

        mvc.perform(post("/api/rules/HOUR_BEFORE_WORKOUT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .cookie(cookie));
    }

    public static void setOptionBeforeMorningWorkout(MockMvc mvc, ObjectMapper mapper, Cookie cookie) throws Exception {
        OptionDtoRequest request = OptionHelper.getOptionBeforeMorningDtoRequest();

        mvc.perform(post("/api/rules/TIME_BEFORE_MORNING_WORKOUT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .cookie(cookie));
    }

}
