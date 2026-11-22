package com.bazaarehunar.service;

import com.bazaarehunar.dto.AuthResponse;
import com.bazaarehunar.dto.LoginRequest;
import com.bazaarehunar.dto.RegisterRequest;
import com.bazaarehunar.model.AppUser;
import com.bazaarehunar.model.Role;
import com.bazaarehunar.repository.UserRepository;
import com.bazaarehunar.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final StoreSettingsService storeSettings;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       StoreSettingsService storeSettings) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.storeSettings = storeSettings;
    }

    public AuthResponse register(RegisterRequest request) {
        if (!storeSettings.getBoolean("allowRegistration")) {
            throw new IllegalArgumentException("New registrations are currently closed");
        }
        String email = request.getEmail().trim().toLowerCase();

        Role role = request.getRole() == null ? Role.BUYER : request.getRole();

        if (role == Role.ADMIN) {
            throw new IllegalArgumentException("Admin accounts cannot be created from public registration");
        }
        if (role == Role.SELLER && !storeSettings.getBoolean("allowSellerSignup")) {
            throw new IllegalArgumentException("Seller sign-ups are currently closed");
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        AppUser user = new AppUser(
                request.getName().trim(),
                email,
                passwordEncoder.encode(request.getPassword()),
                role
        );
        user = userRepository.save(user);

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        AppUser user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
