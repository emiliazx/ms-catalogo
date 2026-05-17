package com.costuras.catalogo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    private String descripcion;
    @NotNull @Positive(message = "El precio debe ser mayor a 0")
    private BigDecimal precio;
    @NotNull @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;
    private Integer idCategoria;
}
