package org.cuentasservice.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimiento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(name = "numero_cuenta", nullable = false)
    private String numeroCuenta;

    @Column(nullable = false)
    private String tipo;

    @Column(name = "saldo_inicial", nullable = false)
    private BigDecimal saldoInicial;

    @Column(nullable = false)
    private Boolean estado;

    @Column(nullable = false)
    private BigDecimal movimiento;

    @Column(name = "saldo_disponible", nullable = false)
    private BigDecimal saldoDisponible;

    @Transient
    private String clienteNombre;
}