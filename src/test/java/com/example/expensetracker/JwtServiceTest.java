package com.example.expensetracker;

import static org.junit.jupiter.api.Assertions.*;

import com.example.expensetracker.security.JwtService;
import org.junit.jupiter.api.Test;

class JwtServiceTest {
    private static final String SECRET = "test-secret-key-that-is-at-least-32-characters-long";

    @Test
    void generatedTokenIsValidAndContainsUsername() {
        JwtService jwt = new JwtService(SECRET, 60_000);
        String token = jwt.generateToken("alice");
        assertTrue(jwt.isValid(token));
        assertEquals("alice", jwt.extractUsername(token));
    }

    @Test
    void tamperedTokenIsRejected() {
        JwtService jwt = new JwtService(SECRET, 60_000);
        String token = jwt.generateToken("alice");
        assertFalse(jwt.isValid(token + "x"));
    }

    @Test
    void expiredTokenIsRejected() {
        JwtService jwt = new JwtService(SECRET, -1000);
        assertFalse(jwt.isValid(jwt.generateToken("alice")));
    }

    @Test
    void tokenSignedWithDifferentKeyIsRejected() {
        JwtService other = new JwtService("another-secret-key-that-is-also-32-chars-long!!", 60_000);
        JwtService jwt = new JwtService(SECRET, 60_000);
        assertFalse(jwt.isValid(other.generateToken("alice")));
    }
}
