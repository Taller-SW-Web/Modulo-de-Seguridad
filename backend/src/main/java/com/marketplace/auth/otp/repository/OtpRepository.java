package com.marketplace.auth.otp.repository;

import com.marketplace.auth.otp.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

/** Repositorio de códigos OTP. */
@Repository
public interface OtpRepository extends JpaRepository<Otp, UUID> {

    /** Busca los códigos OTP que aún no se han usado por un usuario. */
    List<Otp> findByUsuarioIdAndUsadoFalse(UUID usuarioId);
}