/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_api.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

/**
 *
 * @author Alberto Jimenez
 */
public class JwtUtil {

    private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private static final long EXPIRATION_TIME = 28800000;

    /**
     * Genera un token JWT para un usuario.
     *
     * @param correo El correo del usuario (subject)
     * @param rol El rol del usuario (claim adicional)
     * @return String con el token JWT
     */
    public static String generarToken(String correo, String rol) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(correo)
                .claim("rol", rol)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(KEY)
                .compact();
    }

    /**
     * Valida el token y retorna los Claims (datos) si es válido.
     *
     * @param token El token JWT recibido
     * @return Claims objeto con los datos del usuario
     * @throws Exception Si el token es inválido o expiró
     */
    public static Claims validarToken(String token) throws Exception {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
