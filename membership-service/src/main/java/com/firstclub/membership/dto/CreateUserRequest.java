package com.firstclub.membership.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.Set;

@Data
public class CreateUserRequest {
    @NotBlank private String name;
    @Email @NotBlank private String email;
    private Set<String> cohortTags;
}
