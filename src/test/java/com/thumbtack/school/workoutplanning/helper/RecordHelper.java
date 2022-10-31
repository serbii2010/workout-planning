package com.thumbtack.school.workoutplanning.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thumbtack.school.workoutplanning.dto.request.workout.RecordDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.workout.RecordDtoResponse;
import com.thumbtack.school.workoutplanning.model.StatusRecord;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.servlet.http.Cookie;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class RecordHelper {
    public  static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static RecordDtoRequest getRecordDtoRequest() {
        return new RecordDtoRequest(2L, "client");
    }

    public static RecordDtoRequest getRecordDtoRequestNow() {
        return new RecordDtoRequest(1L, "client");
    }

    public static RecordDtoRequest getRecordTwoDtoRequest() {
        return new RecordDtoRequest(2L, "client2");
    }

    public static RecordDtoResponse getRecordDtoResponse() {
        return new RecordDtoResponse(
                1,
                "client",
                "clientFirst",
                "clientLast",
                "trainer",
                LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.parse("11:30")).
                        format(formatter),
                LocalDateTime.now().format(formatter),
                StatusRecord.ACTIVE.name()
        );
    }

    public static RecordDtoResponse getRecordTwoActiveDtoResponse() {
        return new RecordDtoResponse(
                2,
                "client2",
                "clientFirst",
                "clientLast",
                "trainer",
                LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.parse("11:30")).
                        format(formatter),
                LocalDateTime.now().format(formatter),
                StatusRecord.ACTIVE.name()
        );
    }

    public static RecordDtoResponse getRecordQueueDtoResponse() {
        return new RecordDtoResponse(
                2,
                "client2",
                "clientFirst",
                "clientLast",
                "trainer",
                LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.parse("11:30")).
                        format(formatter),
                LocalDateTime.now().format(formatter),
                StatusRecord.QUEUED.name()
        );
    }

    public static RecordDtoResponse getRecordCancelDtoResponse() {
        return new RecordDtoResponse(
                1,
                "client",
                "clientFirst",
                "clientLast",
                "trainer",
                LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.parse("11:30")).
                        format(formatter),
                LocalDateTime.now().format(formatter),
                StatusRecord.CANCELLED.name()
        );
    }

    public static RecordDtoResponse getRecordCancelByTimeDtoResponse(LocalDateTime dateTime) {
        return new RecordDtoResponse(
                1,
                "client",
                "clientFirst",
                "clientLast",
                "trainer",
                LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.parse("11:30")).
                        format(formatter),
                LocalDateTime.now().format(formatter),
                StatusRecord.CANCELLED.name()
        );
    }

    public static RecordDtoResponse getRecordSkipDtoResponse() {
        return new RecordDtoResponse(
                1,
                "client",
                "clientFirst",
                "clientLast",
                "trainer",
                LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.parse("11:30")).
                        format(formatter),
                LocalDateTime.now().format(formatter),
                StatusRecord.SKIPPED.name()
        );
    }

    public static void insertRecordByClient(MockMvc mvc, ObjectMapper mapper, Cookie cookie) throws Exception {
        RecordDtoRequest request = RecordHelper.getRecordDtoRequest();

        mvc.perform(post("/api/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .cookie(cookie));
    }

    public static void insertRecordByClient2InQueue(MockMvc mvc, ObjectMapper mapper, Cookie cookie) throws Exception {
        RecordDtoRequest request = RecordHelper.getRecordTwoDtoRequest();

        mvc.perform(post("/api/records/queue")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .cookie(cookie));
    }

    public static void cancelRecordClient1(MockMvc mvc, ObjectMapper mapper, Cookie cookie) throws Exception {
        RecordDtoRequest request = RecordHelper.getRecordDtoRequest();
        mvc.perform(put("/api/records/"+StatusRecord.CANCELLED.name())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .cookie(cookie));
    }
}
