package com.momentum.habittracker.Service;

import com.momentum.habittracker.DTO.AuthResponseDTO;
import com.momentum.habittracker.DTO.UserLoginDTO;
import com.momentum.habittracker.DTO.UserRegisterDTO;
import com.momentum.habittracker.DTO.UserResponseDTO;
import com.momentum.habittracker.Entities.User;
import com.momentum.habittracker.Exception.ResourceNotFoundException;
import com.momentum.habittracker.Exception.UserAlreadyExistsException;
import com.momentum.habittracker.Repository.UserRepository;
import com.momentum.habittracker.Security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service managing user identity operations: registration, authentication, and profile lookup.
 *
 * Purpose:
 * Contains the core business logic for user management:
 * 1. Registration: Enforces unique username and email constraints, hashes passwords via BCrypt,
 *    persists the User entity, and generates an initial JWT token.
 * 2. Login: Authenticates credentials against the database via AuthenticationManager and issues a JWT.
 * 3. Profile: Resolves public user details safely without exposing sensitive credential hashes.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    /**
     * Constructs AuthService with all required security and persistence dependencies.
     */
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Registers a new user, hashes their password, and generates an initial JWT token.
     *
     * @param dto validated user registration data containing username, email, and raw password
     * @return AuthResponseDTO containing the issued JWT token and basic user details
     * @throws UserAlreadyExistsException if the username or email is already in use
     */
    @Transactional
    public AuthResponseDTO register(UserRegisterDTO dto) {
        // Step 1: Check for duplicate username
        if (userRepository.existsByUsername(dto.username())) {
            throw new UserAlreadyExistsException("Username is already taken: " + dto.username());
        }

        // Step 2: Check for duplicate email
        if (userRepository.existsByEmail(dto.email())) {
            throw new UserAlreadyExistsException("Email is already registered: " + dto.email());
        }

        // Step 3: Hash raw password and construct User entity with default USER role
        User user = User.builder()
                .username(dto.username())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .role(User.Role.USER)
                .build();

        // Step 4: Persist user to database
        User savedUser = userRepository.save(user);

        // Step 5: Issue a signed JWT token
        String token = jwtUtils.generateToken(savedUser.getUsername());

        return new AuthResponseDTO(token, savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
    }

    /**
     * Authenticates existing user credentials and generates a signed JWT token.
     *
     * @param dto the login payload containing username/email and raw password
     * @return AuthResponseDTO containing the issued JWT token and basic user details
     * @throws BadCredentialsException if username/email does not exist or password is wrong
     */
    public AuthResponseDTO login(UserLoginDTO dto) {
        // Step 1: Look up user by username or email to resolve their canonical username
        User user = userRepository.findByUsername(dto.usernameOrEmail())
                .or(() -> userRepository.findByEmail(dto.usernameOrEmail()))
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        // Step 2: Authenticate via Spring Security's AuthenticationManager (checks BCrypt hash)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), dto.password())
        );

        // Step 3: Issue a signed JWT token upon successful authentication
        String token = jwtUtils.generateToken(user.getUsername());

        return new AuthResponseDTO(token, user.getId(), user.getUsername(), user.getEmail());
    }

    /**
     * Fetches public profile details for the currently authenticated user.
     *
     * @param username the authenticated username extracted from the security context
     * @return UserResponseDTO containing user profile data without sensitive credentials
     * @throws ResourceNotFoundException if user record is missing
     */
    @Transactional(readOnly = true)
    public UserResponseDTO getCurrentUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
