package com.marketplace.auth.rol.service;

import com.marketplace.auth.rol.repository.UsuarioRolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Calcula los permisos efectivos de un usuario (RF-11.4).
 *
 * <p>Es la costura con SPEC-05 y SPEC-06: quien firma o renueva un accessToken
 * llama a {@link #calcular(UUID)} y copia el resultado en los claims. Los demás
 * módulos autorizan con esos claims sin consultar nuestra base de datos.
 */
@Service
public class PermisosEfectivosService {

    private final UsuarioRolRepository usuarioRolRepository;

    public PermisosEfectivosService(UsuarioRolRepository usuarioRolRepository) {
        this.usuarioRolRepository = usuarioRolRepository;
    }

    @Transactional(readOnly = true)
    public PermisosEfectivos calcular(UUID usuarioId) {
        List<String> roles = usuarioRolRepository.findRolesByUsuarioId(usuarioId).stream()
                .map(Enum::name)
                .sorted()
                .toList();
        List<String> permisos = roles.isEmpty()
                ? List.of()
                : usuarioRolRepository.findPermisosEfectivos(usuarioId);
        return new PermisosEfectivos(roles, permisos);
    }
}
