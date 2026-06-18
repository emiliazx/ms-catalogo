package com.costuras.catalogo.controller;

import com.costuras.catalogo.dto.CategoriaRequest;
import com.costuras.catalogo.dto.CategoriaResponse;
import com.costuras.catalogo.service.CatalogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Categorías", description = "Gestión de categorías del catálogo")
public class CategoriaController {

    private final CatalogoService catalogoService;

    @Operation(summary = "Listar categorías", description = "Retorna todas las categorías disponibles.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    })
    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listar() {
        return ResponseEntity.ok(catalogoService.listarCategorias());
    }

    @Operation(summary = "Crear categoría",
               description = "Crea una nueva categoría. El nombre no puede estar duplicado. Requiere rol ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Categoría creada correctamente"),
        @ApiResponse(responseCode = "400", description = "Nombre duplicado o datos inválidos"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@Valid @RequestBody CategoriaRequest request) {
        CategoriaResponse categoria = catalogoService.crearCategoria(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensaje", "Categoría creada correctamente", "categoria", categoria));
    }

    @Operation(summary = "Editar categoría",
               description = "Actualiza el nombre y descripción de una categoría existente. Requiere rol ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría actualizada correctamente"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> editar(
            @PathVariable Integer id,
            @Valid @RequestBody CategoriaRequest request) {
        CategoriaResponse categoria = catalogoService.editarCategoria(id, request);
        return ResponseEntity.ok(Map.of("mensaje", "Categoría actualizada correctamente", "categoria", categoria));
    }
}