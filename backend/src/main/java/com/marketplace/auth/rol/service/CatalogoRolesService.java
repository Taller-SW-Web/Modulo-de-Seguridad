package com.marketplace.auth.rol.service;

import com.marketplace.auth.rol.dto.CatalogoPermisosResponse;
import com.marketplace.auth.rol.dto.CatalogoPermisosResponse.PermisoResponse;
import com.marketplace.auth.rol.dto.CatalogoRolesResponse;
import com.marketplace.auth.rol.dto.CatalogoRolesResponse.RolResponse;
import com.marketplace.auth.rol.entity.Permiso;
import com.marketplace.auth.rol.entity.Rol;
import com.marketplace.auth.rol.repository.PermisoRepository;
import com.marketplace.auth.rol.repository.RolRepository;
import com.marketplace.auth.rol.repository.RolRepository.RolPermisoFila;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Catálogo de roles y permisos que publica SPEC-18 (GET /roles y GET /permisos).
 * Los módulos lo consultan en vez de codificar los roles a mano.
 */
@Service
public class CatalogoRolesService {

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;

    public CatalogoRolesService(RolRepository rolRepository, PermisoRepository permisoRepository) {
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
    }

    @Transactional(readOnly = true)
    public CatalogoRolesResponse listarRoles() {
        Map<String, List<String>> permisosPorRol = rolRepository.findParesRolPermiso().stream()
                .collect(Collectors.groupingBy(RolPermisoFila::getRol,
                        Collectors.mapping(RolPermisoFila::getPermiso, Collectors.toList())));

        List<RolResponse> roles = rolRepository.findAll().stream()
                .sorted(Comparator.comparing(Rol::getCodigo))
                .map(rol -> new RolResponse(rol.getCodigo().name(), rol.getNombre(), rol.getDescripcion(),
                        permisosPorRol.getOrDefault(rol.getCodigo().name(), List.of())))
                .toList();
        return new CatalogoRolesResponse(roles);
    }

    @Transactional(readOnly = true)
    public CatalogoPermisosResponse listarPermisos(String modulo) {
        List<Permiso> permisos = modulo == null || modulo.isBlank()
                ? permisoRepository.findAllByOrderByCodigoAsc()
                : permisoRepository.findByModuloOrderByCodigoAsc(modulo);
        return new CatalogoPermisosResponse(permisos.stream()
                .map(p -> new PermisoResponse(p.getCodigo(), p.getModulo(), p.getDescripcion()))
                .toList());
    }
}
