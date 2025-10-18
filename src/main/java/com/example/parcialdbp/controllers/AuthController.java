package com.example.parcialdbp.controllers;

import com.example.parcialdbp.dto.request.LoginRequestDTO;
import com.example.parcialdbp.dto.request.UserRequestDTO;
import com.example.parcialdbp.dto.response.LoginResponseDTO;
import com.example.parcialdbp.dto.response.UserResponseDTO;
import com.example.parcialdbp.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO requestDTO) {
        UserResponseDTO responseDTO = authService.registrar(requestDTO);
        return  ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO requestDTO) {
        LoginResponseDTO responseDTO = authService.login(requestDTO);
        return  ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }


}
