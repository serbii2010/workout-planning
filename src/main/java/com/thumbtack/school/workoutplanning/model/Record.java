package com.thumbtack.school.workoutplanning.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(
        name = "record",
        schema = "public",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "workout_id"})
)
@RequiredArgsConstructor
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;
    @ManyToOne(fetch = FetchType.EAGER)
    private Workout workout;

    private StatusRecord status = StatusRecord.ACTIVE;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime dateTime;

    public Record(User client, Workout workout, StatusRecord status) {
        this(null, client, workout, status, null);
    }
}
