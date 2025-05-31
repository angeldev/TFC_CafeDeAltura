package proyecto.tfc.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import proyecto.tfc.utils.JwtUtil;

/**
 * Controlador REST para autenticación de usuarios.
 * Proporciona el endpoint de login para obtener un token JWT.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Autentica al usuario y devuelve un token JWT si las credenciales son válidas.
     *
     * @param body Mapa con las claves "username" y "password"
     * @return 200 OK con el token JWT, 401 si las credenciales son inválidas
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (userDetails == null) {
                throw new org.springframework.security.core.userdetails.UsernameNotFoundException("Usuario no encontrado");
            }
            if ("{noop}".concat(password).equals(userDetails.getPassword()) || userDetails.getPassword().equals("{noop}" + password)) {
                String token = JwtUtil.generateToken(userDetails);
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
            }
        } catch (AuthenticationException | NullPointerException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }
    }
} 