package com.thumbtack.school.workoutplanning.model;

import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import static com.thumbtack.school.workoutplanning.model.AccountState.ACTIVE;

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
    @Size(min = 4, message = "Min length 4 symbols")
    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String email;
    private String firstName;
    private String lastName;

    private String phone;

    private String password;

    @Column(columnDefinition = "INTEGER DEFAULT 0")
    @Enumerated(value = EnumType.ORDINAL)
    private AccountState state = ACTIVE;
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

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Record> records;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Subscription> subscriptions;

    public User(String username, String password,  String firstName, String lastName, String email, Boolean isSocial) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.isSocial = isSocial;
    }
}
