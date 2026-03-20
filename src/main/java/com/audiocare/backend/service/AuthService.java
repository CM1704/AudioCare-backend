package com.audiocare.backend.service;

import com.audiocare.backend.dto.request.LoginRequest;
import com.audiocare.backend.dto.response.LoginResponse;
import com.audiocare.backend.model.Admin;
import com.audiocare.backend.repository.AdminRepository;
import com.audiocare.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassHash())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        String token = jwtService.generateToken(admin);

        return LoginResponse.builder()
                .token(token)
                .adminId(admin.getId())
                .name(admin.getName())
                .lastName1(admin.getLastName1())
                .email(admin.getEmail())
                .isMaster(admin.getIsMaster())
                // Master no tiene fila en admin_permissions (acceso total implícito).
                .permissions(admin.getIsMaster() ? null : admin.getPermissions())
                .build();
    }
}