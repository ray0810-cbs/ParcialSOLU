package com.example.parcialdbp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    @NotBlank(message = "Username obligatorio")
    private String username;

    @NotBlank(message = "Email obligatorio")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Contraseña obligatoria")
    @Size(min = 8)
    private String password;
}
