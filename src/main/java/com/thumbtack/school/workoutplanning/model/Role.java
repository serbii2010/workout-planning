package com.thumbtack.school.workoutplanning.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(
        name = "role",
        schema = "public"
)
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    @Enumerated(EnumType.STRING)
    private AuthType name;
    @Transient
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private Set<User> users;
}