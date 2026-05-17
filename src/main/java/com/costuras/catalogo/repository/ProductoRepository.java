package com.costuras.catalogo.repository;

import com.costuras.catalogo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByActivoTrue();
    List<Producto> findByCategoriaIdAndActivoTrue(Integer categoriaId);
    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
}
