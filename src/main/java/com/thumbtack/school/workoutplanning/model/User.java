package com.thumbtack.school.workoutplanning.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

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
    @Size(min = 5, message = "Min length 5 symbols")
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

    @ManyToOne(fetch = FetchType.EAGER)
    private Role role;

}
