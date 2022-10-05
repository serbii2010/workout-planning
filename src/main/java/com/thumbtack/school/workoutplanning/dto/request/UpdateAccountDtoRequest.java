package com.thumbtack.school.workoutplanning.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAccountDtoRequest {
    @NotBlank
    @Pattern(regexp = "^[А-Яа-я \\-]+$", message = "FirstName Invalid characters are used")
    private String firstName;
    @NotBlank
    @Pattern(regexp = "^[А-Яа-я \\-]+$", message = "LastName Invalid characters are used")
    private String lastName;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$",
            message = "Phone number incorrectly")
    private String phone;
}
