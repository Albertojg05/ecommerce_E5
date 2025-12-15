package com.mycompany.ecommerce_api.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.mycompany.ecommerce_api.dominio.enums.RolUsuario;
import java.util.Date;

/**
 * Utilidad para generación y validación de tokens JWT usando auth0/java-jwt.
 * Implementa autenticación stateless para la API REST.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
public class JwtUtil {

    // Clave secreta para firmar tokens (en producción usar variable de entorno)
    private static final String SECRET = System.getenv("JWT_SECRET") != null
            ? System.getenv("JWT_SECRET")
            : "ecommerce_E5_jwt_secret_key_2024_segura";

    private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET);

    // Tiempo de expiración: 24 horas en milisegundos
    private static final long EXPIRATION_TIME = 86400000;

    // Issuer del token
    private static final String ISSUER = "ecommerce_e5";

    /**
     * Genera un token JWT para un usuario.
     *
     * @param userId El ID del usuario
     * @param email El correo del usuario (subject)
     * @param rol El rol del usuario (enum RolUsuario)
     * @return String con el token JWT
     */
    public static String generarToken(Long userId, String email, RolUsuario rol) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + EXPIRATION_TIME);

        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(email)
                .withClaim("userId", userId)
                .withClaim("email", email)
                .withClaim("rol", rol.name())
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .sign(ALGORITHM);
    }

    /**
     * Valida el token y retorna el DecodedJWT si es válido.
     *
     * @param token El token JWT recibido
     * @return DecodedJWT objeto con los datos del usuario
     * @throws JWTVerificationException Si el token es inválido o expiró
     */
    public static DecodedJWT validarToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(ALGORITHM)
                .withIssuer(ISSUER)
                .build();
        return verifier.verify(token);
    }

    /**
     * Obtiene el userId del token.
     *
     * @param token El token JWT
     * @return El ID del usuario
     */
    public static Long obtenerUserId(String token) {
        return validarToken(token).getClaim("userId").asLong();
    }

    /**
     * Obtiene el rol del token como enum RolUsuario.
     *
     * @param token El token JWT
     * @return El rol del usuario como RolUsuario
     */
    public static RolUsuario obtenerRol(String token) {
        String rolStr = validarToken(token).getClaim("rol").asString();
        return RolUsuario.valueOf(rolStr);
    }

    /**
     * Obtiene el email (subject) del token.
     *
     * @param token El token JWT
     * @return El email del usuario
     */
    public static String obtenerEmail(String token) {
        return validarToken(token).getSubject();
    }
}
