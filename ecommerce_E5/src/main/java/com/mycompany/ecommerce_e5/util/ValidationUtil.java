/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_e5.util;

import java.util.regex.Pattern;

/**
 * Clase utilitaria con metodos de validacion comunes.
 * Valida correos electronicos, campos vacios, numeros positivos, etc.
 * Se usa en los BO para validar datos antes de guardarlos.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
public class ValidationUtil {
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    public static boolean isValidEmail(String email) {
        return isNotEmpty(email) && EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isPositive(double number) {
        return number > 0;
    }
    
    public static boolean isNonNegative(int number) {
        return number >= 0;
    }
    
    public static boolean hasMinLength(String str, int minLength) {
        return isNotEmpty(str) && str.length() >= minLength;
    }
    
    public static Integer parseIntParameter(String param) throws IllegalArgumentException {
        if (param == null || param.trim().isEmpty()) {
            throw new IllegalArgumentException("Parámetro requerido");
        }
        try {
            return Integer.parseInt(param.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Parámetro numérico inválido: " + param);
        }
    }
    
    public static Double parseDoubleParameter(String param) throws IllegalArgumentException {
        if (param == null || param.trim().isEmpty()) {
            throw new IllegalArgumentException("Parámetro requerido");
        }
        try {
            return Double.parseDouble(param.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Parámetro decimal inválido: " + param);
        }
    }
    
    private ValidationUtil() {}
}
