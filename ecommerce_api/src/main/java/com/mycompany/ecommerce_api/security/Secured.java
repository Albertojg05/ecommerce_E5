package com.mycompany.ecommerce_api.security;

import jakarta.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para marcar recursos REST que requieren autenticación JWT.
 * Usar en clases o métodos que necesitan token válido.
 *
 * Ejemplo de uso:
 * <pre>
 * {@literal @}Secured
 * {@literal @}GET
 * public Response getProtectedResource() { ... }
 * </pre>
 *
  * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Secured {
}
