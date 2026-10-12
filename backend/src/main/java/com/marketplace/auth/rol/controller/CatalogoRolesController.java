package com.marketplace.auth.rol.controller;

import com.marketplace.auth.rol.dto.CatalogoPermisosResponse;
import com.marketplace.auth.rol.dto.CatalogoRolesResponse;
import com.marketplace.auth.rol.service.CatalogoRolesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Catálogo de roles y permisos (SPEC-18, ESC-18.7), con el contenido que define SPEC-11.
 * Lo consumen los demás módulos con el scope {@code roles:leer}.
 */
@RestController
@RequestMapping("/api/v1")
public class CatalogoRolesController {

    private final CatalogoRolesService catalogoRolesService;

    public CatalogoRolesController(CatalogoRolesService catalogoRolesService) {
        this.catalogoRolesService = catalogoRolesService;
    }

    @GetMapping("/roles")
    public CatalogoRolesResponse listarRoles() {
        return catalogoRolesService.listarRoles();
    }

    @GetMapping("/permisos")
    public CatalogoPermisosResponse listarPermisos(@RequestParam(name = "modulo", required = false) String modulo) {
        return catalogoRolesService.listarPermisos(modulo);
    }
}
