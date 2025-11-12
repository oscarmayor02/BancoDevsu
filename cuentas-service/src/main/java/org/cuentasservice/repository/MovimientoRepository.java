package org.cuentasservice.repository;

import org.cuentasservice.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    List<Movimiento> findByNumeroCuentaOrderByFechaDesc(String numeroCuenta);

    Optional<Movimiento> findFirstByNumeroCuentaOrderByFechaDesc(String numeroCuenta);

    @Query("SELECT m FROM Movimiento m WHERE m.numeroCuenta IN " +
            "(SELECT c.numeroCuenta FROM Cuenta c WHERE c.clienteId = :clienteId) " +
            "AND m.fecha BETWEEN :fechaInicio AND :fechaFin " +
            "ORDER BY m.fecha DESC")
    List<Movimiento> findByClienteIdAndFechaBetween(
            @Param("clienteId") String clienteId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );
}