package com.thumbtack.school.workoutplanning.model;

import java.util.Set;
import javax.persistence.Column;
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
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        name = "user",
        schema = "public"
)
@RequiredArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min = 4, message = "Min length 5 symbols")
    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String email;
    private String firstName;
    private String lastName;
    @Size(min = 10, message = "Min length 10 symbols")
    private String phone;
    @Size(min = 5, message = "Min length 5 symbols")
    private String password;
    private Boolean isActive = true;
    private Boolean isSocial = false;

    @ManyToOne(fetch = FetchType.EAGER)
    private Role role;

    @ManyToMany
    @JoinTable(
            name = "record",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "workout_id")}
    )
    private Set<Workout> workouts;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<Record> records;

    public User(String username, String password,  String firstName, String lastName, String email, Boolean isSocial) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.isSocial = isSocial;
    }
}
