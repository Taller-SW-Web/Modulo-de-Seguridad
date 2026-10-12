package com.marketplace.auth.comun.error;

import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

/**
 * Traduce las excepciones a errores RFC 7807 con el código del catálogo.
 */
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(ErrorApi.class)
    public ResponseEntity<ProblemDetail> manejarErrorApi(ErrorApi error) {
        return responder(error);
    }

    /** Falta un campo obligatorio del cuerpo (@NotNull, etc.). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> manejarValidacion(MethodArgumentNotValidException ex) {
        var campo = ex.getBindingResult().getFieldErrors().stream().findFirst();
        return responder(ErrorApi.validacion(
                campo.map(f -> f.getField()).orElse("cuerpo"),
                campo.map(f -> f.getDefaultMessage()).orElse("No es válido.")));
    }

    /** El cuerpo no es JSON válido o un valor no pertenece a un catálogo cerrado (p. ej. rol "BUYER"). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> manejarCuerpoIlegible(HttpMessageNotReadableException ex) {
        if (ex.getCause() instanceof InvalidFormatException formato && !formato.getPath().isEmpty()) {
            String campo = formato.getPath().get(formato.getPath().size() - 1).getFieldName();
            return responder(ErrorApi.validacion(campo, "No pertenece al catálogo."));
        }
        return responder(ErrorApi.validacion("cuerpo", "No es un JSON válido."));
    }

    /** Un parámetro de ruta no tiene el tipo esperado (UUID mal formado, rol fuera del catálogo). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> manejarParametro(MethodArgumentTypeMismatchException ex) {
        return responder(ErrorApi.validacion(ex.getName(), "No es un valor válido."));
    }

    private ResponseEntity<ProblemDetail> responder(ErrorApi error) {
        return ResponseEntity.status(error.getEstado())
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(error.aProblemDetail());
    }
}
