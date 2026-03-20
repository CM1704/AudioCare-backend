package com.audiocare.backend.security;

import com.audiocare.backend.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return adminRepository.findByEmail(email)
                .map(admin -> User.builder()
                        .username(admin.getEmail())
                        .password(admin.getPassHash())
                        // Rol según isMaster. El control granular de permisos
                        // se hace en la capa de servicio, no en Spring Security.
                        .roles(admin.getIsMaster() ? "MASTER" : "ADMIN")
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Admin no encontrado: " + email));
    }
}
