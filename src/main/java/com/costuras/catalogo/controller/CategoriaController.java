package com.costuras.catalogo.controller;

import com.costuras.catalogo.dto.CategoriaRequest;
import com.costuras.catalogo.dto.CategoriaResponse;
import com.costuras.catalogo.service.CatalogoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/catalogo/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CatalogoService catalogoService;

    /** GET /catalogo/categorias */
    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listar() {
        return ResponseEntity.ok(catalogoService.listarCategorias());
    }

    /** POST /catalogo/categorias — solo ADMIN */
    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@Valid @RequestBody CategoriaRequest request) {
        CategoriaResponse categoria = catalogoService.crearCategoria(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensaje", "Categoría creada correctamente", "categoria", categoria));
    }

    /** PUT /catalogo/categorias/{id} — solo ADMIN */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> editar(
            @PathVariable Integer id,
            @Valid @RequestBody CategoriaRequest request
    ) {
        CategoriaResponse categoria = catalogoService.editarCategoria(id, request);
        return ResponseEntity.ok(Map.of("mensaje", "Categoría actualizada correctamente", "categoria", categoria));
    }
}
