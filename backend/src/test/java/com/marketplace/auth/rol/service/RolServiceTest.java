package com.marketplace.auth.rol.service;

import com.marketplace.auth.auditoria.service.AuditoriaService;
import com.marketplace.auth.comun.error.ErrorApi;
import com.marketplace.auth.integracion.service.PublicadorEventos;
import com.marketplace.auth.rol.entity.CodigoRol;
import com.marketplace.auth.rol.entity.UsuarioRolId;
import com.marketplace.auth.rol.repository.UsuarioRolRepository;
import com.marketplace.auth.rol.repository.UsuarioRolRepository.CuentaResumen;
import com.marketplace.auth.sesion.repository.TokenRefrescoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Reglas de RolService sin base de datos: protección del administrador (RF-11.7),
 * idempotencia y efectos de un cambio de roles (RF-11.5, 11.6, 11.8).
 */
@ExtendWith(MockitoExtension.class)
class RolServiceTest {

    private static final UUID ADMIN = UUID.randomUUID();
    private static final UUID OTRO = UUID.randomUUID();

    @Mock
    private UsuarioRolRepository usuarioRolRepository;
    @Mock
    private TokenRefrescoRepository tokenRefrescoRepository;
    @Mock
    private AuditoriaService auditoriaService;
    @Mock
    private PublicadorEventos publicadorEventos;

    @InjectMocks
    private RolService rolService;

    private final Actor actor = new Actor(ADMIN, "10.0.0.1", "pruebas");

    @BeforeEach
    void cuentas() {
        lenient().when(usuarioRolRepository.findCuenta(ADMIN)).thenReturn(Optional.of(cuenta(ADMIN, "ACTIVO")));
    }

    @Test
    void asignarRevocaSesionesAuditaYPublicaLaListaCompleta() {
        when(usuarioRolRepository.findCuenta(OTRO)).thenReturn(Optional.of(cuenta(OTRO, "ACTIVO")));
        when(usuarioRolRepository.existsById(new UsuarioRolId(OTRO, CodigoRol.GESTOR_COMERCIAL))).thenReturn(false);
        when(usuarioRolRepository.findRolesByUsuarioId(OTRO))
                .thenReturn(List.of(CodigoRol.VENDEDOR, CodigoRol.GESTOR_COMERCIAL));

        CuentaConRoles resultado = rolService.asignar(OTRO, CodigoRol.GESTOR_COMERCIAL, actor);

        assertThat(resultado.roles()).containsExactly("GESTOR_COMERCIAL", "VENDEDOR");
        verify(tokenRefrescoRepository).revocarTodosPorUsuarioId(OTRO);
        verify(auditoriaService).registrar(eq("ROL_ASIGNADO"), any(), any(), eq(ADMIN.toString()), eq(OTRO),
                eq("10.0.0.1"), eq("pruebas"), anyMap());
        verify(publicadorEventos).publicar("usuario.roles_cambiados", OTRO,
                Map.of("roles", List.of("GESTOR_COMERCIAL", "VENDEDOR")));
    }

    @Test
    void asignarUnRolQueYaTieneNoTieneEfectos() {
        when(usuarioRolRepository.findCuenta(OTRO)).thenReturn(Optional.of(cuenta(OTRO, "ACTIVO")));
        when(usuarioRolRepository.existsById(new UsuarioRolId(OTRO, CodigoRol.VENDEDOR))).thenReturn(true);

        rolService.asignar(OTRO, CodigoRol.VENDEDOR, actor);

        verify(usuarioRolRepository, never()).save(any());
        verifyNoInteractions(tokenRefrescoRepository, auditoriaService, publicadorEventos);
    }

    @Test
    void cuentaInexistenteResponde404() {
        when(usuarioRolRepository.findCuenta(OTRO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rolService.asignar(OTRO, CodigoRol.VENDEDOR, actor))
                .isInstanceOf(ErrorApi.class)
                .extracting(e -> ((ErrorApi) e).getCodigo()).isEqualTo("NO_ENCONTRADO");
    }

    @Test
    void elUltimoAdministradorActivoNoPierdeSuRol() {
        when(usuarioRolRepository.existsById(new UsuarioRolId(ADMIN, CodigoRol.ADMIN_SISTEMA))).thenReturn(true);
        when(usuarioRolRepository.countAdminsActivos()).thenReturn(1L);

        assertThatThrownBy(() -> rolService.revocar(ADMIN, CodigoRol.ADMIN_SISTEMA, new Actor(OTRO, null, null)))
                .isInstanceOf(ErrorApi.class)
                .hasMessage(RolService.MENSAJE_ULTIMO_ADMIN);

        verify(usuarioRolRepository).bloquearAsignacionesAdmin();
        verify(usuarioRolRepository, never()).deleteById(any());
        verifyNoInteractions(publicadorEventos);
    }

    @Test
    void nadieSeQuitaASiMismoElRolDeAdministrador() {
        when(usuarioRolRepository.existsById(new UsuarioRolId(ADMIN, CodigoRol.ADMIN_SISTEMA))).thenReturn(true);
        when(usuarioRolRepository.countAdminsActivos()).thenReturn(3L);

        assertThatThrownBy(() -> rolService.revocar(ADMIN, CodigoRol.ADMIN_SISTEMA, actor))
                .isInstanceOf(ErrorApi.class)
                .hasMessage(RolService.MENSAJE_AUTO_REVOCACION);
        verify(usuarioRolRepository, never()).deleteById(any());
    }

    @Test
    void unAdministradorBloqueadoNoCuentaComoUltimoActivo() {
        when(usuarioRolRepository.findCuenta(OTRO)).thenReturn(Optional.of(cuenta(OTRO, "BLOQUEADO")));
        when(usuarioRolRepository.existsById(new UsuarioRolId(OTRO, CodigoRol.ADMIN_SISTEMA))).thenReturn(true);

        rolService.revocar(OTRO, CodigoRol.ADMIN_SISTEMA, actor);

        verify(usuarioRolRepository).deleteById(new UsuarioRolId(OTRO, CodigoRol.ADMIN_SISTEMA));
        verify(tokenRefrescoRepository).revocarTodosPorUsuarioId(OTRO);
    }

    @Test
    void revocarUnRolQueNoTieneNoTieneEfectos() {
        when(usuarioRolRepository.findCuenta(OTRO)).thenReturn(Optional.of(cuenta(OTRO, "ACTIVO")));
        when(usuarioRolRepository.existsById(new UsuarioRolId(OTRO, CodigoRol.GESTOR_DESPACHO))).thenReturn(false);

        rolService.revocar(OTRO, CodigoRol.GESTOR_DESPACHO, actor);

        verify(usuarioRolRepository, never()).deleteById(any());
        verifyNoInteractions(tokenRefrescoRepository, auditoriaService, publicadorEventos);
    }

    private static CuentaResumen cuenta(UUID id, String estado) {
        return new CuentaResumen() {
            public UUID getId() { return id; }
            public String getNombres() { return "Nombre"; }
            public String getApellidos() { return "Apellido"; }
            public String getCorreo() { return "cuenta@ejemplo.com"; }
            public Boolean getCorreoVerificado() { return true; }
            public String getEstado() { return estado; }
        };
    }
}
