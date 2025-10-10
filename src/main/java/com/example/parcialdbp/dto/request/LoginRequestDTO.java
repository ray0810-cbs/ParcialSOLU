package com.example.parcialdbp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {
    @NotBlank(message = "Username obligatorio")
    private String username;

    @NotBlank(message = "Contraseña obligatoria")
    @Size(min = 8)
    private String password;
}
