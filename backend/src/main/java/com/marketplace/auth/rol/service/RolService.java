package com.marketplace.auth.rol.service;

import com.marketplace.auth.auditoria.entity.AuditoriaSeguridad.ActorTipo;
import com.marketplace.auth.auditoria.entity.AuditoriaSeguridad.Resultado;
import com.marketplace.auth.auditoria.service.AuditoriaService;
import com.marketplace.auth.comun.error.ErrorApi;
import com.marketplace.auth.integracion.service.PublicadorEventos;
import com.marketplace.auth.rol.entity.CodigoRol;
import com.marketplace.auth.rol.entity.UsuarioRol;
import com.marketplace.auth.rol.entity.UsuarioRolId;
import com.marketplace.auth.rol.repository.UsuarioRolRepository;
import com.marketplace.auth.rol.repository.UsuarioRolRepository.CuentaResumen;
import com.marketplace.auth.sesion.repository.TokenRefrescoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Asignación y revocación de roles, de uno en uno (SPEC-11).
 *
 * <p>Todo cambio efectivo ocurre en una sola transacción: la fila de usuario_rol,
 * la revocación de sesiones (RF-11.5), el registro de auditoría (RF-11.8, crítico)
 * y el evento en el outbox (RF-11.6). Si cualquiera falla, no queda nada a medias.
 */
@Service
public class RolService {

    static final String EVENTO_ROLES_CAMBIADOS = "usuario.roles_cambiados";
    static final String MENSAJE_ULTIMO_ADMIN = "No se puede eliminar el único administrador del sistema";
    static final String MENSAJE_AUTO_REVOCACION = "Un administrador no puede quitarse a sí mismo el rol ADMIN_SISTEMA";

    private final UsuarioRolRepository usuarioRolRepository;
    private final TokenRefrescoRepository tokenRefrescoRepository;
    private final AuditoriaService auditoriaService;
    private final PublicadorEventos publicadorEventos;

    public RolService(UsuarioRolRepository usuarioRolRepository,
                      TokenRefrescoRepository tokenRefrescoRepository,
                      AuditoriaService auditoriaService,
                      PublicadorEventos publicadorEventos) {
        this.usuarioRolRepository = usuarioRolRepository;
        this.tokenRefrescoRepository = tokenRefrescoRepository;
        this.auditoriaService = auditoriaService;
        this.publicadorEventos = publicadorEventos;
    }

    /**
     * Asigna un rol (ESC-11.1). Asignar un rol que ya tiene no cambia nada ni
     * revoca sesiones: responde el mismo estado.
     */
    @Transactional
    public CuentaConRoles asignar(UUID usuarioId, CodigoRol rol, Actor actor) {
        CuentaResumen cuenta = buscarCuenta(usuarioId);

        UsuarioRolId id = new UsuarioRolId(usuarioId, rol);
        if (!usuarioRolRepository.existsById(id)) {
            usuarioRolRepository.save(new UsuarioRol(usuarioId, rol));
            registrarCambio("ROL_ASIGNADO", usuarioId, rol, actor);
        }
        return aRespuesta(cuenta, rolesActuales(usuarioId));
    }

    /**
     * Revoca un rol (ESC-11.6). Protege al último ADMIN_SISTEMA activo y a quien
     * intenta quitarse su propio rol de administrador (RF-11.7, ESC-11.5).
     * Revocar un rol que no tiene no cambia nada.
     */
    @Transactional
    public void revocar(UUID usuarioId, CodigoRol rol, Actor actor) {
        CuentaResumen cuenta = buscarCuenta(usuarioId);

        UsuarioRolId id = new UsuarioRolId(usuarioId, rol);
        if (!usuarioRolRepository.existsById(id)) {
            return;
        }
        if (rol == CodigoRol.ADMIN_SISTEMA) {
            protegerAdministrador(cuenta, actor);
        }
        usuarioRolRepository.deleteById(id);
        registrarCambio("ROL_REVOCADO", usuarioId, rol, actor);
    }

    // ── Reglas ─────────────────────────────────────────────────

    /**
     * RF-11.7: nadie se quita a sí mismo ADMIN_SISTEMA, y nadie se lo quita al
     * último activo. El bloqueo de filas serializa dos revocaciones simultáneas.
     */
    private void protegerAdministrador(CuentaResumen cuenta, Actor actor) {
        usuarioRolRepository.bloquearAsignacionesAdmin();
        boolean objetivoActivo = "ACTIVO".equals(cuenta.getEstado());
        if (objetivoActivo && usuarioRolRepository.countAdminsActivos() <= 1) {
            throw ErrorApi.administradorProtegido(MENSAJE_ULTIMO_ADMIN);
        }
        if (cuenta.getId().equals(actor.id())) {
            throw ErrorApi.administradorProtegido(MENSAJE_AUTO_REVOCACION);
        }
    }

    /**
     * Efectos de un cambio de roles: sesiones revocadas, auditoría y evento con la
     * lista completa resultante (no el delta: así dos eventos desordenados no
     * dejan al consumidor con una suma incoherente).
     */
    private void registrarCambio(String accion, UUID usuarioId, CodigoRol rol, Actor actor) {
        usuarioRolRepository.flush();
        int sesionesRevocadas = tokenRefrescoRepository.revocarTodosPorUsuarioId(usuarioId);

        auditoriaService.registrar(accion, Resultado.EXITO, ActorTipo.USUARIO,
                actor.id().toString(), usuarioId, actor.ip(), actor.agente(),
                Map.of("rol", rol.name(), "sesionesRevocadas", sesionesRevocadas));

        publicadorEventos.publicar(EVENTO_ROLES_CAMBIADOS, usuarioId,
                Map.of("roles", rolesActuales(usuarioId)));
    }

    private CuentaResumen buscarCuenta(UUID usuarioId) {
        return usuarioRolRepository.findCuenta(usuarioId).orElseThrow(ErrorApi::noEncontrado);
    }

    private List<String> rolesActuales(UUID usuarioId) {
        return usuarioRolRepository.findRolesByUsuarioId(usuarioId).stream()
                .map(Enum::name)
                .sorted()
                .toList();
    }

    private static CuentaConRoles aRespuesta(CuentaResumen cuenta, List<String> roles) {
        return new CuentaConRoles(cuenta.getId(), cuenta.getNombres(), cuenta.getApellidos(),
                cuenta.getCorreo(), Boolean.TRUE.equals(cuenta.getCorreoVerificado()),
                cuenta.getEstado(), roles);
    }
}
