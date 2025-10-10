package com.example.parcialdbp.service;

import com.example.parcialdbp.clases.Rol;
import com.example.parcialdbp.clases.User;
import com.example.parcialdbp.dto.request.UserRequestDTO;
import com.example.parcialdbp.dto.response.UserResponseDTO;
import com.example.parcialdbp.repositorios.UserRepository;
import com.example.parcialdbp.seguridad.JwtService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Transactional
    public UserResponseDTO registrar(UserRequestDTO userRequestDTO) {

        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new UnknownError("Ese email ya está registrado");
        }

        //Inicializar valores de estudiante con valores en DTO
        User user= User.builder()
                .email(userRequestDTO.getEmail())
                .password(passwordEncoder.encode(userRequestDTO.getPassword()))
                .rol(Rol.ROLE_READER)
                .build();

        //Guardar estudiante en BD
        User saved = userRepository.save(user);

        UserResponseDTO respuesta =  modelMapper.map(saved, UserResponseDTO.class);
        return  respuesta;
    }



}
