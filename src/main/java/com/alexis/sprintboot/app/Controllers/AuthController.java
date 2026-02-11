package com.alexis.sprintboot.app.Controllers;

import com.alexis.sprintboot.app.DTO.auth.LoginRequest;
import com.alexis.sprintboot.app.DTO.auth.LoginResponse;
import com.alexis.sprintboot.app.DTO.auth.RegisterRequest;
import com.alexis.sprintboot.app.Service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/auth/register
     * Registrar un nuevo usuario
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            LoginResponse response = authService.register(request);

            Map<String, Object> success = new HashMap<>();
            success.put("success", true);
            success.put("message", "Usuario registrado exitosamente");
            success.put("data", response);

            return new ResponseEntity<>(success, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * POST /api/auth/login
     * Iniciar sesión
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);

            Map<String, Object> success = new HashMap<>();
            success.put("success", true);
            success.put("message", "Login exitoso");
            success.put("data", response);

            return ResponseEntity.ok(success);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Credenciales inválidas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    /**
     * GET /api/auth/profile
     * Obtener perfil del usuario autenticado
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            LoginResponse profile = authService.getProfile(userDetails.getUsername());

            Map<String, Object> success = new HashMap<>();
            success.put("success", true);
            success.put("data", profile);

            return ResponseEntity.ok(success);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * PUT /api/auth/profile
     * Actualizar perfil de usuario
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido) {
        try {
            LoginResponse updated = authService.updateProfile(userDetails.getUsername(), nombre, apellido);

            Map<String, Object> success = new HashMap<>();
            success.put("success", true);
            success.put("message", "Perfil actualizado exitosamente");
            success.put("data", updated);

            return ResponseEntity.ok(success);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * POST /api/auth/change-password
     * Cambiar contraseña
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        try {
            authService.changePassword(userDetails.getUsername(), oldPassword, newPassword);

            Map<String, Object> success = new HashMap<>();
            success.put("success", true);
            success.put("message", "Contraseña actualizada exitosamente");

            return ResponseEntity.ok(success);

        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * POST /api/auth/logout
     * Cerrar sesión (cliente debe eliminar el token)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Map<String, Object> success = new HashMap<>();
        success.put("success", true);
        success.put("message", "Sesión cerrada exitosamente");
        return ResponseEntity.ok(success);
    }
}