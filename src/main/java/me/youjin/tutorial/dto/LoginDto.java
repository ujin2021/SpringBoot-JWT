package me.youjin.tutorial.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {

    @NotNull
    @Size(min = 3, max = 50) // validation 관련 annotation
    private String username;

    @NotNull
    @Size(min = 3, max = 100)
    private String password;
}
