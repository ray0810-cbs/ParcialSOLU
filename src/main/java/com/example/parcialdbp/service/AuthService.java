package com.example.parcialdbp.service;

import com.example.parcialdbp.clases.Rol;
import com.example.parcialdbp.clases.UserClass;
import com.example.parcialdbp.dto.request.LoginRequestDTO;
import com.example.parcialdbp.dto.request.UserRequestDTO;
import com.example.parcialdbp.dto.response.LoginResponseDTO;
import com.example.parcialdbp.dto.response.UserResponseDTO;
import com.example.parcialdbp.repositorios.UserRepository;
import com.example.parcialdbp.seguridad.JwtService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
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

        //Inicializar valores de User con valores en DTO
        UserClass user= UserClass.builder()
                .username(userRequestDTO.getUsername())
                .email(userRequestDTO.getEmail())
                .password(passwordEncoder.encode(userRequestDTO.getPassword()))
                .rol(Rol.ROLE_READER)
                .build();

        //Guardar User en BD
        UserClass saved = userRepository.save(user);

        UserResponseDTO respuesta =  modelMapper.map(saved, UserResponseDTO.class);
        return  respuesta;
    }

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        String password = null;
        String rol = null;
        UserClass user= userRepository.findByUsername(loginRequestDTO.getUsername()).orElse(null);
        if (user != null){
            password = user.getPassword();
            rol= user.getRol().name();
        } else{
            throw new UnknownError("No hay usuario registrado con ese username");
        }
        //Chequea si la contraseña que ingresamos es valida para el usuario
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), password)){
            //Cambiar luego error
            throw new UnknownError("Contraseña incorrecta");
        }
        // Generar token
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequestDTO.getUsername());
        String token = jwtService.generateToken(userDetails, rol);
        return new LoginResponseDTO(token, jwtService.getExpirationTime());
    }




}
