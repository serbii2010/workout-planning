package com.thumbtack.school.workoutplanning.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.function.Supplier;

@Entity
@Getter
@Setter
@Table(
        name = "recovery_code",
        schema = "public"
)
@RequiredArgsConstructor
public class RecoveryCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    private String code;

    private LocalDateTime timeCreate;

    public RecoveryCode(Integer code, User user, Supplier<LocalDateTime> timeProvider) {
        this.code = code.toString();
        this.user = user;
        this.timeCreate = timeProvider.get();
    }
}
