package com.marketplace.auth.rol.service;

import java.util.List;

/**
 * Lo que viaja en los claims {@code roles} y {@code permisos} del accessToken.
 *
 * @param roles    códigos de rol, en orden alfabético
 * @param permisos unión sin duplicados de los permisos de cada rol, en orden alfabético
 */
public record PermisosEfectivos(List<String> roles, List<String> permisos) {
}
