package com.cashinvoice.order.security;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;

    private static final Map<String, String[]> USERS = Map.of(
        "admin", new String[]{"adminpass", "ROLE_ADMIN"},
        "user1", new String[]{"userpass",  "ROLE_USER"}
    );

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (USERS.containsKey(username) &&
                USERS.get(username)[0].equals(password)) {

            String role  = USERS.get(username)[1];
            String token = jwtUtil.generateToken(username, role);
            return ResponseEntity.ok(Map.of("token", token, "role", role));
        }

        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }
}
