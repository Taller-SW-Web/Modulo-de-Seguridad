package com.marketplace.auth.comun.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Error de la API con su código estable del catálogo (specs/catalogo-errores.md).
 * Se responde como RFC 7807: los clientes ramifican por {@code code}, no por {@code detail}.
 */
public class ErrorApi extends RuntimeException {

    private static final String BASE_TIPO = "https://g7.unmsm.pe/errores/";

    private final HttpStatus estado;
    private final String codigo;
    private final String titulo;
    private final List<Map<String, String>> errores;

    public ErrorApi(HttpStatus estado, String codigo, String titulo, String detalle,
                    List<Map<String, String>> errores) {
        super(detalle != null ? detalle : titulo);
        this.estado = estado;
        this.codigo = codigo;
        this.titulo = titulo;
        this.errores = errores;
    }

    // ── Códigos del catálogo ───────────────────────────────────

    public static ErrorApi validacion(String campo, String mensaje) {
        return new ErrorApi(HttpStatus.BAD_REQUEST, "VALIDACION", "Datos de entrada inválidos", null,
                List.of(Map.of("campo", campo, "mensaje", mensaje)));
    }

    public static ErrorApi tokenInvalido() {
        return new ErrorApi(HttpStatus.UNAUTHORIZED, "TOKEN_INVALIDO", "Token ausente o inválido", null, null);
    }

    public static ErrorApi scopeInsuficiente() {
        return new ErrorApi(HttpStatus.FORBIDDEN, "SCOPE_INSUFICIENTE", "Permisos insuficientes", null, null);
    }

    public static ErrorApi noEncontrado() {
        return new ErrorApi(HttpStatus.NOT_FOUND, "NO_ENCONTRADO", "Usuario no encontrado", null, null);
    }

    public static ErrorApi administradorProtegido(String detalle) {
        return new ErrorApi(HttpStatus.UNPROCESSABLE_ENTITY, "ADMINISTRADOR_PROTEGIDO",
                "Operación no permitida sobre este administrador", detalle, null);
    }

    // ── Conversión ─────────────────────────────────────────────

    public ProblemDetail aProblemDetail() {
        ProblemDetail problema = ProblemDetail.forStatus(estado);
        problema.setType(URI.create(BASE_TIPO + codigo.toLowerCase().replace('_', '-')));
        problema.setTitle(titulo);
        problema.setProperty("code", codigo);
        if (!getMessage().equals(titulo)) {
            problema.setDetail(getMessage());
        }
        if (errores != null) {
            problema.setProperty("errores", errores);
        }
        return problema;
    }

    public HttpStatus getEstado() {
        return estado;
    }

    public String getCodigo() {
        return codigo;
    }
}
