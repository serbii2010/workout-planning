package com.thumbtack.school.workoutplanning.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        name = "workout",
        schema = "public"
)
@RequiredArgsConstructor
public class Workout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private LocalTime timeStart;
    private Integer duration;
    @ManyToOne(fetch = FetchType.EAGER)
    private User trainer;
    private String typeWorkout;
    private Integer totalSeats;
    private Integer availableSeats;

    @ManyToMany
    @JoinTable(
            name = "record",
            joinColumns = {@JoinColumn(name = "workout_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    private Set<User> clients;

    @OneToMany(mappedBy = "workout", fetch = FetchType.EAGER)
    private List<Record> records;
}
