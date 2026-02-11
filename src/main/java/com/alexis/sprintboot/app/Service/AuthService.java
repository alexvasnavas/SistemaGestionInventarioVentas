package com.alexis.sprintboot.app.Service;

import com.alexis.sprintboot.app.DTO.auth.LoginRequest;
import com.alexis.sprintboot.app.DTO.auth.LoginResponse;
import com.alexis.sprintboot.app.DTO.auth.RegisterRequest;
import com.alexis.sprintboot.app.Model.Rol;
import com.alexis.sprintboot.app.Model.Usuario;
import com.alexis.sprintboot.app.Repository.UsuarioRepository;
import com.alexis.sprintboot.app.Security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Registrar un nuevo usuario
     */
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        // Validar que el email no esté registrado
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setRol(Rol.USER); // Por defecto, rol USER
        usuario.setActivo(true);

        // Guardar usuario
        usuarioRepository.save(usuario);

        // Generar token JWT
        String jwtToken = jwtService.generateToken(usuario);

        // Construir respuesta
        return LoginResponse.builder()
                .token(jwtToken)
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .rol(usuario.getRol().name())
                .build();
    }

    /**
     * Registrar un nuevo administrador (solo para uso interno)
     */
    @Transactional
    public LoginResponse registerAdmin(RegisterRequest request) {
        // Validar que el email no esté registrado
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Crear nuevo administrador
        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setRol(Rol.ADMIN);
        usuario.setActivo(true);

        // Guardar usuario
        usuarioRepository.save(usuario);

        // Generar token JWT
        String jwtToken = jwtService.generateToken(usuario);

        // Construir respuesta
        return LoginResponse.builder()
                .token(jwtToken)
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .rol(usuario.getRol().name())
                .build();
    }

    /**
     * Iniciar sesión
     */
    public LoginResponse login(LoginRequest request) {
        // Autenticar usuario
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Buscar usuario
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Verificar si el usuario está activo
        if (!usuario.getActivo()) {
            throw new IllegalStateException("El usuario está desactivado");
        }

        // Generar token JWT
        String jwtToken = jwtService.generateToken(usuario);

        // Construir respuesta
        return LoginResponse.builder()
                .token(jwtToken)
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .rol(usuario.getRol().name())
                .build();
    }

    /**
     * Obtener perfil de usuario
     */
    @Transactional(readOnly = true)
    public LoginResponse getProfile(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return LoginResponse.builder()
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .rol(usuario.getRol().name())
                .build();
    }

    /**
     * Cambiar contraseña
     */
    @Transactional
    public void changePassword(String email, String oldPassword, String newPassword) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Verificar contraseña actual
        if (!passwordEncoder.matches(oldPassword, usuario.getPassword())) {
            throw new IllegalArgumentException("Contraseña actual incorrecta");
        }

        // Actualizar contraseña
        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);
    }

    /**
     * Actualizar perfil
     */
    @Transactional
    public LoginResponse updateProfile(String email, String nombre, String apellido) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (nombre != null && !nombre.trim().isEmpty()) {
            usuario.setNombre(nombre);
        }

        if (apellido != null && !apellido.trim().isEmpty()) {
            usuario.setApellido(apellido);
        }

        usuarioRepository.save(usuario);

        return LoginResponse.builder()
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .rol(usuario.getRol().name())
                .build();
    }

    /**
     * Desactivar usuario (Admin)
     */
    @Transactional
    public void deactivateUser(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    /**
     * Activar usuario (Admin)
     */
    @Transactional
    public void activateUser(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }

    /**
     * Obtener todos los usuarios (Admin)
     */
    @Transactional(readOnly = true)
    public List<LoginResponse> getAllUsers() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuario -> LoginResponse.builder()
                        .email(usuario.getEmail())
                        .nombre(usuario.getNombre())
                        .apellido(usuario.getApellido())
                        .rol(usuario.getRol().name())
                        .activo(usuario.getActivo())
                        .build())
                .collect(Collectors.toList());
    }
}